package pl.datingSite.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.DefaultHttpClient;
import org.controlsfx.control.CheckComboBox;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.core.executors.ApacheHttpClient4Executor;
import org.jboss.resteasy.util.GenericType;
import pl.datingSite.enums.*;
import pl.datingSite.model.City;
import pl.datingSite.model.SearchHelper;
import pl.datingSite.model.User;
import pl.datingSite.tools.CSVReader;
import pl.datingSite.tools.DatabaseGenerator;
import pl.datingSite.tools.DistanceCalculator;
import pl.datingSite.tools.ImageNameGenerator;

import javax.ws.rs.core.MediaType;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.*;

public class MainPanelController {

    private Stage stage;
    private AnchorPane mainPanel, loginPanel;
    private User user;
    private EmptyPanelController emptyPanelController;
    private LoginPanelController loginPanelController;
    private String password;

    private ImageNameGenerator imageNameGenerator;

    private List<City> foundedCtiesFrom, foundedCtiesTo;
    private int choosedCityFrom, choosedCityTo;


    @FXML
    private ImageView avatar, currentImage;
    @FXML
    private Label nameAndSurname, friendsCount, notificationCount, messageCount;
    @FXML
    private Button settings, logout, friends, notifications, messages;
    @FXML
    private StackPane friendsCounter, notificationsCounter, messagesCounter;

    /********  Distance Calculator *******/
    @FXML
    private ComboBox from, to;
    @FXML
    private TextField distance;


    /********* Editing Photo ***********/
    @FXML
    private Button prev, next;
    @FXML
    private RadioButton woman, man;
    @FXML
    private TextField ageFrom, ageTo, actualFilename, generatedFilename;
    @FXML
    private ProgressBar progress;
    private ToggleGroup toggleGroup = new ToggleGroup();


    /********* Database Generator *************/
    @FXML
    private TextField quantity;
    @FXML
    private Label quantityError, cityError, cityError2, generateLabel, generateLabel2;
    @FXML
    private CheckBox nearby;
    @FXML
    private ComboBox city, city2;
    private List<City> foundedCties2, foundedCties3;


    /************ Quick Search ***********/
    @FXML
    private ComboBox sex, ageFromSearch, ageToSearch, locationFrom, distanceFrom, heightFrom, heightTo;
    @FXML
    private CheckBox withAvatar, real;
    @FXML
    private CheckComboBox maritalStatus, figure, hairColor, smoking, alcohol, children, lookingFor, religion;
    @FXML
    private VBox resultVBox;
    @FXML
    private Pagination pagination;
    private final int ITEM_PER_PAGE = 6;
    private List<City> foundedCitiesSearch;
    private List<User> users;



    private final String countNotificationUrl = "http://localhost:8090/notification/count?";
    private final String getCityByNameUrl = "http://localhost:8090/city/getByName?";
    private final String generateUrl = "http://localhost:8090/databaseGenerator/generate?";
    private final String generatePrepareUrl = "http://localhost:8090/databaseGenerator/generatePrepare";
    private final String getUsersUrl = "http://localhost:8090/user/getUsers";

    @FXML
    public void initialize() throws Exception {
        woman.setToggleGroup(toggleGroup);
        man.setToggleGroup(toggleGroup);
        prev.setDisable(true);

        quantityError.setVisible(false);
        cityError.setVisible(false);
        cityError2.setVisible(false);
        generateLabel.setVisible(false);
        generateLabel2.setVisible(false);
        city.setDisable(true);
        pagination.setVisible(false);
    }

    public void refresh() throws Exception {
        nameAndSurname.setText(user.getName() + " " + user.getSurname());

        if(user.getAvatar() != null)
            avatar.setImage(new Image(new ByteArrayInputStream(user.getAvatar())));
        else
            avatar.setImage(new Image("images/noFoto.png"));

        DefaultHttpClient client = new DefaultHttpClient();
        Credentials credentials = new UsernamePasswordCredentials(user.getUsername(), password);
        client.getCredentialsProvider().setCredentials(AuthScope.ANY, credentials);
        ApacheHttpClient4Executor executor = new ApacheHttpClient4Executor(client);

        ClientRequest clientRequest = new ClientRequest(countNotificationUrl + "username=" + user.getUsername(), executor);
        Integer notificationQuantity = (Integer)clientRequest.get().getEntity(Integer.class);
        notificationCount.setText(notificationQuantity.toString());

        if(friendsCount.getText().equals("0"))
            friendsCounter.setVisible(false);
        if(notificationCount.getText().equals("0"))
            notificationsCounter.setVisible(false);
        if(messageCount.getText().equals("0"))
            messagesCounter.setVisible(false);

        setSearchData();
    }

