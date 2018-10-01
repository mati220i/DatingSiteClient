package pl.datingSite.controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.client.core.executors.ApacheHttpClient4Executor;
import org.jboss.resteasy.util.GenericType;
import pl.datingSite.model.User;
import pl.datingSite.model.messages.Conversation;
import pl.datingSite.model.messages.Message;
import pl.datingSite.model.messages.MessageHelper;

import javax.ws.rs.core.MediaType;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class MessagePanelController {

    private Stage stage;
    private AnchorPane mainPanel, loginPanel, messagePane;
    private User user;
    private EmptyPanelController emptyPanelController;
    private LoginPanelController loginPanelController;
    private MainPanelController mainPanelController;
    private String password;

    private Set<Conversation> conversations;
    private List<String> unreadedConversations;

    private User conversationWith;

    @FXML
    private ImageView avatar;
    @FXML
    private Label nameAndSurname, friendsCount, notificationCount, messageCount, nameAndLogin;
    @FXML
    private Button settings, logout, friends, notifications, messages;
    @FXML
    private StackPane friendsCounter, notificationsCounter, messagesCounter;
    @FXML
    private ListView messagesList;
    @FXML
    private TextArea areaToWrite;
    @FXML
    private VBox messagePanel;
    @FXML
    private AnchorPane messageArea;
    @FXML
    private ScrollPane scrollPane;


    private final String applicationTestUrl = "http://localhost:8090/test";
    private final String countNotificationUrl = "http://localhost:8090/notification/count?";
    private final String getConversationUrl = "http://localhost:8090/messages/getConversation?";
    private final String newMessageUrl = "http://localhost:8090/messages/newMessage";
    private final String getUserUrl = "http://localhost:8090/user/getUser?";
    private final String readConversationUrl = "http://localhost:8090/messages/readConversation?";


    @FXML
    public void initialize() throws Exception {
        try {
            ClientRequest clientRequest = new ClientRequest(applicationTestUrl);
            clientRequest.get();
        } catch (ConnectException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            ((Stage)alert.getDialogPane().getScene().getWindow()).getIcons().add(new Image("images/logoMini.png"));
            alert.setTitle("Dating Site");
            alert.setContentText("Brak połączenia z serwerem!");

            Optional<ButtonType> result = alert.showAndWait();
            if((result.get() == ButtonType.OK)){
                System.exit(0);
            }
        }
        unreadedConversations = new LinkedList<>();
    }

    public void refresh() throws Exception {
        Random generator = new Random();
        int val = generator.nextInt(16) + 1;
        String path = "images/background/background" + val + ".jpg";
        messagePane.setStyle("-fx-background-image: url('" + path + "'); -fx-background-size: 1200 720; -fx-background-size: cover");

        messagePanel.setVisible(false);
        scrollPane.setVvalue(1);

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

        setConversations();
    }

    public void toWriteMessage() throws Exception {
        messagePanel.setVisible(true);
        nameAndLogin.setText(conversationWith.getName() + " (" + conversationWith.getUsername() + ")");

        if(messagesList.getItems().contains(conversationWith.getUsername())) {
            messagesList.getSelectionModel().select(conversationWith.getUsername());
            chooseConversation();
        }
    }

    private void setConversations() throws Exception {
        DefaultHttpClient client = new DefaultHttpClient();
        Credentials credentials = new UsernamePasswordCredentials(user.getUsername(), password);
        client.getCredentialsProvider().setCredentials(AuthScope.ANY, credentials);
        ApacheHttpClient4Executor executor = new ApacheHttpClient4Executor(client);

        ClientRequest clientRequest = new ClientRequest(getConversationUrl + "username=" + user.getUsername(), executor);
        this.conversations = (Set<Conversation>)clientRequest.get().getEntity(new GenericType<Set<Conversation>>() {});
        checkUnreaded(conversations);
        messagesList.setItems(FXCollections.observableArrayList(prepareConversationList(conversations)));

    }

    @SuppressWarnings("Duplicates")
    private void checkUnreaded(Set<Conversation> conversations) {
        int counter = 0;

        Iterator<Conversation> conversationIterator = conversations.iterator();

        while (conversationIterator.hasNext()) {
            Conversation conversation = conversationIterator.next();
            List<Message> messages = conversation.getMessages();
            if(messages.stream().filter(p -> p.getMessageFrom().equals(conversation.getFromWho()) && p.isReaded() == false).count() >0)
                counter++;
        }
        messageCount.setText(String.valueOf(counter));

        if(messageCount.getText().equals("0"))
            messagesCounter.setVisible(false);

    }

    private User getUser(String username) throws Exception {
        DefaultHttpClient client = new DefaultHttpClient();
        Credentials credentials = new UsernamePasswordCredentials(user.getUsername(), password);
        client.getCredentialsProvider().setCredentials(AuthScope.ANY, credentials);
        ApacheHttpClient4Executor executor = new ApacheHttpClient4Executor(client);

        ClientRequest clientRequest = new ClientRequest(getUserUrl + "login=" + username, executor);
        ClientResponse response = clientRequest.post();
        User user = (User) response.getEntity(User.class);
        return user;
    }

    public List<String> prepareConversationList(Set<Conversation> conversations) {
        Map<String, Date> conversationList = new HashMap<>();
        Iterator<Conversation> iterator = conversations.iterator();

        while (iterator.hasNext()) {
            Conversation conversation = iterator.next();

            String fromWho = conversation.getFromWho();
            List<Message> messages = conversation.getMessages();

            Collections.sort(messages);
            Date lastDate = messages.get(messages.size() - 1).getWhenSent();

            conversationList.put(fromWho, lastDate);
        }

        conversationList = conversationList.entrySet().stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

        List<String> list = new LinkedList<>();
        Set<String> keys = conversationList.keySet();
        Iterator<String> keysIterator = keys.iterator();

        unreadedConversations.clear();

        while (keysIterator.hasNext()) {
            String username = keysIterator.next();
            iterator = conversations.iterator();
            while (iterator.hasNext()) {
                Conversation conv = iterator.next();

                List<Message> messages = conv.getMessages();
                long counter = messages.stream().filter(p -> p.getMessageFrom().equals(conv.getFromWho()) && p.isReaded() == false).count();
                if(counter > 0)
                    unreadedConversations.add(conv.getFromWho());

                if(username.equals(conv.getFromWho())) {
                        list.add(conv.getFromWho());
                }
            }
        }
        setBoldRows();

        return list;
    }

    private void setBoldRows() {

        messagesList.setCellFactory(column -> {
            return new ListCell<String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);

                    if (item == null || empty) {
                        setText(null);
                        setStyle("");
                    } else {
                        setText(item);

                        String username = getListView().getItems().get(getIndex());

                        if (unreadedConversations.contains(username)) {
                            setStyle("-fx-font-weight: bold; -fx-font-size: 18;");
                        } else {
                            setStyle("");
                        }
                    }
                }
            };
        });

    }

    @FXML
    public void chooseConversation() throws Exception {
        if(messagesList.getSelectionModel().getSelectedItem() != null) {
            conversationWith = getUser(messagesList.getSelectionModel().getSelectedItem().toString());
            messagePanel.setVisible(true);
            Iterator<Conversation> iterator = conversations.iterator();
            Conversation conversation = null;
            messageArea.getChildren().clear();

            while (iterator.hasNext()) {
                conversation = iterator.next();
                if(conversation.getFromWho().equals(messagesList.getSelectionModel().getSelectedItem()))
                    break;
            }

            DefaultHttpClient client = new DefaultHttpClient();
            Credentials credentials = new UsernamePasswordCredentials(user.getUsername(), password);
            client.getCredentialsProvider().setCredentials(AuthScope.ANY, credentials);
            ApacheHttpClient4Executor executor = new ApacheHttpClient4Executor(client);


            ClientRequest clientRequest = new ClientRequest(readConversationUrl + "whose=" + conversation.getWhose() + "&fromWho=" + conversation.getFromWho(), executor);
            clientRequest.put();

            unreadedConversations.remove(conversationWith.getUsername());
            setConversations();

            User user = getUser(conversation.getFromWho());
            nameAndLogin.setText(user.getName() + " (" + user.getUsername() + ")");

            VBox messageBox = new VBox(25);
            messageBox.setPrefWidth(780);
            messageBox.setPrefHeight(0);
            messageBox.setAlignment(Pos.TOP_CENTER);

            Collections.sort(conversation.getMessages());
            Iterator<Message> messageIterator = conversation.getMessages().iterator();

            while (messageIterator.hasNext()) {
                Message message = messageIterator.next();

                VBox chatBubble = new VBox();

                SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm");
                String date = format.format(message.getWhenSent());


                Label dateLabel = new Label(date);
                dateLabel.setFont(Font.font("Comic Sans MS", 15));

                Text text = new Text(message.getMessage());
                text.setFont(Font.font("Comic Sans MS", 25));
                text.setWrappingWidth(430);


                ImageView imageView = new ImageView();
                if(message.isReaded())
                    imageView.setImage(new Image("images/readed.png"));
                else
                    imageView.setImage(new Image("images/notReaded.png"));

                imageView.setFitWidth(25);
                imageView.setFitHeight(25);

                VBox messageBubble = new VBox();

                messageBubble.setMaxWidth(450);
                chatBubble.setMaxWidth(780);

                if(message.getMessageFrom().equals(this.user.getUsername())) {
                    chatBubble.setAlignment(Pos.CENTER_RIGHT);
                    text.setTextAlignment(TextAlignment.RIGHT);
                    messageBubble.setPadding(new Insets(10));
                    messageBubble.setAlignment(Pos.CENTER_RIGHT);
                    messageBubble.setStyle("-fx-background-color: #37a1ee; -fx-background-radius: 40");
                    messageBubble.getChildren().addAll(dateLabel, text, imageView);
                } else {
                    chatBubble.setAlignment(Pos.CENTER_LEFT);
                    text.setTextAlignment(TextAlignment.LEFT);
                    messageBubble.setPadding(new Insets(10));
                    messageBubble.setAlignment(Pos.CENTER_LEFT);
                    messageBubble.setStyle("-fx-background-color: #70f467; -fx-background-radius: 40");
                    messageBubble.getChildren().addAll(dateLabel, text);
                }
                messageBubble.applyCss();
                chatBubble.getChildren().addAll(messageBubble);
                messageBox.getChildren().addAll(chatBubble);
            }
            messageArea.getChildren().addAll(messageBox);
            messageArea.applyCss();
            messageArea.setPrefHeight(getChatBubbleSize());
            scrollPane.layout();
            scrollPane.setVvalue(1);
        }
    }

    private double getChatBubbleSize() {
        VBox vbox = (VBox)messageArea.getChildren().get(0);
        vbox.layout();
        int qnt = vbox.getChildren().size();
        double height = 0;
        for(int i = 0; i < qnt; i++) {
            VBox box = (VBox)vbox.getChildren().get(i);
            box.layout();
            height += box.getBoundsInParent().getHeight() + vbox.getSpacing();
        }
        return height;
    }

    @FXML
    public void send() throws Exception {
        String text = areaToWrite.getText();
        Message message = new Message(user.getUsername(), text);

        MessageHelper helper = new MessageHelper(user.getUsername(), conversationWith.getUsername(), message);

        DefaultHttpClient client = new DefaultHttpClient();
        Credentials credentials = new UsernamePasswordCredentials(user.getUsername(), password);
        client.getCredentialsProvider().setCredentials(AuthScope.ANY, credentials);
        ApacheHttpClient4Executor executor = new ApacheHttpClient4Executor(client);

        ClientRequest clientRequest = new ClientRequest(newMessageUrl, executor);
        clientRequest.body(MediaType.APPLICATION_JSON, helper);
        this.conversations = (Set<Conversation>)clientRequest.put().getEntity(new GenericType<Set<Conversation>>() {});
        refresh();
        messagePanel.setVisible(true);
        areaToWrite.clear();
        if(messagesList.getItems().contains(conversationWith.getUsername())) {
            messagesList.getSelectionModel().select(conversationWith.getUsername());
            chooseConversation();
        } else
            chooseConversation();
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
        settingsPanelController.setLoginPanelController(loginPanelController);
        settingsPanelController.setLoginPanel(loginPanel);
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
        friendsPanelController.setUser(user);
        friendsPanelController.setEmptyPanelController(emptyPanelController);
        friendsPanelController.setMainPanel(mainPanel);
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
        notificationPanelController.setUser(user);
        notificationPanelController.setEmptyPanelController(emptyPanelController);
        notificationPanelController.setLoginPanel(loginPanel);
        notificationPanelController.setLoginPanelController(loginPanelController);
        notificationPanelController.setMainPanelController(mainPanelController);
        notificationPanelController.setStage(stage);
        notificationPanelController.setPassword(password);
        notificationPanelController.setNotificationPanel(pane);
        notificationPanelController.refresh();
        emptyPanelController.setScreen(pane);
    }

    @FXML
    public void messages() {

    }

    @FXML
    public void back() throws Exception {
        mainPanelController.refresh();
        mainPanelController.clear();
        emptyPanelController.setScreen(mainPanel);
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

    public void setConversationWith(User conversationWith) {
        this.conversationWith = conversationWith;
    }

    public void setMessagePane(AnchorPane messagePane) {
        this.messagePane = messagePane;
    }
}
