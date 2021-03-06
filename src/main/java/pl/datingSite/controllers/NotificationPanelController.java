package pl.datingSite.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.core.executors.ApacheHttpClient4Executor;
import org.jboss.resteasy.util.GenericType;
import pl.datingSite.model.Notification;
import pl.datingSite.model.User;
import pl.datingSite.model.messages.Conversation;
import pl.datingSite.model.messages.Message;

import javax.ws.rs.core.MediaType;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.util.*;

public class NotificationPanelController {

    private Stage stage;
    private AnchorPane mainPanel, loginPanel, notificationPanel;
    private User user;
    private EmptyPanelController emptyPanelController;
    private LoginPanelController loginPanelController;
    private MainPanelController mainPanelController;
    private String password;

    private Set<Notification> notificationsList;
    private Set<Conversation> conversations;

    @FXML
    private ImageView avatar;
    @FXML
    private Label nameAndSurname, friendsCount, notificationCount, messageCount, topic, date;
    @FXML
    private Button settings, logout, friends, notifications, messages, delete;
    @FXML
    private StackPane friendsCounter, notificationsCounter, messagesCounter;
    @FXML
    private TableView<Notification> notificationTable;
    @FXML
    private TableColumn topicColumn, dateColumn;
    @FXML
    private TextFlow content;
    @FXML
    private Separator line, line2;


    static final Logger logger = Logger.getLogger(NotificationPanelController.class);

    private final String applicationTestUrl = "http://localhost:8090/test";
    private final String countNotificationUrl = "http://localhost:8090/notification/count?";
    private final String getNotificationsUrl = "http://localhost:8090/notification/getAll?";
    private final String readNotificationsUrl = "http://localhost:8090/notification/read?";
    private final String deleteNotificationsUrl = "http://localhost:8090/notification/delete";
    private final String getUserUrl = "http://localhost:8090/user/getUserWithAllData?";
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
            alert.setHeaderText(null);
            alert.setContentText("Brak połączenia z serwerem!");
            alert.setTitle("Dating Site");
            logger.error("Brak połączenia z serwerem");