    @FXML
    public void calculate() {
        this.choosedCityFrom = from.getSelectionModel().getSelectedIndex();
        this.choosedCityTo = to.getSelectionModel().getSelectedIndex();

        City from = foundedCtiesFrom.get(choosedCityFrom);
        City to = foundedCtiesTo.get(choosedCityTo);

//        distance.setText(from.getName() + ", " + from.getLatitude() + " : " + from.getLongitude()
//                + " " + to.getName() + ", " + to.getLatitude() + " : " + to.getLongitude());
        DistanceCalculator calculator = new DistanceCalculator(from, to);
        distance.setText(String.valueOf(calculator.getDistance()));
    }

    @FXML
    public void typeFrom() throws Exception {
        ClientRequest clientRequest = new ClientRequest(getCityByNameUrl + "name=" + from.getEditor().getText());
        this.foundedCtiesFrom = (List<City>)clientRequest.get().getEntity(new GenericType<List<City>>() {});
        List<String> citiesName = getCityNameList(this.foundedCtiesFrom);

        ObservableList<String> options = FXCollections.observableArrayList(citiesName);
        from.setItems(options);
    }

    @FXML
    public void typeTo() throws Exception {
        ClientRequest clientRequest = new ClientRequest(getCityByNameUrl + "name=" + to.getEditor().getText());
        this.foundedCtiesTo = (List<City>)clientRequest.get().getEntity(new GenericType<List<City>>() {});
        List<String> citiesName = getCityNameList(this.foundedCtiesTo);

        ObservableList<String> options = FXCollections.observableArrayList(citiesName);
        to.setItems(options);
    }

    private List<String> getCityNameList(List<City> cities) {
        List<String> list = new ArrayList<>();
        Iterator<City> iterator = cities.iterator();

        while (iterator.hasNext()) {
            City tmp = iterator.next();
            list.add(tmp.getName() + ", woj. " + tmp.getVoivodeship() + ", powiat " + tmp.getCountry() + ", gmina " + tmp.getCommunity());
        }

        return list;
    }

