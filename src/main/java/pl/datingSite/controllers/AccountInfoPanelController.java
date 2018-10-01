package pl.datingSite.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.client.core.executors.ApacheHttpClient4Executor;
import org.jboss.resteasy.util.GenericType;
import pl.datingSite.enums.Holiday;
import pl.datingSite.enums.LookingFor;
import pl.datingSite.enums.MovieType;
import pl.datingSite.enums.Style;
import pl.datingSite.model.Friends;
import pl.datingSite.model.User;
import pl.datingSite.model.messages.Conversation;
import pl.datingSite.model.messages.Message;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.*;

public class AccountInfoPanelController {
    private Stage stage;
    private AnchorPane mainPanel, loginPanel, accountInfoPanel;
    private User loggedUser, userToView;
    private EmptyPanelController emptyPanelController;
    private LoginPanelController loginPanelController;
    private MainPanelController mainPanelController;
    private String password;
    private Friends userFriends;

    private Set<Conversation> conversations;

    @FXML
    private ImageView avatar, avatar2;
    @FXML
    private Label nameAndSurname, friendsCount, notificationCount, messageCount, fake;
    @FXML
    private Button settings, logout, friends, notifications, messages, addToFriends;
    @FXML
    private StackPane friendsCounter, notificationsCounter, messagesCounter;

    @FXML
    private TextField login, name, sex, age, typeInterests, city, zodiacSign, profession, maritalStatus, education,
            figure, hairColor, eyeColor, smoking, alkohol, children, religion, height;
    @FXML
    ListView<String> interests, style, holiday, lookingFor, movieType;
    @FXML
    private HBox buttons;


    private final String applicationTestUrl = "http://localhost:8090/test";
    private final String countNotificationUrl = "http://localhost:8090/notification/count?";
    private final String newWaveNotificationUrl = "http://localhost:8090/notification/newWave?";
    private final String newKissNotificationUrl = "http://localhost:8090/notification/newKiss?";
    private final String sendInvitationUrl = "http://localhost:8090/friends/sendInvitation?";
    private final String getFriendsUrl = "http://localhost:8090/friends/getFriends?";
    private final String countInvitationsUrl = "http://localhost:8090/friends/count?";
    private final String getConversationUrl = "http://localhost:8090/messages/getConversation?";

    @FXML
    public void initialize() throws Exception {
        try {
            ClientRequest clientRequest = new ClientRequest(applicationTestUrl);
            clientRequest.get();
        } catch (ConnectException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            ((Stage)alert.getDialogPane().getScene().getWindow()).getIcons().add(new Image("images/logoMini.png"));
            alert.setTitle("Dating Site");
            alert.setHeaderText(null);
            alert.setContentText("Brak połączenia z serwerem!");

            Optional<ButtonType> result = alert.showAndWait();
            if((result.get() == ButtonType.OK)){
                System.exit(0);
            }
        }
    }

    public void refresh() throws Exception {
        Random generator = new Random();
        int val = generator.nextInt(16) + 1;
        String path = "images/background/background" + val + ".jpg";
        accountInfoPanel.setStyle("-fx-background-image: url('" + path + "'); -fx-background-size: 1200 720; -fx-background-size: cover");


        nameAndSurname.setText(loggedUser.getName() + " " + loggedUser.getSurname());

        if(loggedUser.getUsername().equals(userToView.getUsername()))
            buttons.setVisible(false);

        if(loggedUser.getAvatar() != null) {
            avatar.setImage(new Image(new ByteArrayInputStream(loggedUser.getAvatar())));
        } else {
            avatar.setImage(new Image("images/noFoto.png"));
        }

        DefaultHttpClient client = new DefaultHttpClient();
        Credentials credentials = new UsernamePasswordCredentials(loggedUser.getUsername(), password);
        client.getCredentialsProvider().setCredentials(AuthScope.ANY, credentials);
        ApacheHttpClient4Executor executor = new ApacheHttpClient4Executor(client);

        ClientRequest clientRequest = new ClientRequest(countNotificationUrl + "username=" + loggedUser.getUsername(), executor);
        Integer notificationQuantity = (Integer)clientRequest.get().getEntity(Integer.class);
        notificationCount.setText(notificationQuantity.toString());

        clientRequest = new ClientRequest(countInvitationsUrl + "username=" + loggedUser.getUsername(), executor);
        Integer invitationQuantity = (Integer)clientRequest.get().getEntity(Integer.class);
        friendsCount.setText(invitationQuantity.toString());

        setVisibleFriendsButton();

        if(friendsCount.getText().equals("0"))
            friendsCounter.setVisible(false);
        if(notificationCount.getText().equals("0"))
            notificationsCounter.setVisible(false);

        setBasicData();
        setAdditional();
        setAppearanceAndCharacter();
        setConversations();
    }

