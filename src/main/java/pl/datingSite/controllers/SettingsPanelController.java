package pl.datingSite.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.DefaultHttpClient;
import org.controlsfx.control.CheckComboBox;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.core.executors.ApacheHttpClient4Executor;
import org.jboss.resteasy.util.GenericType;
import pl.datingSite.enums.*;
import pl.datingSite.model.AppearanceAndCharacter;
import pl.datingSite.model.City;
import pl.datingSite.model.User;

import javax.imageio.ImageIO;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.ws.rs.core.MediaType;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.*;

public class SettingsPanelController {

    private Stage stage;
    private AnchorPane mainPanel, loginPanel;
    private User user;
    private EmptyPanelController emptyPanelController;
    private LoginPanelController loginPanelController;
    private MainPanelController mainPanelController;
    private String password;

    private List<City> foundedCties;

    @FXML
    private ImageView avatar, avatar2;
    @FXML
    private Label nameAndSurname, friendsCount, notificationCount, messageCount, changePasswordError, deleteAccountPasswordError, newPasswordError;
    @FXML
    private Button settings, logout, friends, notifications, messages, removeAvatar;
    @FXML
    private StackPane friendsCounter, notificationsCounter, messagesCounter;
    @FXML
    private TextField login, email, name, surname, sex, dateOfBirth, age, typeInterests;
    @FXML
    private ComboBox<String> city;
    @FXML
    private ComboBox<String> zodiacSign, profession, maritalStatus, education,
            figure, hairColor, eyeColor, smoking, alkohol, children, religion;
    @FXML
    private ComboBox<Integer> height;
    @FXML
    ListView<String> interests;
    @FXML
    private CheckComboBox<String> style, holiday, lookingFor, movieType;
    @FXML
    private PasswordField presentPassword, newPassword, passPassword;


    private final String countNotificationUrl = "http://localhost:8090/notification/count?";
    private final String updateUserUrl = "http://localhost:8090/user/updateUser";
    private final String deleteUserUrl = "http://localhost:8090/user/deleteUser";
    private final String changePasswordUrl = "http://localhost:8090/user/changePassword";
    private final String getCityByNameUrl = "http://localhost:8090/city/getByName?";

    @FXML
    public void initialize() {
        changePasswordError.setVisible(false);
        deleteAccountPasswordError.setVisible(false);
        newPasswordError.setVisible(false);
    }

    public void refresh() throws Exception {
        nameAndSurname.setText(user.getName() + " " + user.getSurname());

        if(user.getAvatar() != null) {
            avatar.setImage(new Image(new ByteArrayInputStream(user.getAvatar())));
            avatar2.setImage(new Image(new ByteArrayInputStream(user.getAvatar())));
        } else {
            avatar.setImage(new Image("images/noFoto.png"));
            avatar2.setImage(new Image("images/noFoto.png"));
            removeAvatar.setDisable(true);
        }

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

        setBasicData();
        setAdditional();
        setAppearanceAndCharacter();
    }

    private void setBasicData() {
        login.setText(user.getUsername());
        email.setText(user.getEmail());
        name.setText(user.getName());
        surname.setText(user.getSurname());
        sex.setText(user.getSex());
        String city = user.getCity().getName() + ", woj. " + user.getCity().getVoivodeship() + ", powiat " + user.getCity().getCountry() + ", gmina " + user.getCity().getCommunity();
        ObservableList<String> list = FXCollections.observableArrayList();
        list.add(city);
        this.city.setItems(list);
        this.city.getSelectionModel().select(0);
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
        String date = format.format(user.getDateOfBirth());
        dateOfBirth.setText(date);

        LocalDate now = LocalDate.now();
        LocalDate birth = user.getDateOfBirth().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        Period period = Period.between(birth, now);
        age.setText(String.valueOf(period.getYears()));
    }