            Optional<ButtonType> result = alert.showAndWait();
            if((result.get() == ButtonType.OK)){
                System.exit(0);
            }
        }

        topic.setVisible(false);
        date.setVisible(false);
        content.setVisible(false);
        line.setVisible(false);
        line2.setVisible(false);
        delete.setVisible(false);
    }

    public void refresh() throws Exception {
        Random generator = new Random();
        int val = generator.nextInt(16) + 1;
        String path = "images/background/background" + val + ".jpg";
        notificationPanel.setStyle("-fx-background-image: url('" + path + "'); -fx-background-size: 1200 720; -fx-background-size: cover");

        nameAndSurname.setText(user.getName() + " " + user.getSurname());

        if(user.getAvatar() != null) {
            avatar.setImage(new Image(new ByteArrayInputStream(user.getAvatar())));
        } else {
            avatar.setImage(new Image("images/noFoto.png"));
        }


        getNotification();
        setBoldRows();
        setCounters();
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

    private void getNotification() throws Exception {
        DefaultHttpClient client = new DefaultHttpClient();
        Credentials credentials = new UsernamePasswordCredentials(user.getUsername(), password);
        client.getCredentialsProvider().setCredentials(AuthScope.ANY, credentials);
        ApacheHttpClient4Executor executor = new ApacheHttpClient4Executor(client);

        ClientRequest clientRequest = new ClientRequest(getNotificationsUrl + "username=" + user.getUsername(), executor);
        notificationsList = (Set<Notification>)clientRequest.get().getEntity(new GenericType<Set<Notification>>() {});
        setDataInTable(notificationsList);
    }

    private void setBoldRows() {
        topicColumn.setCellFactory(column -> {
            return new TableCell<Notification, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);

                    if (item == null || empty) {
                        setText(null);
                        setStyle("");
                    } else {

                        setText(item);

                        Notification notification = getTableView().getItems().get(getIndex());

                        if (!notification.isReaded()) {
                            setStyle("-fx-font-weight: bold; -fx-font-size: 18;");
                        } else {
                            setStyle("");
                        }
                    }
                }
            };
        });

        dateColumn.setCellFactory(topicColumn.getCellFactory());
    }

    private void setCounters() throws Exception {
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

        getNotification();
        setBoldRows();

        if(friendsCount.getText().equals("0"))
            friendsCounter.setVisible(false);
        if(notificationCount.getText().equals("0"))
            notificationsCounter.setVisible(false);
    }

    private void setDataInTable(Set<Notification> notificationsList) {
        ObservableList<Notification> list = FXCollections.observableArrayList(notificationsList);

        topicColumn.setCellValueFactory(new PropertyValueFactory<Notification, String>("topic"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<Notification, String>("receiveDate"));

        notificationTable.setItems(list);

    }

    @FXML
    public void chooseNotification() throws Exception {
        Notification notification = notificationTable.getSelectionModel().getSelectedItem();
        if(notification != null) {
            topic.setVisible(true);
            date.setVisible(true);
            content.setVisible(true);
            line.setVisible(true);
            line2.setVisible(true);
            delete.setVisible(true);

            topic.setText(notification.getTopic());
            date.setText(notification.getReceiveDate());

            if(notification.getTopic().split(" ")[0].equals("Użytkownik")) {
                String[] data = notification.getContent().split("'");

                Hyperlink linkToProfile = new Hyperlink(String.valueOf(data[1]));

                User userToView = getUserToView(data[1]);

                linkToProfile.setOnAction(event -> {
                    viewProfile(userToView);
                });

                Text text = new Text( data[0] + " (");
                Text tex2 = new Text(") " + data[2]);
                content.getChildren().clear();
                content.getChildren().addAll(text, linkToProfile, tex2);
            } else {
                Text text = new Text(notification.getContent());
                content.getChildren().clear();
                content.getChildren().addAll(text);
            }
            if(!notification.isReaded()) {
                DefaultHttpClient client = new DefaultHttpClient();
                Credentials credentials = new UsernamePasswordCredentials(user.getUsername(), password);
                client.getCredentialsProvider().setCredentials(AuthScope.ANY, credentials);
                ApacheHttpClient4Executor executor = new ApacheHttpClient4Executor(client);

                ClientRequest clientRequest = new ClientRequest(readNotificationsUrl + "id=" + notification.getId(), executor);
                clientRequest.put();

                setCounters();
            }
        } else {
            displayAlert("Nic nie zostało wybrane!", Alert.AlertType.WARNING);
        }
    }

    private User getUserToView(String username) throws Exception {
        DefaultHttpClient client = new DefaultHttpClient();
        Credentials credentials = new UsernamePasswordCredentials(user.getUsername(), password);
        client.getCredentialsProvider().setCredentials(AuthScope.ANY, credentials);
        ApacheHttpClient4Executor executor = new ApacheHttpClient4Executor(client);

        ClientRequest clientRequest = new ClientRequest(getUserUrl + "login=" + username, executor);
        return (User) clientRequest.post().getEntity(User.class);
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
            accountInfoPanelController.setMainPanelController(mainPanelController);
            accountInfoPanelController.setPassword(password);
            accountInfoPanelController.setStage(stage);
            accountInfoPanelController.setAccountInfoPanel(pane);
            accountInfoPanelController.refresh();
            emptyPanelController.setScreen(pane);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean displayAlert(String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle("Dating Site");
        ((Stage)alert.getDialogPane().getScene().getWindow()).getIcons().add(new Image("images/logoMini.png"));
        alert.setHeaderText(null);
        alert.setContentText(content);

        Optional<ButtonType> result = alert.showAndWait();
        if(alertType.equals(Alert.AlertType.WARNING)) {
            if ((result.get() == ButtonType.OK)) {
                return true;
            }
        } else if (alertType.equals(Alert.AlertType.CONFIRMATION)) {
            if(result.get() == ButtonType.OK) {
                return true;
            }
        }

        return false;
    }

    @FXML
    public void settings() throws Exception{
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
        friendsPanelController.setLoginPanel(loginPanel);
        friendsPanelController.setUser(user);
        friendsPanelController.setLoginPanelController(loginPanelController);
        friendsPanelController.setMainPanelController(mainPanelController);
        friendsPanelController.setPassword(password);
        friendsPanelController.setStage(stage);
        friendsPanelController.setFriendsPanel(pane);
        friendsPanelController.refresh();
        emptyPanelController.setScreen(pane);
    }

    @FXML
    public void notifications() {

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
        messagePanelController.setLoginPanelController(loginPanelController);
        messagePanelController.setEmptyPanelController(emptyPanelController);
        messagePanelController.setLoginPanel(loginPanel);
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

    @FXML
    public void delete() throws Exception{
        Notification notification = notificationTable.getSelectionModel().getSelectedItem();
        if(notification == null) {
            displayAlert("Nic nie zostało wybrane!", Alert.AlertType.WARNING);
        } else {
            if(displayAlert("Usunąć powiadomienie?", Alert.AlertType.CONFIRMATION)) {
                DefaultHttpClient client = new DefaultHttpClient();
                Credentials credentials = new UsernamePasswordCredentials(user.getUsername(), password);
                client.getCredentialsProvider().setCredentials(AuthScope.ANY, credentials);
                ApacheHttpClient4Executor executor = new ApacheHttpClient4Executor(client);

                ClientRequest clientRequest = new ClientRequest(deleteNotificationsUrl, executor);
                clientRequest.body(MediaType.APPLICATION_JSON, notification);
                clientRequest.delete();
                refresh();

                topic.setVisible(false);
                date.setVisible(false);
                content.setVisible(false);
                line.setVisible(false);
                line2.setVisible(false);
                delete.setVisible(false);

            }

        }

    }

    public void setMainPanel(AnchorPane mainPanel) {
        this.mainPanel= mainPanel;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setEmptyPanelController(EmptyPanelController emptyPanelController) {
        this.emptyPanelController = emptyPanelController;
    }

    public void setLoginPanel(AnchorPane loginPanel) {
        this.loginPanel = loginPanel;
    }

    public void setLoginPanelController(LoginPanelController loginPanelController) {
        this.loginPanelController = loginPanelController;
    }

    public void setMainPanelController(MainPanelController mainPanelController) {
        this.mainPanelController = mainPanelController;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setNotificationPanel(AnchorPane notificationPanel) {
        this.notificationPanel = notificationPanel;
    }
}