    @SuppressWarnings("Duplicates")
    private void setConversations() throws Exception {
        DefaultHttpClient client = new DefaultHttpClient();
        Credentials credentials = new UsernamePasswordCredentials(loggedUser.getUsername(), password);
        client.getCredentialsProvider().setCredentials(AuthScope.ANY, credentials);
        ApacheHttpClient4Executor executor = new ApacheHttpClient4Executor(client);

        ClientRequest clientRequest = new ClientRequest(getConversationUrl + "username=" + loggedUser.getUsername(), executor);
        this.conversations = (Set<Conversation>)clientRequest.get().getEntity(new GenericType<Set<Conversation>>() {});
        checkUnreaded(conversations);
    }

    @SuppressWarnings("Duplicates")
    private void checkUnreaded(Set<Conversation> conversations) {
        int counter = 0;

        Iterator<Conversation> conversationIterator = conversations.iterator();
        while (conversationIterator.hasNext()) {
            Conversation conversation = conversationIterator.next();
            List<Message> messages = conversation.getMessages();
            if(messages.stream().filter(m -> m.getMessageFrom().equals(conversation.getFromWho()) && m.isReaded() == false).count() > 0)
                counter++;
        }
        messageCount.setText(String.valueOf(counter));

        if(messageCount.getText().equals("0"))
            messagesCounter.setVisible(false);

    }

    private void setVisibleFriendsButton() throws Exception {
        DefaultHttpClient client = new DefaultHttpClient();
        Credentials credentials = new UsernamePasswordCredentials(loggedUser.getUsername(), password);
        client.getCredentialsProvider().setCredentials(AuthScope.ANY, credentials);
        ApacheHttpClient4Executor executor = new ApacheHttpClient4Executor(client);

        ClientRequest clientRequest = new ClientRequest(getFriendsUrl + "username=" + loggedUser.getUsername(), executor);
        this.userFriends = (Friends) clientRequest.get().getEntity(Friends.class);

        if(userFriends.getSendInvitations().contains(userToView.getUsername()) || userFriends.getFriendsUsernames().contains(userToView.getUsername())
                || userFriends.getInvitationsFrom().contains(userToView.getUsername()))
            addToFriends.setVisible(false);
    }

    private void setBasicData() {
        login.setText(userToView.getUsername());
        name.setText(userToView.getName());
        sex.setText(userToView.getSex());

        if(userToView.getAvatar() != null) {
            avatar2.setImage(new Image(new ByteArrayInputStream(userToView.getAvatar())));
        } else {
            avatar2.setImage(new Image("images/noFoto.png"));
        }

        String city = userToView.getCity().getName() + ", woj. " + userToView.getCity().getVoivodeship() + ", powiat " + userToView.getCity().getCountry() + ", gmina " + userToView.getCity().getCommunity();
        this.city.setText(city);

        LocalDate now = LocalDate.now();
        LocalDate birth = userToView.getDateOfBirth().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        Period period = Period.between(birth, now);
        age.setText(String.valueOf(period.getYears()));

        if(userToView.isFake()) {
            fake.setText("Fake");
            fake.setFont(new Font("Comic Sans MS", 30));
            fake.setTextFill(Color.RED);
            fake.setVisible(true);
        } else if(userToView.getUsername().equals("mati")) {
            fake.setText("Admin");
            fake.setFont(new Font("Comic Sans MS", 30));
            fake.setTextFill(Color.BLUE);
            fake.setVisible(true);
        }
        else
            fake.setVisible(false);
    }