    private void setAdditional() {
        ObservableList<String> zodiacSigns = FXCollections.observableArrayList(
                "Wodnik", "Ryby", "Baran", "Byk", "Bliźnięta", "Rak", "Lew",
                "Panna", "Waga", "Skorpion", "Strzelec", "Koziorożec"
        );
        zodiacSign.setItems(zodiacSigns);
        if(user.getZodiacSign() != null)
            zodiacSign.getSelectionModel().select(user.getZodiacSign().ordinal());

        ObservableList<String> professions = FXCollections.observableArrayList(
                "Administracja Biurowa", "Służba cywilna", "Architektura", "Badania",
                "Budownictwo", "Geodezja", "Doradztwo", "Edukacja", "Energetyka", "Farmaceutyka",
                "Finanse", "Gastronomia", "Grafika", "Kadry", "Informatyka Administracja",
                "Informatyka Programowanie", "Inżynieria", "Zarządzanie", "Kosmetyka",
                "Księgowość", "Logistyka", "Marketing", "Media", "Medycyna", "Motoryzacja",
                "Nieruchomości", "Ochrona osób i mienia", "Praca fizyczna", "Prawo", "Rolnictwo",
                "Sport", "Sprzedaż", "Telekomunikacja", "Transport", "Turystyka", "Inne", "Własny biznes"
        );
        profession.setItems(professions);
        if(user.getProfession() != null)
            profession.getSelectionModel().select(user.getProfession().ordinal());

        ObservableList<String> maritalsStatus = FXCollections.observableArrayList(
                "Singiel / Singielka", "Mężatka / Żonaty", "Rozwiedziona / Rozwiedziony", "Wdowa / Wdowiec"
        );
        maritalStatus.setItems(maritalsStatus);
        if(user.getMaritalStatus() != null)
            maritalStatus.getSelectionModel().select(user.getMaritalStatus().ordinal());

        ObservableList<String> educations = FXCollections.observableArrayList(
                "Podstawowe", "Zawodowe", "Średnie", "Wyższe", "Studiuję"
        );
        education.setItems(educations);
        if(user.getEducation() != null)
            education.getSelectionModel().select(user.getEducation().ordinal());

        if(!user.getInterests().isEmpty()) {
            interests.getItems().clear();
            interests.getItems().addAll(user.getInterests());
        }
    }

