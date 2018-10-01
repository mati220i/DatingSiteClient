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
import javafx.stage.Stage;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.client.core.executors.ApacheHttpClient4Executor;
import org.jboss.resteasy.util.GenericType;
import pl.datingSite.model.City;
import pl.datingSite.model.Friends;
import pl.datingSite.model.User;
import pl.datingSite.model.messages.Conversation;
import pl.datingSite.model.messages.Message;
import pl.datingSite.tools.ImageNameGenerator;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.*;

public class FriendsPanelController {
    private Stage stage;
    private AnchorPane mainPanel, loginPanel, friendsPanel;
    private User user;
    private EmptyPanelController emptyPanelController;
    private LoginPanelController loginPanelController;
    private MainPanelController mainPanelController;
    private String password;
    private Friends userFriends;

    private ImageNameGenerator imageNameGenerator;

    private List<City> foundedCtiesFrom, foundedCtiesTo;
    private int choosedCityFrom, choosedCityTo;
    private Set<Conversation> conversations;


    @FXML
    private ImageView avatar, avatarFriend, avatarInvit, avatarSendInvit;
    @FXML
    private Label nameAndSurname, friendsCount, notificationCount, messageCount, fake, fakeInvit, fakeSendInvit;
    @FXML
    private Button settings, logout, friends, notifications, messages;
    @FXML
    private StackPane friendsCounter, notificationsCounter, messagesCounter;
    @FXML
    private ListView friendsList, invitationList, sendInvitationList;
    @FXML
    private TextField sex, login, name, city, age, sexInvit, loginInvit, nameInvit, cityInvit, ageInvit, sexSendInvit, loginSendInvit, nameSendInvit, citySendInvit, ageSendInvit;
    @FXML
    private HBox info, infoInvit, infoSendInvit;
    @FXML
    private Tab invitationTab, yourFriends, sentInvitations;


    private final String applicationTestUrl = "http://localhost:8090/test";
    private final String countNotificationUrl = "http://localhost:8090/notification/count?";
    private final String getUserUrl = "http://localhost:8090/user/getUser?";
    private final String countInvitationsUrl = "http://localhost:8090/friends/count?";
    private final String getFriendsUrl = "http://localhost:8090/friends/getFriends?";
    private final String newWaveNotificationUrl = "http://localhost:8090/notification/newWave?";
    private final String newKissNotificationUrl = "http://localhost:8090/notification/newKiss?";
    private final String undoInvitationUrl = "http://localhost:8090/friends/undoInvitation?";
    private final String cancelInvitationUrl = "http://localhost:8090/friends/cancelInvitation?";
    private final String acceptInvitationUrl = "http://localhost:8090/friends/acceptInvitation?";
    private final String removeFriendUrl = "http://localhost:8090/friends/removeFriend?";
    private final String getConversationUrl = "http://localhost:8090/messages/getConversation?";