    private void setAdditional() {
        if(userToView.getZodiacSign() != null) {
            ObservableList<String> zodiacSigns = FXCollections.observableArrayList(
                    "Wodnik", "Ryby", "Baran", "Byk", "Bliźnięta", "Rak", "Lew",
                    "Panna", "Waga", "Skorpion", "Strzelec", "Koziorożec"
            );
            zodiacSign.setText(zodiacSigns.get(userToView.getZodiacSign().ordinal()));
        }

        if(userToView.getProfession() != null) {
            ObservableList<String> professions = FXCollections.observableArrayList(
                    "Administracja Biurowa", "Służba cywilna", "Architektura", "Badania",
                    "Budownictwo", "Geodezja", "Doradztwo", "Edukacja", "Energetyka", "Farmaceutyka",
                    "Finanse", "Gastronomia", "Grafika", "Kadry", "Informatyka Administracja",
                    "Informatyka Programowanie", "Inżynieria", "Zarządzanie", "Kosmetyka",
                    "Księgowość", "Logistyka", "Marketing", "Media", "Medycyna", "Motoryzacja",
                    "Nieruchomości", "Ochrona osób i mienia", "Praca fizyczna", "Prawo", "Rolnictwo",
                    "Sport", "Sprzedaż", "Telekomunikacja", "Transport", "Turystyka", "Inne", "Własny biznes"
            );
            profession.setText(professions.get(userToView.getProfession().ordinal()));
        }

        if(userToView.getMaritalStatus() != null) {
            ObservableList<String> maritalsStatus = FXCollections.observableArrayList(
                    "Singiel / Singielka", "Mężatka / Żonaty", "Rozwiedziona / Rozwiedziony", "Wdowa / Wdowiec"
            );
            maritalStatus.setText(maritalsStatus.get(userToView.getMaritalStatus().ordinal()));
        }

        if(userToView.getEducation() != null) {
            ObservableList<String> educations = FXCollections.observableArrayList(
                    "Podstawowe", "Zawodowe", "Średnie", "Wyższe", "Studiuję"
            );
            education.setText(educations.get(userToView.getEducation().ordinal()));
        }

        if(userToView.getInterests() != null)
            interests.setItems(FXCollections.observableArrayList(userToView.getInterests()));
    }