    private void setAppearanceAndCharacter() {
        ObservableList<String> figures = FXCollections.observableArrayList(
                "Sczupła", "Normalna", "Puszysta"
        );
        figure.setItems(figures);
        if(user.getAppearanceAndCharacter() != null && user.getAppearanceAndCharacter().getFigure() != null)
            figure.getSelectionModel().select(user.getAppearanceAndCharacter().getFigure().ordinal());

        List<Integer> list = new ArrayList<>();
        for(int i = 140; i <= 240; i++)
            list.add(i);

        ObservableList<Integer> heights = FXCollections.observableArrayList(list);
        height.setItems(heights);
        if(user.getAppearanceAndCharacter() != null && user.getAppearanceAndCharacter().getHeight() != null)
            height.getSelectionModel().select(user.getAppearanceAndCharacter().getHeight());

        ObservableList<String> hairColors = FXCollections.observableArrayList(
                "Czarny", "Brązowy", "Blond", "Kasztanowy", "Rudy", "Szary", "Biały"
        );
        hairColor.setItems(hairColors);
        if(user.getAppearanceAndCharacter() != null && user.getAppearanceAndCharacter().getHairColor() != null)
            hairColor.getSelectionModel().select(user.getAppearanceAndCharacter().getHairColor().ordinal());

        ObservableList<String> eyeColors = FXCollections.observableArrayList(
                "Bursztynowe", "Niebieskie", "Brązowe", "Szare", "Zielone", "Piwne"
        );
        eyeColor.setItems(eyeColors);
        if(user.getAppearanceAndCharacter() != null && user.getAppearanceAndCharacter().getEyeColor() != null)
            eyeColor.getSelectionModel().select(user.getAppearanceAndCharacter().getEyeColor().ordinal());

        ObservableList<String> smokings = FXCollections.observableArrayList(
                "Nie palę", "Okazjonalnie", "Palę", "Próbuję rzucić"
        );
        smoking.setItems(smokings);
        if(user.getAppearanceAndCharacter() != null && user.getAppearanceAndCharacter().getSmoking() != null)
            smoking.getSelectionModel().select(user.getAppearanceAndCharacter().getSmoking().ordinal());

        ObservableList<String> alcohols = FXCollections.observableArrayList(
                "Nie piję", "Okazjonalnie", "Piję"
        );
        alkohol.setItems(alcohols);
        if(user.getAppearanceAndCharacter() != null && user.getAppearanceAndCharacter().getAlcohol() != null)
            alkohol.getSelectionModel().select(user.getAppearanceAndCharacter().getAlcohol().ordinal());

        ObservableList<String> childrenList = FXCollections.observableArrayList(
                "Nie mam ale chcę mieć", "Nie mam i nie chcę mieć", "Mam i wystarczy", "Mam ale chcę mieć"
        );
        children.setItems(childrenList);
        if(user.getAppearanceAndCharacter() != null && user.getAppearanceAndCharacter().getChildren() != null)
            children.getSelectionModel().select(user.getAppearanceAndCharacter().getChildren().ordinal());

        ObservableList<String> holidays = FXCollections.observableArrayList(
                "W kraju", "Za granicą", "Nad morzem", "Aktywnie", "Zwiedzając", "W górach", "W domu", "W mieście"
        );
        holiday.getItems().clear();
        holiday.getItems().addAll(holidays);
        if(user.getAppearanceAndCharacter() != null && user.getAppearanceAndCharacter().getHoliday() != null && !user.getAppearanceAndCharacter().getHoliday().isEmpty())
            checkHolidays();

        ObservableList<String> lookingsFor = FXCollections.observableArrayList(
                "Przyjaźni", "Poważnego związku","Zabawy", "Małżeństwa"
        );
        lookingFor.getItems().clear();
        lookingFor.getItems().addAll(lookingsFor);
        if(user.getAppearanceAndCharacter() != null && user.getAppearanceAndCharacter().getLookingFor() != null && !user.getAppearanceAndCharacter().getLookingFor().isEmpty())
            checkLookingFors();

        ObservableList<String> moviesType = FXCollections.observableArrayList(
                "Komedia", "Komedia romantyczna", "Science Fiction", "Fantasy", "Romantyczny", "Historyczny",
                "Horror", "Psychologiczny", "Fantastyczny", "Przygodowy", "Thriller", "Podróżniczy", "Animowany",
                "Melodramat", "Kostiumowy", "Rodzinny", "Muzyczny", "Popularno-Naukowy",
                "Dramat", "Sensacyjny", "Dokumentalny", "Motoryzacyjny"
        );
        movieType.getItems().clear();
        movieType.getItems().addAll(moviesType);
        if(user.getAppearanceAndCharacter() != null && user.getAppearanceAndCharacter().getMovieType() != null && !user.getAppearanceAndCharacter().getMovieType().isEmpty())
            checkMovieTypes();

        ObservableList<String> styles = FXCollections.observableArrayList(
                "Własny", "Elegancki", "Modny", "Wyluzowany", "Artystyczny", "Hip-Hop", "Tradycyjny", "Nowoczesny", "Sportowy"
        );
        style.getItems().clear();
        style.getItems().addAll(styles);
        if(user.getAppearanceAndCharacter() != null && user.getAppearanceAndCharacter().getStyle() != null && !user.getAppearanceAndCharacter().getStyle().isEmpty())
            checkStyles();

        ObservableList<String> religions = FXCollections.observableArrayList(
                "Chrześcijaństwo", "Islam", "Ateizm", "Hinduizm", "Buddyzm", "Judaizm", "Prawosławni", "Protestantyzm", "Świadkowie Jehowy"
        );
        religion.setItems(religions);
        if(user.getAppearanceAndCharacter() != null && user.getAppearanceAndCharacter().getReligion() != null)
            religion.getSelectionModel().select(user.getAppearanceAndCharacter().getReligion().ordinal());
    }

    private void checkHolidays() {
        Set<Holiday> holidays = user.getAppearanceAndCharacter().getHoliday();
        Iterator<Holiday> iterator = holidays.iterator();

        while (iterator.hasNext()) {
            holiday.getCheckModel().checkIndices(iterator.next().ordinal());
        }
    }

    private void checkLookingFors() {
        Set<LookingFor> lookingFors = user.getAppearanceAndCharacter().getLookingFor();
        Iterator<LookingFor> iterator = lookingFors.iterator();

        while (iterator.hasNext()) {
            lookingFor.getCheckModel().checkIndices(iterator.next().ordinal());
        }
    }