    @FXML
    public void settings() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("SettingsPanel.fxml"));
        AnchorPane pane = null;
        try {
            pane = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        SettingsPanelController settingsPanelController = loader.getController();
        settingsPanelController.setMainPanel(mainPanel);
        settingsPanelController.setUser(user);
        settingsPanelController.setEmptyPanelController(emptyPanelController);
        settingsPanelController.setLoginPanel(loginPanel);
        settingsPanelController.setLoginPanelController(loginPanelController);
        settingsPanelController.setMainPanelController(this);
        settingsPanelController.setPassword(password);
        settingsPanelController.setStage(stage);
        settingsPanelController.refresh();
        emptyPanelController.setScreen(pane);
    }

    @FXML
    public void logout() {
        loginPanelController.clearTextFields();
        emptyPanelController.setScreen(loginPanel);
    }

    @FXML
    public void friends() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("FriendsPanel.fxml"));
        AnchorPane pane = null;
        try {
            pane = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        FriendsPanelController friendsPanelController = loader.getController();
        friendsPanelController.setMainPanel(mainPanel);
        friendsPanelController.setUser(user);
        friendsPanelController.setEmptyPanelController(emptyPanelController);
        friendsPanelController.setLoginPanel(loginPanel);
        friendsPanelController.setLoginPanelController(loginPanelController);
        friendsPanelController.setMainPanelController(this);
        friendsPanelController.setPassword(password);
        friendsPanelController.setStage(stage);
        friendsPanelController.refresh();
        emptyPanelController.setScreen(pane);

    }

    @FXML
    public void notifications() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("NotificationPanel.fxml"));
        AnchorPane pane = null;
        try {
            pane = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        NotificationPanelController notificationPanelController = loader.getController();
        notificationPanelController.setMainPanel(mainPanel);
        notificationPanelController.setUser(user);
        notificationPanelController.setEmptyPanelController(emptyPanelController);
        notificationPanelController.setLoginPanel(loginPanel);
        notificationPanelController.setLoginPanelController(loginPanelController);
        notificationPanelController.setMainPanelController(this);
        notificationPanelController.setPassword(password);
        notificationPanelController.setStage(stage);
        notificationPanelController.refresh();
        emptyPanelController.setScreen(pane);
    }

    @FXML
    public void messages() {

    }


    /****** Editing Photos ***********/

    @FXML
    public void chooseFolder() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Dating Site");
        File selectedDirectory = directoryChooser.showDialog(stage);

        if(selectedDirectory != null) {
            this.imageNameGenerator = new ImageNameGenerator(selectedDirectory);
            next();
            next.setDisable(true);
        }
    }

    @FXML
    public void prev() {
        if(imageNameGenerator != null) {
            File file = imageNameGenerator.getCurrentImage(false);
            currentImage.setImage(new Image(file.toURI().toString()));
            setProgressBar();
            setImageInfo(file);
        } else
            alert();

    }

    @FXML
    public void next() {
        if(imageNameGenerator != null) {
            imageNameGenerator.setNameCurrentImage(generatedFilename.getText());
            File file = imageNameGenerator.getCurrentImage(true);
            currentImage.setImage(new Image(file.toURI().toString()));
            setImageInfo(file);
            if(toggleGroup.getSelectedToggle() != null)
                toggleGroup.getSelectedToggle().setSelected(false);
            ageFrom.clear();
            ageTo.clear();
            generatedFilename.clear();
            prev.setDisable(false);
            setProgressBar();
            next.setDisable(true);
        } else
            alert();
    }

    private void setProgressBar() {
        double val = (double)imageNameGenerator.getCurrent() / (double)imageNameGenerator.countFiles();
        progress.setProgress(val);
    }

    @FXML
    public void generate() {
        String filename = "";
        if(checkData()) {
            filename += checkSex() + "-" + checkAge();
            generatedFilename.setText(filename);
            next.setDisable(false);
        } else
            generatedFilename.setText("Błędne dane!");


    }

    private boolean checkData() {
        if(checkSex() != null && checkAge() != null)
            return true;
        else
            return false;
    }

    private String checkSex() {
        if(toggleGroup.getSelectedToggle() != null) {
            RadioButton button = (RadioButton) toggleGroup.getSelectedToggle();
            if(button.getText().equals("Kobieta"))
                return "woman";
            else
                return "man";
        } else {
            generatedFilename.setText("Nie podano płci!");
            return null;
        }
    }

    private String checkAge() {
        try {
            if (!ageFrom.getText().isEmpty() && !ageTo.getText().isEmpty()) {
                Integer ageFrom = Integer.valueOf(this.ageFrom.getText());
                Integer ageTo = Integer.valueOf(this.ageTo.getText());

                if((ageTo - ageFrom) <= 0) {
                    this.ageFrom.setText("Wiek od nie może być większy od wieku do");
                    return null;
                }
                return String.valueOf(ageFrom) + "-" + String.valueOf(ageTo);
            } else
                return null;
        } catch (NumberFormatException e) {
            ageFrom.setText("Wiek musi być liczbą!");
            return null;
        }
    }

    private void setImageInfo(File image) {
        actualFilename.setText(image.getName());
    }

    private void alert() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Dating Site");
        alert.setHeaderText(null);
        alert.setContentText("Nie wybrano katalogu zdjęć!");

        Optional<ButtonType> result = alert.showAndWait();
        if((result.get() == ButtonType.OK)){

        }
    }


    /**** Database Genereator **********/

    @FXML
    public void typeQuantity() {
        quantityError.setVisible(false);
    }

    @FXML
    public void checkNearby() {
        if(nearby.isSelected()) {
            city.setDisable(false);
        } else
            city.setDisable(true);
    }

    @FXML
    public void typeCity() throws Exception {
        cityError.setVisible(false);

        ClientRequest clientRequest = new ClientRequest(getCityByNameUrl + "name=" + city.getEditor().getText());
        this.foundedCties2 = (List<City>)clientRequest.get().getEntity(new GenericType<List<City>>() {});
        List<String> citiesName = getCityNameList(this.foundedCties2);

        ObservableList<String> options = FXCollections.observableArrayList(citiesName);
        city.setItems(options);
    }

    @FXML
    public void typeCity2() throws Exception {
        cityError2.setVisible(false);

        ClientRequest clientRequest = new ClientRequest(getCityByNameUrl + "name=" + city2.getEditor().getText());
        this.foundedCties3 = (List<City>)clientRequest.get().getEntity(new GenericType<List<City>>() {});
        List<String> citiesName = getCityNameList(this.foundedCties3);

        ObservableList<String> options = FXCollections.observableArrayList(citiesName);
        city2.setItems(options);
    }

    @FXML
    public void generateDb() throws Exception {
        try {
            if(quantityError.getText().isEmpty()) {
                quantityError.setText("Podaj ilość");
                quantityError.setVisible(true);
            }
            Integer data = Integer.valueOf(quantity.getText());

            if(nearby.isSelected()) {
                if(city.getSelectionModel().getSelectedIndex() >= 0) {
                    City city = foundedCties2.get(this.city.getSelectionModel().getSelectedIndex());
                    ClientRequest clientRequest = new ClientRequest(generateUrl + "quantity=" + data);
                    clientRequest.body(MediaType.APPLICATION_JSON, city);
                    clientRequest.post();
                    generateLabel.setVisible(true);
                } else {
                    cityError.setVisible(true);
                }
            } else {
                ClientRequest clientRequest = new ClientRequest(generateUrl + "quantity=" + data);
                clientRequest.get();
                generateLabel.setVisible(true);
            }

        } catch (NumberFormatException e) {
            quantityError.setText("Ilość musi być liczbą!");
            quantityError.setVisible(true);
        }
    }

    @FXML
    public void generateDb2() throws Exception {
        if(city2.getSelectionModel().getSelectedIndex() >= 0) {
            City city = foundedCties3.get(this.city2.getSelectionModel().getSelectedIndex());
            ClientRequest clientRequest = new ClientRequest(generatePrepareUrl);
            clientRequest.body(MediaType.APPLICATION_JSON, city);
            clientRequest.post();
            generateLabel2.setVisible(true);
        } else {
            cityError2.setVisible(true);
        }
    }


    /****** Quick Search *****/

    private void setSearchData() {
        ObservableList<String> sex = FXCollections.observableArrayList("Kobiety", "Mężczyźni");
        this.sex.setItems(sex);

        List<Integer> list = new ArrayList<>();
        for(int i = 140; i <= 240; i++)
            list.add(i);

        ObservableList<Integer> heights = FXCollections.observableArrayList(list);
        heightFrom.setItems(heights);

        heightTo.setItems(heights);

        ObservableList<String> distance = FXCollections.observableArrayList("5", "10", "20", "35", "50", "75", "100", "150", "200", "250", "300", "450");
        distanceFrom.setItems(distance);

        ObservableList<String> maritalsStatus = FXCollections.observableArrayList(
                "Singiel / Singielka", "Mężatka / Żonaty", "Rozwiedziona / Rozwiedziony", "Wdowa / Wdowiec"
        );
        maritalStatus.getItems().clear();
        maritalStatus.getItems().addAll(maritalsStatus);

        ObservableList<String> figures = FXCollections.observableArrayList(
                "Sczupła", "Normalna", "Puszysta"
        );
        figure.getItems().clear();
        figure.getItems().addAll(figures);

        list.clear();
        for(int i = 18; i <= 90; i++)
            list.add(i);

        ObservableList<Integer> ages = FXCollections.observableArrayList(list);
        ageFromSearch.setItems(ages);
        ageToSearch.setItems(ages);

        ObservableList<String> hairColors = FXCollections.observableArrayList(
                "Czarny", "Brązowy", "Blond", "Kasztanowy", "Rudy", "Szary", "Biały"
        );
        hairColor.getItems().clear();
        hairColor.getItems().addAll(hairColors);

        ObservableList<String> smokings = FXCollections.observableArrayList(
                "Nie palę", "Okazjonalnie", "Palę", "Próbuję rzucić"
        );
        smoking.getItems().clear();
        smoking.getItems().addAll(smokings);

        ObservableList<String> alcohols = FXCollections.observableArrayList(
                "Nie piję", "Okazjonalnie", "Piję"
        );
        alcohol.getItems().clear();
        alcohol.getItems().addAll(alcohols);

        ObservableList<String> childrenList = FXCollections.observableArrayList(
                "Nie mam ale chcę mieć", "Nie mam i nie chcę mieć", "Mam i wystarczy", "Mam ale chcę mieć"
        );
        children.getItems().clear();
        children.getItems().addAll(childrenList);

        ObservableList<String> lookingsFor = FXCollections.observableArrayList(
                "Przyjaźni", "Poważnego związku","Zabawy", "Małżeństwa"
        );
        lookingFor.getItems().clear();
        lookingFor.getItems().addAll(lookingsFor);

        ObservableList<String> religions = FXCollections.observableArrayList(
                "Chrześcijaństwo", "Islam", "Ateizm", "Hinduizm", "Buddyzm", "Judaizm", "Prawosławni", "Protestantyzm", "Świadkowie Jehowy"
        );
        religion.getItems().clear();
        religion.getItems().addAll(religions);
    }


    @FXML
    public void clear() {
        sex.getSelectionModel().select(null);
        ageFromSearch.getSelectionModel().select(null);
        ageToSearch.getSelectionModel().select(null);
        withAvatar.setSelected(false);
        locationFrom.getSelectionModel().select(null);
        distanceFrom.getSelectionModel().select(null);
        maritalStatus.getCheckModel().clearChecks();
        figure.getCheckModel().clearChecks();
        heightFrom.getSelectionModel().select(null);
        heightTo.getSelectionModel().select(null);
        hairColor.getCheckModel().clearChecks();
        smoking.getCheckModel().clearChecks();
        alcohol.getCheckModel().clearChecks();
        children.getCheckModel().clearChecks();
        lookingFor.getCheckModel().clearChecks();
        religion.getCheckModel().clearChecks();
    }

    @FXML
    public void search() {
        SearchHelper searchHelper = new SearchHelper();
        if(sex.getSelectionModel().getSelectedItem() != null)
            searchHelper.setSex(sex.getSelectionModel().getSelectedItem().toString());
        if(ageFromSearch.getSelectionModel().getSelectedItem() !=  null)
            searchHelper.setAgeFrom(Integer.valueOf(ageFromSearch.getSelectionModel().getSelectedItem().toString()));
        if(ageToSearch.getSelectionModel().getSelectedItem() != null)
            searchHelper.setAgeTo(Integer.valueOf(ageToSearch.getSelectionModel().getSelectedItem().toString()));
        searchHelper.setWithAvatar(withAvatar.isSelected());
        searchHelper.setReal(real.isSelected());
        if(locationFrom.getSelectionModel().getSelectedItem() != null)
            searchHelper.setLocation(foundedCitiesSearch.get(locationFrom.getSelectionModel().getSelectedIndex()));
        if(distanceFrom.getSelectionModel().getSelectedItem() != null)
            searchHelper.setDistance(Integer.valueOf(distanceFrom.getSelectionModel().getSelectedItem().toString()));


        if(!maritalStatus.getCheckModel().getCheckedItems().isEmpty()) {
            List<MaritalStatus> maritalStatuses = new ArrayList<>();
            ObservableList<Integer> checkedItems = maritalStatus.getCheckModel().getCheckedIndices();
            Iterator<Integer> iterator = checkedItems.iterator();

            while (iterator.hasNext())
                maritalStatuses.add(MaritalStatus.values()[iterator.next()]);
            searchHelper.setMaritalStatuses(maritalStatuses);
        }

        if(!figure.getCheckModel().getCheckedItems().isEmpty()) {
            List<Figure> figures = new ArrayList<>();
            ObservableList<Integer> checkedItems = figure.getCheckModel().getCheckedIndices();
            Iterator<Integer> iterator = checkedItems.iterator();

            while (iterator.hasNext())
                figures.add(Figure.values()[iterator.next()]);
            searchHelper.setFigures(figures);
        }

        if(heightFrom.getSelectionModel().getSelectedItem() != null)
            searchHelper.setHeightFrom(Integer.valueOf(heightFrom.getSelectionModel().getSelectedItem().toString()));
        if(heightTo.getSelectionModel().getSelectedItem() != null)
            searchHelper.setHeightTo(Integer.valueOf(heightTo.getSelectionModel().getSelectedItem().toString()));

        if(!hairColor.getCheckModel().getCheckedItems().isEmpty()) {
            List<HairColor> hairColors = new ArrayList<>();
            ObservableList<Integer> checkedItems = hairColor.getCheckModel().getCheckedIndices();
            Iterator<Integer> iterator = checkedItems.iterator();

            while (iterator.hasNext())
                hairColors.add(HairColor.values()[iterator.next()]);
            searchHelper.setHairColors(hairColors);
        }

        if(!smoking.getCheckModel().getCheckedItems().isEmpty()) {
            List<Smoking> smokings = new ArrayList<>();
            ObservableList<Integer> checkedItems = smoking.getCheckModel().getCheckedIndices();
            Iterator<Integer> iterator = checkedItems.iterator();

            while (iterator.hasNext())
                smokings.add(Smoking.values()[iterator.next()]);
            searchHelper.setSmokings(smokings);
        }

        if(!alcohol.getCheckModel().getCheckedItems().isEmpty()) {
            List<Alcohol> alcohol = new ArrayList<>();
            ObservableList<Integer> checkedItems = this.alcohol.getCheckModel().getCheckedIndices();
            Iterator<Integer> iterator = checkedItems.iterator();

            while (iterator.hasNext())
                alcohol.add(Alcohol.values()[iterator.next()]);
            searchHelper.setAlcohol(alcohol);
        }

        if(!children.getCheckModel().getCheckedItems().isEmpty()) {
            List<Children> children = new ArrayList<>();
            ObservableList<Integer> checkedItems = this.children.getCheckModel().getCheckedIndices();
            Iterator<Integer> iterator = checkedItems.iterator();

            while (iterator.hasNext())
                children.add(Children.values()[iterator.next()]);
            searchHelper.setChildren(children);
        }

        if(!lookingFor.getCheckModel().getCheckedItems().isEmpty()) {
            List<LookingFor> lookingFors = new ArrayList<>();
            ObservableList<Integer> checkedItems = lookingFor.getCheckModel().getCheckedIndices();
            Iterator<Integer> iterator = checkedItems.iterator();

            while (iterator.hasNext())
                lookingFors.add(LookingFor.values()[iterator.next()]);
            searchHelper.setLookingFors(lookingFors);
        }

        if(!religion.getCheckModel().getCheckedItems().isEmpty()) {
            List<Religion> religions = new ArrayList<>();
            ObservableList<Integer> checkedItems = religion.getCheckModel().getCheckedIndices();
            Iterator<Integer> iterator = checkedItems.iterator();

            while (iterator.hasNext())
                religions.add(Religion.values()[iterator.next()]);
            searchHelper.setReligions(religions);
        }

        ProgressIndicator indicator = new ProgressIndicator(-1);
        resultVBox.getChildren().add(0, indicator);

        new Thread(new Runnable() {
            @Override
            public void run() {
                DefaultHttpClient client = new DefaultHttpClient();
                Credentials credentials = new UsernamePasswordCredentials(user.getUsername(), password);
                client.getCredentialsProvider().setCredentials(AuthScope.ANY, credentials);
                ApacheHttpClient4Executor executor = new ApacheHttpClient4Executor(client);

                ClientRequest clientRequest = new ClientRequest(getUsersUrl, executor);
                clientRequest.body(MediaType.APPLICATION_JSON, searchHelper);
                try {
                    users = new ArrayList<>((Set<User>) clientRequest.post().getEntity(new GenericType<Set<User>>() {
                    }));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Platform.runLater(
                        () -> {
                            pagination.setVisible(true);
                            int maxPage = (int) Math.ceil((double) users.size() / (double) ITEM_PER_PAGE);
                            pagination.setPageCount(maxPage);
                            pagination.setPageFactory((Integer pageIndex) -> createPage(pageIndex));
                            resultVBox.getChildren().remove(indicator);
                        }
                );
            }
        }).start();

    }

    private void viewProfile(User user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("AccountInfoPanel.fxml"));
            AnchorPane pane = null;
            try {
                pane = loader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }

            AccountInfoPanelController accountInfoPanelController = loader.getController();
            accountInfoPanelController.setMainPanel(mainPanel);
            accountInfoPanelController.setLoggedUser(this.user);
            accountInfoPanelController.setUserToView(user);
            accountInfoPanelController.setEmptyPanelController(emptyPanelController);
            accountInfoPanelController.setLoginPanel(loginPanel);
            accountInfoPanelController.setLoginPanelController(loginPanelController);
            accountInfoPanelController.setMainPanelController(this);
            accountInfoPanelController.setPassword(password);
            accountInfoPanelController.setStage(stage);
            accountInfoPanelController.refresh();
            emptyPanelController.setScreen(pane);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public VBox createPage(int pageIndex) {


        VBox box = new VBox(5);

        int page = pageIndex * ITEM_PER_PAGE;
        for (int i = page; i < page + ITEM_PER_PAGE; i++) {

            if(i < users.size()) {
                HBox element = new HBox(10);

                ImageView imageView;
                if(users.get(i).getAvatar() != null) {
                    imageView = new ImageView(new Image(new ByteArrayInputStream(users.get(i).getAvatar())));
                    imageView.setFitHeight(120);
                    imageView.setFitWidth(120);

                } else {
                    imageView = new ImageView("images/noFoto.png");
                    imageView.setFitHeight(120);
                    imageView.setFitWidth(120);
                }

                VBox vBox = new VBox(10);
                vBox.setAlignment(Pos.CENTER_LEFT);
                Label name = new Label("Imię: " + users.get(i).getName());
                name.setFont(new Font("Comic Sans MS", 14));

                LocalDate now = LocalDate.now();
                LocalDate birth = users.get(i).getDateOfBirth().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                Period period = Period.between(birth, now);

                Label age = new Label("Wiek: " + String.valueOf(period.getYears()));
                age.setFont(new Font("Comic Sans MS", 14));

                City userCity = users.get(i).getCity();
                String cityData = userCity.getName() + ", Woj. " + userCity.getVoivodeship() + ", Pow. " + userCity.getCountry() + ", Gmina " + userCity.getCommunity();
                Label city = new Label("Lokalizacja: " + cityData);
                city.setFont(new Font("Comic Sans MS", 14));

                if(users.get(i).isFake()) {
                    Label fake = new Label("Fake");
                    fake.setFont(new Font("Comic Sans MS", 14));
                    fake.setTextFill(Color.RED);
                    vBox.getChildren().addAll(name, age, city, fake);
                } else
                    vBox.getChildren().addAll(name, age, city);

                element.getChildren().addAll(imageView, vBox);
                box.getChildren().add(element);
                int userId = box.getChildren().size() + pageIndex * ITEM_PER_PAGE - 1;
                element.setOnMouseClicked(event -> {
                    viewProfile(users.get(userId));
                });
            }

        }
        return box;
    }

    @FXML
    public void typeLocation() throws Exception {
        ClientRequest clientRequest = new ClientRequest(getCityByNameUrl + "name=" + locationFrom.getEditor().getText());
        this.foundedCitiesSearch = (List<City>)clientRequest.get().getEntity(new GenericType<List<City>>() {});
        List<String> citiesName = getCityNameList(this.foundedCitiesSearch);

        ObservableList<String> options = FXCollections.observableArrayList(citiesName);
        locationFrom.setItems(options);
        distanceFrom.getSelectionModel().select(4);
    }



    public void setMainPanel(AnchorPane mainPanel) {
        this.mainPanel = mainPanel;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setLoginPanel(AnchorPane loginPanel) {
        this.loginPanel = loginPanel;
    }

    public void setEmptyPanelController(EmptyPanelController emptyPanelController) {
        this.emptyPanelController = emptyPanelController;
    }

    public void setLoginPanelController(LoginPanelController loginPanelController) {
        this.loginPanelController = loginPanelController;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
}