    private void setAppearanceAndCharacter() {
        if(userToView.getAppearanceAndCharacter().getFigure() != null) {
            ObservableList<String> figures = FXCollections.observableArrayList(
                    "Sczupła", "Normalna", "Puszysta"
            );
            figure.setText(figures.get(userToView.getAppearanceAndCharacter().getFigure().ordinal()));
        }

        if(userToView.getAppearanceAndCharacter().getHeight() != null) {
            height.setText(String.valueOf(userToView.getAppearanceAndCharacter().getHeight()));
        }

        if(userToView.getAppearanceAndCharacter().getHairColor() != null) {
            ObservableList<String> hairColors = FXCollections.observableArrayList(
                    "Czarny", "Brązowy", "Blond", "Kasztanowy", "Rudy", "Szary", "Biały"
            );
            hairColor.setText(hairColors.get(userToView.getAppearanceAndCharacter().getHairColor().ordinal()));
        }

        if(userToView.getAppearanceAndCharacter().getEyeColor() != null) {
            ObservableList<String> eyeColors = FXCollections.observableArrayList(
                    "Bursztynowe", "Niebieskie", "Brązowe", "Szare", "Zielone", "Piwne"
            );
            eyeColor.setText(eyeColors.get(userToView.getAppearanceAndCharacter().getEyeColor().ordinal()));
        }

        if(userToView.getAppearanceAndCharacter().getSmoking() != null) {
            ObservableList<String> smokings = FXCollections.observableArrayList(
                    "Nie palę", "Okazjonalnie", "Palę", "Próbuję rzucić"
            );
            smoking.setText(smokings.get(userToView.getAppearanceAndCharacter().getSmoking().ordinal()));
        }

        if(userToView.getAppearanceAndCharacter().getAlcohol() != null) {
            ObservableList<String> alcohols = FXCollections.observableArrayList(
                    "Nie piję", "Okazjonalnie", "Piję"
            );
            alkohol.setText(alcohols.get(userToView.getAppearanceAndCharacter().getAlcohol().ordinal()));
        }

        if(userToView.getAppearanceAndCharacter().getChildren() != null) {
            ObservableList<String> childrenList = FXCollections.observableArrayList(
                    "Nie mam ale chcę mieć", "Nie mam i nie chcę mieć", "Mam i wystarczy", "Mam ale chcę mieć"
            );
            children.setText(childrenList.get(userToView.getAppearanceAndCharacter().getChildren().ordinal()));
        }

        if(userToView.getAppearanceAndCharacter().getHoliday() != null) {
            holiday.setItems(FXCollections.observableArrayList(getDataList(userToView.getAppearanceAndCharacter().getHoliday())));
        }

        if(userToView.getAppearanceAndCharacter().getLookingFor() != null)
            lookingFor.setItems(FXCollections.observableArrayList(getDataList(userToView.getAppearanceAndCharacter().getLookingFor())));

        if(userToView.getAppearanceAndCharacter().getMovieType() != null)
            movieType.setItems(FXCollections.observableArrayList(getDataList(userToView.getAppearanceAndCharacter().getMovieType())));

        if(userToView.getAppearanceAndCharacter().getStyle() != null)
            style.setItems(FXCollections.observableArrayList(getDataList(userToView.getAppearanceAndCharacter().getStyle())));

        if(userToView.getAppearanceAndCharacter().getReligion() != null) {
            ObservableList<String> religions = FXCollections.observableArrayList(
                    "Chrześcijaństwo", "Islam", "Ateizm", "Hinduizm", "Buddyzm", "Judaizm", "Prawosławni", "Protestantyzm", "Świadkowie Jehowy"
            );
            religion.setText(religions.get(userToView.getAppearanceAndCharacter().getReligion().ordinal()));
        }
    }

    private <T> List<String> getDataList(Set<T> data) {
        ObservableList<String> holidays = FXCollections.observableArrayList(
                "W kraju", "Za granicą", "Nad morzem", "Aktywnie", "Zwiedzając", "W górach", "W domu", "W mieście"
        );

        ObservableList<String> lookingsFor = FXCollections.observableArrayList(
                "Przyjaźni", "Poważnego związku","Zabawy", "Małżeństwa"
        );

        ObservableList<String> moviesType = FXCollections.observableArrayList(
                "Komedia", "Komedia romantyczna", "Science Fiction", "Fantasy", "Romantyczny", "Historyczny",
                "Horror", "Psychologiczny", "Fantastyczny", "Przygodowy", "Thriller", "Podróżniczy", "Animowany",
                "Melodramat", "Kostiumowy", "Rodzinny", "Muzyczny", "Popularno-Naukowy",
                "Dramat", "Sensacyjny", "Dokumentalny", "Motoryzacyjny"
        );

        ObservableList<String> styles = FXCollections.observableArrayList(
                "Własny", "Elegancki", "Modny", "Wyluzowany", "Artystyczny", "Hip-Hop", "Tradycyjny", "Nowoczesny", "Sportowy"
        );



        List<T> list = new ArrayList<>(data);
        List<String> result = new ArrayList<>();

        if(!list.isEmpty() && list.get(0) instanceof Holiday) {
            Iterator<Holiday> iterator = (Iterator<Holiday>) data.iterator();

            while (iterator.hasNext()) {
                int id = iterator.next().ordinal();
                result.add(holidays.get(id));
            }
        }

        if(!list.isEmpty() && list.get(0) instanceof LookingFor) {
            Iterator<LookingFor> iterator = (Iterator<LookingFor>) data.iterator();

            while (iterator.hasNext()) {
                int id = iterator.next().ordinal();
                result.add(lookingsFor.get(id));
            }
        }

        if(!list.isEmpty() && list.get(0) instanceof MovieType) {
            Iterator<MovieType> iterator = (Iterator<MovieType>) data.iterator();

            while (iterator.hasNext()) {
                int id = iterator.next().ordinal();
                result.add(moviesType.get(id));
            }
        }

        if(!list.isEmpty() && list.get(0) instanceof Style) {
            Iterator<Style> iterator = (Iterator<Style>) data.iterator();

            while (iterator.hasNext()) {
                int id = iterator.next().ordinal();
                result.add(styles.get(id));
            }
        }

        return result;
    }