    private void checkMovieTypes() {
        Set<MovieType> movieTypes = user.getAppearanceAndCharacter().getMovieType();
        Iterator<MovieType> iterator = movieTypes.iterator();

        while (iterator.hasNext()) {
            movieType.getCheckModel().checkIndices(iterator.next().ordinal());
        }
    }

    private void checkStyles() {
        Set<Style> styles = user.getAppearanceAndCharacter().getStyle();
        Iterator<Style> iterator = styles.iterator();

        while (iterator.hasNext()) {
            style.getCheckModel().checkIndices(iterator.next().ordinal());
        }
    }

    @FXML
    public void typeCity() throws Exception {

        ClientRequest clientRequest = new ClientRequest(getCityByNameUrl + "name=" + city.getEditor().getText());
        this.foundedCties = (List<City>)clientRequest.get().getEntity(new GenericType<List<City>>() {});
        List<String> citiesName = getCityNameList(this.foundedCties);

        ObservableList<String> options = FXCollections.observableArrayList(citiesName);
        city.setItems(options);
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
    public void settings() {

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
        friendsPanelController.setUser(user);
        friendsPanelController.setMainPanel(mainPanel);
        friendsPanelController.setEmptyPanelController(emptyPanelController);
        friendsPanelController.setLoginPanel(loginPanel);
        friendsPanelController.setLoginPanelController(loginPanelController);
        friendsPanelController.setMainPanelController(mainPanelController);
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
        notificationPanelController.setMainPanelController(mainPanelController);
        notificationPanelController.setPassword(password);
        notificationPanelController.refresh();
        emptyPanelController.setScreen(pane);
    }

    @FXML
    public void messages() {

    }

    @FXML
    public void save() throws Exception {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Dating Site");
        alert.setHeaderText(null);
        alert.setContentText("Zapisać dane?");

        Optional<ButtonType> result = alert.showAndWait();
        if((result.get() == ButtonType.OK)) {
            user.setName(name.getText());
            user.setSurname(surname.getText());
            user.setPassword(password);

            if (city.getSelectionModel().getSelectedIndex() >= 0 && foundedCties != null)
                user.setCity(foundedCties.get(city.getSelectionModel().getSelectedIndex()));

            if (!zodiacSign.getSelectionModel().isEmpty())
                user.setZodiacSign(ZodiacSign.values()[zodiacSign.getSelectionModel().getSelectedIndex()]);
            if (!profession.getSelectionModel().isEmpty())
                user.setProfession(Profession.values()[profession.getSelectionModel().getSelectedIndex()]);
            if (!maritalStatus.getSelectionModel().isEmpty())
                user.setMaritalStatus(MaritalStatus.values()[maritalStatus.getSelectionModel().getSelectedIndex()]);
            if (!education.getSelectionModel().isEmpty())
                user.setEducation(Education.values()[education.getSelectionModel().getSelectedIndex()]);

            if (!interests.getItems().isEmpty()) {
                Set<String> listOfInterest = new HashSet<>(interests.getItems());
                user.setInterests(listOfInterest);
            }

            if(user.getAppearanceAndCharacter() == null)
                user.setAppearanceAndCharacter(new AppearanceAndCharacter());

            if (!figure.getSelectionModel().isEmpty())
                user.getAppearanceAndCharacter().setFigure(Figure.values()[figure.getSelectionModel().getSelectedIndex()]);
            if (!height.getSelectionModel().isEmpty())
                user.getAppearanceAndCharacter().setHeight(height.getSelectionModel().getSelectedItem());
            if (!hairColor.getSelectionModel().isEmpty())
                user.getAppearanceAndCharacter().setHairColor(HairColor.values()[hairColor.getSelectionModel().getSelectedIndex()]);
            if (!eyeColor.getSelectionModel().isEmpty())
                user.getAppearanceAndCharacter().setEyeColor(EyeColor.values()[eyeColor.getSelectionModel().getSelectedIndex()]);
            if (!smoking.getSelectionModel().isEmpty())
                user.getAppearanceAndCharacter().setSmoking(Smoking.values()[smoking.getSelectionModel().getSelectedIndex()]);
            if (!alkohol.getSelectionModel().isEmpty())
                user.getAppearanceAndCharacter().setAlcohol(Alcohol.values()[alkohol.getSelectionModel().getSelectedIndex()]);
            if (!children.getSelectionModel().isEmpty())
                user.getAppearanceAndCharacter().setChildren(Children.values()[children.getSelectionModel().getSelectedIndex()]);

            if(user.getAppearanceAndCharacter() == null)
                user.setAppearanceAndCharacter(new AppearanceAndCharacter());

            user.getAppearanceAndCharacter().setHoliday(getHolidaysList());
            user.getAppearanceAndCharacter().setLookingFor(getLookingForList());
            user.getAppearanceAndCharacter().setMovieType(getMovieTypeList());
            user.getAppearanceAndCharacter().setStyle(getStyleList());

            if (!religion.getSelectionModel().isEmpty())
                user.getAppearanceAndCharacter().setReligion(Religion.values()[religion.getSelectionModel().getSelectedIndex()]);


            DefaultHttpClient client = new DefaultHttpClient();
            Credentials credentials = new UsernamePasswordCredentials(user.getUsername(), password);
            client.getCredentialsProvider().setCredentials(AuthScope.ANY, credentials);
            ApacheHttpClient4Executor executor = new ApacheHttpClient4Executor(client);

            ClientRequest clientRequest = new ClientRequest(updateUserUrl, executor);
            clientRequest.body(MediaType.APPLICATION_JSON, user);
            this.user = (User) clientRequest.put().getEntity(User.class);

            refresh();
        }
    }

    private Set<Holiday> getHolidaysList() {
        Set<Holiday> holidays = null;

        if(!holiday.getCheckModel().getCheckedItems().isEmpty()) {
            holidays = new HashSet<>();
            ObservableList<Integer> checkedItems = holiday.getCheckModel().getCheckedIndices();
            Iterator<Integer> iterator = checkedItems.iterator();

            while (iterator.hasNext())
                holidays.add(Holiday.values()[iterator.next()]);
        }

        return holidays;
    }

    private Set<LookingFor> getLookingForList() {
        Set<LookingFor> lookingFors = null;

        if(!lookingFor.getCheckModel().getCheckedItems().isEmpty()) {
            lookingFors = new HashSet<>();
            ObservableList<Integer> checkedItems = lookingFor.getCheckModel().getCheckedIndices();
            Iterator<Integer> iterator = checkedItems.iterator();

            while (iterator.hasNext())
                lookingFors.add(LookingFor.values()[iterator.next()]);
        }

        return lookingFors;
    }

    private Set<MovieType> getMovieTypeList() {
        Set<MovieType> movieTypes = null;

        if(!movieType.getCheckModel().getCheckedItems().isEmpty()) {
            movieTypes = new HashSet<>();
            ObservableList<Integer> checkedItems = movieType.getCheckModel().getCheckedIndices();
            Iterator<Integer> iterator = checkedItems.iterator();

            while (iterator.hasNext())
                movieTypes.add(MovieType.values()[iterator.next()]);
        }

        return movieTypes;
    }

    private Set<Style> getStyleList() {
        Set<Style> styles = null;

        if(!style.getCheckModel().getCheckedItems().isEmpty()) {
            styles = new HashSet<>();
            ObservableList<Integer> checkedItems = style.getCheckModel().getCheckedIndices();
            Iterator<Integer> iterator = checkedItems.iterator();

            while (iterator.hasNext())
                styles.add(Style.values()[iterator.next()]);
        }

        return styles;
    }

    @FXML
    public void back() throws Exception {
        mainPanelController.refresh();
        mainPanelController.clear();
        emptyPanelController.setScreen(mainPanel);
    }

    @FXML
    public void add() {
        String interest = typeInterests.getText();
        interests.getItems().add(interest);
        typeInterests.clear();
    }

    @FXML
    public void delete() {
        String deletedItem = interests.getSelectionModel().getSelectedItem();
        if(deletedItem == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Dating Site");
            alert.setHeaderText(null);
            alert.setContentText("Nic nie zostało wybrane!");

            Optional<ButtonType> result = alert.showAndWait();
            if((result.get() == ButtonType.OK)){

            }
        } else {
            interests.getItems().remove(deletedItem);
        }
    }

    @FXML
    public void changePassword() throws Exception {
        if(presentPassword.getText().isEmpty())
            changePasswordError.setVisible(true);
        if(presentPassword.getText().trim().equals(password)) {
            if(newPassword.getText().isEmpty())
                newPasswordError.setVisible(true);
            else {
                user.setPassword(newPassword.getText());

                DefaultHttpClient client = new DefaultHttpClient();
                Credentials credentials = new UsernamePasswordCredentials(user.getUsername(), password);
                client.getCredentialsProvider().setCredentials(AuthScope.ANY, credentials);
                ApacheHttpClient4Executor executor = new ApacheHttpClient4Executor(client);

                ClientRequest clientRequest = new ClientRequest(changePasswordUrl, executor);
                clientRequest.body(MediaType.APPLICATION_JSON, user);
                this.user = (User) clientRequest.put().getEntity(User.class);
                this.password = newPassword.getText();

                presentPassword.clear();
                newPassword.clear();

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Dating Site");
                alert.setHeaderText(null);
                alert.setContentText("Twoje hasło zostało zmienione");

                Optional<ButtonType> result = alert.showAndWait();
                if((result.get() == ButtonType.OK)){

                }
            }
        } else
            changePasswordError.setVisible(true);
    }

    @FXML
    public void deleteAccount() throws Exception {
        if(passPassword.getText().isEmpty())
            deleteAccountPasswordError.setVisible(true);
        if(passPassword.getText().trim().equals(password)) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Dating Site");
            alert.setHeaderText(null);
            alert.setContentText("Czy na pewno chcesz usunąć swoje konto?");

            Optional<ButtonType> result = alert.showAndWait();
            if((result.get() == ButtonType.OK)){
                DefaultHttpClient client = new DefaultHttpClient();
                Credentials credentials = new UsernamePasswordCredentials(user.getUsername(), password);
                client.getCredentialsProvider().setCredentials(AuthScope.ANY, credentials);
                ApacheHttpClient4Executor executor = new ApacheHttpClient4Executor(client);

                ClientRequest clientRequest = new ClientRequest(deleteUserUrl, executor);
                clientRequest.body(MediaType.APPLICATION_JSON, user.getUsername());
                clientRequest.delete();

                Alert alert2 = new Alert(Alert.AlertType.INFORMATION);
                alert2.setTitle("Dating Site");
                alert2.setHeaderText(null);
                alert2.setContentText("Twoje konto zostało usunięte");

                Optional<ButtonType> result2 = alert2.showAndWait();
                if(result2.get() == ButtonType.OK)
                    logout();
            }
        } else
            deleteAccountPasswordError.setVisible(true);
    }