    @FXML
    public void initialize() throws Exception {
        try {
            ClientRequest clientRequest = new ClientRequest(applicationTestUrl);
            clientRequest.get();
        } catch (ConnectException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            ((Stage)alert.getDialogPane().getScene().getWindow()).getIcons().add(new Image("images/logoMini.png"));
            alert.setHeaderText(null);
            alert.setTitle("Dating Site");
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
        friendsPanel .setStyle("-fx-background-image: url('" + path + "'); -fx-background-size: 1200 720; -fx-background-size: cover");


        info.setVisible(false);
        infoInvit.setVisible(false);
        infoSendInvit.setVisible(false);

        fake.setVisible(false);
        fakeInvit.setVisible(false);
        fakeSendInvit.setVisible(false);

        friendsList.getItems().clear();
        sendInvitationList.getItems().clear();
        invitationList.getItems().clear();

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

        clientRequest = new ClientRequest(countInvitationsUrl + "username=" + user.getUsername(), executor);
        Integer invitationQuantity = (Integer)clientRequest.get().getEntity(Integer.class);
        friendsCount.setText(invitationQuantity.toString());



        clientRequest = new ClientRequest(getFriendsUrl + "username=" + user.getUsername(), executor);
        this.userFriends = (Friends) clientRequest.get().getEntity(Friends.class);

        setYourFriendsTab();
        setInvitationsTab();
        setSentTab();

        if(friendsCount.getText().equals("0"))
            friendsCounter.setVisible(false);
        if(notificationCount.getText().equals("0"))
            notificationsCounter.setVisible(false);

        setConversations();
    }

    @SuppressWarnings("Duplicates")
    private void setConversations() throws Exception {
        DefaultHttpClient client = new DefaultHttpClient();
        Credentials credentials = new UsernamePasswordCredentials(user.getUsername(), password);
        client.getCredentialsProvider().setCredentials(AuthScope.ANY, credentials);
        ApacheHttpClient4Executor executor = new ApacheHttpClient4Executor(client);

        ClientRequest clientRequest = new ClientRequest(getConversationUrl + "username=" + user.getUsername(), executor);
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

    private void setYourFriendsTab() {
        if(!userFriends.getFriendsUsernames().isEmpty()) {
            ObservableList<String> list = FXCollections.observableArrayList(userFriends.getFriendsUsernames());
            friendsList.setItems(list);
            yourFriends.setText("Twoi znajomi (" + list.size() + ")");
        } else
            yourFriends.setText("Twoi znajomi (0)");
    }

    private void setInvitationsTab() {
        if(!userFriends.getInvitationsFrom().isEmpty()) {
            ObservableList<String> list = FXCollections.observableArrayList(userFriends.getInvitationsFrom());
            invitationList.setItems(list);
            invitationTab.setText("Zaproszenia (" + list.size() + ")");
        } else
            invitationTab.setText("Zaproszenia (0)");
    }

    private void setSentTab() {
        if(!userFriends.getSendInvitations().isEmpty()) {
            ObservableList<String> list = FXCollections.observableArrayList(userFriends.getSendInvitations());
            sendInvitationList.setItems(list);
            sentInvitations.setText("Wysłane (" + list.size() + ")");
        } else
            sentInvitations.setText("Wysłane (0)");
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
        settingsPanelController.setUser(user);
        settingsPanelController.setLoginPanel(loginPanel);
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
        friendsPanelController.setEmptyPanelController(emptyPanelController);
        friendsPanelController.setUser(user);
        friendsPanelController.setLoginPanel(loginPanel);
        friendsPanelController.setPassword(password);
        friendsPanelController.setLoginPanelController(loginPanelController);
        friendsPanelController.setMainPanelController(mainPanelController);
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
        notificationPanelController.setStage(stage);
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
        messagePanelController.setUser(user);
        messagePanelController.setEmptyPanelController(emptyPanelController);
        messagePanelController.setLoginPanelController(loginPanelController);
        messagePanelController.setLoginPanel(loginPanel);
        messagePanelController.setMainPanelController(mainPanelController);
        messagePanelController.setPassword(password);
        messagePanelController.setStage(stage);
        messagePanelController.setMessagePane(pane);
        messagePanelController.refresh();
        emptyPanelController.setScreen(pane);
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
        messagePanelController.setUser(user);
        messagePanelController.setEmptyPanelController(emptyPanelController);
        messagePanelController.setLoginPanel(loginPanel);
        messagePanelController.setLoginPanelController(loginPanelController);
        messagePanelController.setMainPanelController(mainPanelController);
        messagePanelController.setConversationWith(getUser(friendsList.getSelectionModel().getSelectedItem().toString()));
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
        Credentials credentials = new UsernamePasswordCredentials(this.user.getUsername(), password);
        client.getCredentialsProvider().setCredentials(AuthScope.ANY, credentials);
        ApacheHttpClient4Executor executor = new ApacheHttpClient4Executor(client);

        User user = getUser(friendsList.getSelectionModel().getSelectedItem().toString());

        ClientRequest clientRequest = new ClientRequest(newWaveNotificationUrl + "from=" + this.user.getUsername() + "&to=" + user.getUsername(), executor);
        ClientResponse clientResponse = clientRequest.put();
        if(clientResponse.getStatus() == 200) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText(null);
            ((Stage)alert.getDialogPane().getScene().getWindow()).getIcons().add(new Image("images/logoMini.png"));
            alert.setTitle("Dating Site");
            alert.setContentText("Pomachałeś użytkownikowi " + user.getName());

            Optional<ButtonType> result = alert.showAndWait();
            if((result.get() == ButtonType.OK)){

            }
        }

    }

    @FXML
    public void sendKiss() throws Exception {
        DefaultHttpClient client = new DefaultHttpClient();
        Credentials credentials = new UsernamePasswordCredentials(this.user.getUsername(), password);
        client.getCredentialsProvider().setCredentials(AuthScope.ANY, credentials);
        ApacheHttpClient4Executor executor = new ApacheHttpClient4Executor(client);

        User user = getUser(friendsList.getSelectionModel().getSelectedItem().toString());

        ClientRequest clientRequest = new ClientRequest(newKissNotificationUrl + "from=" + this.user.getUsername() + "&to=" + user.getUsername(), executor);
        ClientResponse clientResponse = clientRequest.put();
        if(clientResponse.getStatus() == 200) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText(null);
            ((Stage)alert.getDialogPane().getScene().getWindow()).getIcons().add(new Image("images/logoMini.png"));
            alert.setTitle("Dating Site");
            alert.setContentText("Całus został wysłany użytkownikowi " + user.getName());

            Optional<ButtonType> result = alert.showAndWait();
            if((result.get() == ButtonType.OK)){

            }
        }
    }