    @FXML
    public void addToFriends() throws Exception {
        DefaultHttpClient client = new DefaultHttpClient();
        Credentials credentials = new UsernamePasswordCredentials(loggedUser.getUsername(), password);
        client.getCredentialsProvider().setCredentials(AuthScope.ANY, credentials);
        ApacheHttpClient4Executor executor = new ApacheHttpClient4Executor(client);

        ClientRequest clientRequest = new ClientRequest(sendInvitationUrl + "from=" + loggedUser.getUsername() + "&to=" + userToView.getUsername(), executor);
        userFriends = (Friends) clientRequest.put().getEntity(Friends.class);

        if(userFriends != null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Dating Site");
            ((Stage)alert.getDialogPane().getScene().getWindow()).getIcons().add(new Image("images/logoMini.png"));
            alert.setHeaderText(null);
            alert.setContentText("Zaprosiłeś użytkownika " + userToView.getName() + " do grona znajomych");

            Optional<ButtonType> result = alert.showAndWait();
            if((result.get() == ButtonType.OK)){
                setVisibleFriendsButton();
            }
        }
    }

    @FXML
    public void writeMessage() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("MessagePanel.fxml"));
        AnchorPane pane = null;
        try {
            pane = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        MessagePanelController messagePanelController = loader.getController();
        messagePanelController.setMainPanel(mainPanel);
        messagePanelController.setUser(loggedUser);
        messagePanelController.setEmptyPanelController(emptyPanelController);
        messagePanelController.setLoginPanel(loginPanel);
        messagePanelController.setLoginPanelController(loginPanelController);
        messagePanelController.setMainPanelController(mainPanelController);
        messagePanelController.setConversationWith(userToView);
        messagePanelController.setPassword(password);
        messagePanelController.setStage(stage);
        messagePanelController.setMessagePane(pane);
        messagePanelController.refresh();
        messagePanelController.toWriteMessage();
        emptyPanelController.setScreen(pane);
    }

    @FXML
    public void wave() throws Exception {
        DefaultHttpClient client = new DefaultHttpClient();
        Credentials credentials = new UsernamePasswordCredentials(loggedUser.getUsername(), password);
        client.getCredentialsProvider().setCredentials(AuthScope.ANY, credentials);
        ApacheHttpClient4Executor executor = new ApacheHttpClient4Executor(client);

        ClientRequest clientRequest = new ClientRequest(newWaveNotificationUrl + "from=" + loggedUser.getUsername() + "&to=" + userToView.getUsername(), executor);
        ClientResponse clientResponse = clientRequest.put();
        if(clientResponse.getStatus() == 200) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Dating Site");
            ((Stage)alert.getDialogPane().getScene().getWindow()).getIcons().add(new Image("images/logoMini.png"));
            alert.setHeaderText(null);
            alert.setContentText("Pomachałeś użytkownikowi " + userToView.getName());

            Optional<ButtonType> result = alert.showAndWait();
            if((result.get() == ButtonType.OK)){

            }
        }

    }

    @FXML
    public void sendKiss() throws Exception {
        DefaultHttpClient client = new DefaultHttpClient();
        Credentials credentials = new UsernamePasswordCredentials(loggedUser.getUsername(), password);
        client.getCredentialsProvider().setCredentials(AuthScope.ANY, credentials);
        ApacheHttpClient4Executor executor = new ApacheHttpClient4Executor(client);

        ClientRequest clientRequest = new ClientRequest(newKissNotificationUrl + "from=" + loggedUser.getUsername() + "&to=" + userToView.getUsername(), executor);
        ClientResponse clientResponse = clientRequest.put();
        if(clientResponse.getStatus() == 200) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Dating Site");
            ((Stage)alert.getDialogPane().getScene().getWindow()).getIcons().add(new Image("images/logoMini.png"));
            alert.setHeaderText(null);
            alert.setContentText("Całus został wysłany użytkownikowi " + userToView.getName());

            Optional<ButtonType> result = alert.showAndWait();
            if((result.get() == ButtonType.OK)){

            }
        }
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
        settingsPanelController.setEmptyPanelController(emptyPanelController);
        settingsPanelController.setLoginPanel(loginPanel);
        settingsPanelController.setUser(loggedUser);
        settingsPanelController.setLoginPanelController(loginPanelController);
        settingsPanelController.setMainPanelController(mainPanelController);
        settingsPanelController.setPassword(password);
        settingsPanelController.setStage(stage);
        settingsPanelController.setSettingsPanel(pane);
        settingsPanelController.refresh();
        emptyPanelController.setScreen(pane);
    }

    @FXML
    public void logout() {
        loginPanelController.clearTextFields();
        loginPanelController.refresh();
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
        friendsPanelController.setUser(loggedUser);
        friendsPanelController.setEmptyPanelController(emptyPanelController);
        friendsPanelController.setLoginPanel(loginPanel);
        friendsPanelController.setLoginPanelController(loginPanelController);
        friendsPanelController.setMainPanelController(mainPanelController);
        friendsPanelController.setPassword(password);
        friendsPanelController.setStage(stage);
        friendsPanelController.setFriendsPanel(pane);
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
        notificationPanelController.setEmptyPanelController(emptyPanelController);
        notificationPanelController.setUser(loggedUser);
        notificationPanelController.setLoginPanel(loginPanel);
        notificationPanelController.setLoginPanelController(loginPanelController);
        notificationPanelController.setMainPanelController(mainPanelController);
        notificationPanelController.setPassword(password);
        notificationPanelController.setNotificationPanel(pane);
        notificationPanelController.refresh();
        emptyPanelController.setScreen(pane);
    }

    @FXML
    public void messages() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("MessagePanel.fxml"));
        AnchorPane pane = null;
        try {
            pane = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        MessagePanelController messagePanelController = loader.getController();
        messagePanelController.setMainPanel(mainPanel);
        messagePanelController.setUser(loggedUser);
        messagePanelController.setEmptyPanelController(emptyPanelController);
        messagePanelController.setLoginPanel(loginPanel);
        messagePanelController.setLoginPanelController(loginPanelController);
        messagePanelController.setMainPanelController(mainPanelController);
        messagePanelController.setPassword(password);
        messagePanelController.setStage(stage);
        messagePanelController.setMessagePane(pane);
        messagePanelController.refresh();
        emptyPanelController.setScreen(pane);
    }

    @FXML
    public void back() throws Exception {
        mainPanelController.refresh();
        mainPanelController.clear();
        emptyPanelController.setScreen(mainPanel);
    }

    public void setMainPanel(AnchorPane mainPanel) {
        this.mainPanel = mainPanel;
    }

    public void setLoggedUser(User loggedUser) {
        this.loggedUser = loggedUser;
    }

    public void setUserToView(User userToView) {
        this.userToView = userToView;
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

    public void setAccountInfoPanel(AnchorPane accountInfoPanel) {
        this.accountInfoPanel = accountInfoPanel;
    }
}