    @FXML
    public void removeAvatar() {
        user.setPassword(null);
        avatar.setImage(new Image("images/noFoto.png"));
        avatar2.setImage(new Image("images/noFoto.png"));
        user.setAvatar(null);
        removeAvatar.setDisable(true);
    }

    @FXML
    public void addAvatar() throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Dating Site");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("JPG", "*.jpg"),
                new FileChooser.ExtensionFilter("PNG", "*.png"), new FileChooser.ExtensionFilter("JPEG", "*.jpeg"));

        File file = fileChooser.showOpenDialog(stage);
        Image image = new Image(file.toURI().toString());
        avatar.setImage(image);
        avatar2.setImage(image);


        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        ImageIO.write(SwingFXUtils.fromFXImage(image, null), FilenameUtils.getExtension(file.toURI().toString()), stream);
        user.setAvatar(stream.toByteArray());

        removeAvatar.setDisable(false);
    }

    @FXML
    public void typePassword() {
        changePasswordError.setVisible(false);
    }

    @FXML
    public void typeNewPassword() {
        newPasswordError.setVisible(false);
    }

    @FXML
    public void typePasswordDelete() {
        deleteAccountPasswordError.setVisible(false);
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

    public void setMainPanelController(MainPanelController mainPanelController) {
        this.mainPanelController = mainPanelController;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
}