    @FXML
    public void remove() throws Exception {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Dating Site");
        ((Stage)alert.getDialogPane().getScene().getWindow()).getIcons().add(new Image("images/logoMini.png"));
        alert.setHeaderText(null);
        alert.setContentText("Usunąć znajomego?");

        Optional<ButtonType> result = alert.showAndWait();
        if((result.get() == ButtonType.OK)){
            String who = friendsList.getSelectionModel().getSelectedItem().toString();

            DefaultHttpClient client = new DefaultHttpClient();

            Credentials credentials = new UsernamePasswordCredentials(this.user.getUsername(), password);
            client.getCredentialsProvider().setCredentials(AuthScope.ANY, credentials);
            ApacheHttpClient4Executor executor = new ApacheHttpClient4Executor(client);


            ClientRequest clientRequest = new ClientRequest(removeFriendUrl + "who=" + who + "&from=" + this.user.getUsername(), executor);
            userFriends = (Friends) clientRequest.put().getEntity(Friends.class);

            refresh();
        }
    }

    @FXML
    public void viewProfile() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("AccountInfoPanel.fxml"));
        AnchorPane pane = null;
        try {
            pane = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        User user = getUser(friendsList.getSelectionModel().getSelectedItem().toString());

        AccountInfoPanelController accountInfoPanelController = loader.getController();
        accountInfoPanelController.setMainPanel(mainPanel);
        accountInfoPanelController.setLoggedUser(this.user);
        accountInfoPanelController.setEmptyPanelController(emptyPanelController);
        accountInfoPanelController.setUserToView(user);
        accountInfoPanelController.setLoginPanel(loginPanel);
        accountInfoPanelController.setLoginPanelController(loginPanelController);
        accountInfoPanelController.setPassword(password);
        accountInfoPanelController.setMainPanelController(mainPanelController);
        accountInfoPanelController.setStage(stage);
        accountInfoPanelController.setAccountInfoPanel(pane);
        accountInfoPanelController.refresh();
        emptyPanelController.setScreen(pane);
    }

    @FXML
    public void accept() throws Exception {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Dating Site");
        ((Stage)alert.getDialogPane().getScene().getWindow()).getIcons().add(new Image("images/logoMini.png"));
        alert.setHeaderText(null);
        alert.setContentText("Akceptować zaproszenie?");

        Optional<ButtonType> result = alert.showAndWait();
        if((result.get() == ButtonType.OK)){
            String user = invitationList.getSelectionModel().getSelectedItem().toString();

            DefaultHttpClient client = new DefaultHttpClient();
            Credentials credentials = new UsernamePasswordCredentials(this.user.getUsername(), password);
            client.getCredentialsProvider().setCredentials(AuthScope.ANY, credentials);
            ApacheHttpClient4Executor executor = new ApacheHttpClient4Executor(client);


            ClientRequest clientRequest = new ClientRequest(acceptInvitationUrl + "from=" + user + "&to=" + this.user.getUsername(), executor);
            userFriends = (Friends) clientRequest.put().getEntity(Friends.class);

            refresh();
        }
    }

    @FXML
    public void cancel() throws Exception {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Dating Site");
        ((Stage)alert.getDialogPane().getScene().getWindow()).getIcons().add(new Image("images/logoMini.png"));
        alert.setHeaderText(null);
        alert.setContentText("Odrzucić zaproszenie?");

        Optional<ButtonType> result = alert.showAndWait();
        if((result.get() == ButtonType.OK)){
            DefaultHttpClient client = new DefaultHttpClient();
            Credentials credentials = new UsernamePasswordCredentials(this.user.getUsername(), password);

            client.getCredentialsProvider().setCredentials(AuthScope.ANY, credentials);
            ApacheHttpClient4Executor executor = new ApacheHttpClient4Executor(client);

            String user = invitationList.getSelectionModel().getSelectedItem().toString();

            ClientRequest clientRequest = new ClientRequest(cancelInvitationUrl + "from=" + user + "&to=" + this.user.getUsername(), executor);
            userFriends = (Friends) clientRequest.put().getEntity(Friends.class);
            refresh();
        }
    }

    @FXML
    public void viewProfileInvit() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("AccountInfoPanel.fxml"));
        AnchorPane pane = null;
        try {
            pane = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        User user = getUser(invitationList.getSelectionModel().getSelectedItem().toString());

        AccountInfoPanelController accountInfoPanelController = loader.getController();
        accountInfoPanelController.setMainPanel(mainPanel);
        accountInfoPanelController.setLoggedUser(this.user);
        accountInfoPanelController.setUserToView(user);
        accountInfoPanelController.setEmptyPanelController(emptyPanelController);
        accountInfoPanelController.setLoginPanel(loginPanel);
        accountInfoPanelController.setLoginPanelController(loginPanelController);
        accountInfoPanelController.setPassword(password);
        accountInfoPanelController.setMainPanelController(mainPanelController);
        accountInfoPanelController.setStage(stage);
        accountInfoPanelController.setAccountInfoPanel(pane);
        accountInfoPanelController.refresh();
        emptyPanelController.setScreen(pane);
    }

    @FXML
    public void viewProfileSent() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("AccountInfoPanel.fxml"));
        AnchorPane pane = null;
        try {
            pane = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        User user = getUser(sendInvitationList.getSelectionModel().getSelectedItem().toString());

        AccountInfoPanelController accountInfoPanelController = loader.getController();
        accountInfoPanelController.setMainPanel(mainPanel);
        accountInfoPanelController.setUserToView(user);
        accountInfoPanelController.setLoggedUser(this.user);
        accountInfoPanelController.setEmptyPanelController(emptyPanelController);
        accountInfoPanelController.setLoginPanel(loginPanel);
        accountInfoPanelController.setLoginPanelController(loginPanelController);
        accountInfoPanelController.setPassword(password);
        accountInfoPanelController.setMainPanelController(mainPanelController);
        accountInfoPanelController.setStage(stage);
        accountInfoPanelController.refresh();
        emptyPanelController.setScreen(pane);
    }

    @FXML
    public void undoInvitation() throws Exception {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Dating Site");
        ((Stage)alert.getDialogPane().getScene().getWindow()).getIcons().add(new Image("images/logoMini.png"));
        alert.setHeaderText(null);
        alert.setContentText("Anulować zaproszenie?");

        Optional<ButtonType> result = alert.showAndWait();
        if((result.get() == ButtonType.OK)){
            DefaultHttpClient client = new DefaultHttpClient();
            Credentials credentials = new UsernamePasswordCredentials(this.user.getUsername(), password);
            client.getCredentialsProvider().setCredentials(AuthScope.ANY, credentials);
            ApacheHttpClient4Executor executor = new ApacheHttpClient4Executor(client);

            String user = sendInvitationList.getSelectionModel().getSelectedItem().toString();

            ClientRequest clientRequest = new ClientRequest(undoInvitationUrl + "from=" + this.user.getUsername() + "&to=" + user, executor);
            userFriends = (Friends) clientRequest.put().getEntity(Friends.class);
            refresh();
        }

    }

    @FXML
    public void back() throws Exception {
        mainPanelController.refresh();
        mainPanelController.clear();
        emptyPanelController.setScreen(mainPanel);
    }

    @FXML
    public void chooseFriend() throws Exception {
        if(friendsList.getSelectionModel().getSelectedItem() != null) {
            info.setVisible(true);
            User user = getUser(friendsList.getSelectionModel().getSelectedItem().toString());

            if(user.getAvatar() != null)
                avatarFriend.setImage(new Image(new ByteArrayInputStream(user.getAvatar())));
            else
                avatarFriend.setImage(new Image("images/noFoto.png"));

            sex.setText(user.getSex());
            login.setText(user.getUsername());
            city.setText(getCityInfo(user.getCity()));
            name.setText(user.getName());
            age.setText(String.valueOf(getAge(user.getDateOfBirth())));
            if(user.isFake())
                fake.setVisible(true);
        }
    }

    @FXML
    public void chooseInvitation() throws Exception {
        if(invitationList.getSelectionModel().getSelectedItem() != null) {
            infoInvit.setVisible(true);
            User user = getUser(invitationList.getSelectionModel().getSelectedItem().toString());

            if(user.getAvatar() != null)
                avatarInvit.setImage(new Image(new ByteArrayInputStream(user.getAvatar())));
            else
                avatarInvit.setImage(new Image("images/noFoto.png"));

            loginInvit.setText(user.getUsername());
            sexInvit.setText(user.getSex());
            nameInvit.setText(user.getName());
            cityInvit.setText(getCityInfo(user.getCity()));
            ageInvit.setText(String.valueOf(getAge(user.getDateOfBirth())));
            if(user.isFake())
                fakeInvit.setVisible(true);
        }
    }

    @FXML
    public void chooseSendInvitation() throws Exception {
        if(sendInvitationList.getSelectionModel().getSelectedItem() != null) {
            infoSendInvit.setVisible(true);
            User user = getUser(sendInvitationList.getSelectionModel().getSelectedItem().toString());

            if(user.getAvatar() != null)
                avatarSendInvit.setImage(new Image(new ByteArrayInputStream(user.getAvatar())));
            else
                avatarSendInvit.setImage(new Image("images/noFoto.png"));

            loginSendInvit.setText(user.getUsername());
            nameSendInvit.setText(user.getName());
            sexSendInvit.setText(user.getSex());
            citySendInvit.setText(getCityInfo(user.getCity()));
            ageSendInvit.setText(String.valueOf(getAge(user.getDateOfBirth())));
            if(user.isFake())
                fakeSendInvit.setVisible(true);
        }
    }

    private Integer getAge(Date dateOfBirth) {
        LocalDate now = LocalDate.now();
        LocalDate birth = dateOfBirth.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        Period period = Period.between(birth, now);
        return period.getYears();
    }

    private String getCityInfo(City city) {
        return city.getName() + ", woj. " + city.getVoivodeship() + ", powiat " + city.getCountry() + ", gmina " + city.getCommunity();
    }

    private User getUser(String username) throws Exception {
        DefaultHttpClient client = new DefaultHttpClient();
        Credentials credentials = new UsernamePasswordCredentials(user.getUsername(), password);
        client.getCredentialsProvider().setCredentials(AuthScope.ANY, credentials);
        ApacheHttpClient4Executor executor = new ApacheHttpClient4Executor(client);

        ClientRequest clientRequest = new ClientRequest(getUserUrl + "login=" + username, executor);
        User user = (User) clientRequest.post().getEntity(User.class);
        return user;
    }

    public void setMainPanelController(MainPanelController mainPanelController) {
        this.mainPanelController = mainPanelController;
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

    public void setFriendsPanel(AnchorPane friendsPanel) {
        this.friendsPanel = friendsPanel;
    }
}
