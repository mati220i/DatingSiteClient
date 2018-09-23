package pl.datingSite.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
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
import org.jboss.resteasy.client.core.executors.ApacheHttpClient4Executor;
import pl.datingSite.model.City;
import pl.datingSite.model.User;
import pl.datingSite.tools.ImageNameGenerator;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

public class FriendsPanelController {
    private Stage stage;
    private AnchorPane mainPanel, loginPanel;
    private User user;
    private EmptyPanelController emptyPanelController;
    private LoginPanelController loginPanelController;
    private MainPanelController mainPanelController;
    private String password;

    private ImageNameGenerator imageNameGenerator;

    private List<City> foundedCtiesFrom, foundedCtiesTo;
    private int choosedCityFrom, choosedCityTo;


    @FXML
    private ImageView avatar, avatarFriend, avatarInvit;
    @FXML
    private Label nameAndSurname, friendsCount, notificationCount, messageCount, fake, fakeInvit;
    @FXML
    private Button settings, logout, friends, notifications, messages;
    @FXML
    private StackPane friendsCounter, notificationsCounter, messagesCounter;
    @FXML
    private ListView friendsList, invitationList;
    @FXML
    private TextField sex, login, name, city, age, sexInvit, loginInvit, nameInvit, cityInvit, ageInvit;
    @FXML
    private HBox info, infoInvit;


    private final String countNotificationUrl = "http://localhost:8090/notification/count?";
    private final String getCityByNameUrl = "http://localhost:8090/city/getByName?";
    private final String generateUrl = "http://localhost:8090/databaseGenerator/generate?";
    private final String generatePrepareUrl = "http://localhost:8090/databaseGenerator/generatePrepare";
    private final String getUsersUrl = "http://localhost:8090/user/getUsers";

    @FXML
    public void initialize() throws Exception {
        info.setVisible(false);
        infoInvit.setVisible(false);
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
        settingsPanelController.refresh();
        emptyPanelController.setScreen(pane);
    }

    @FXML
    public void logout() {
        loginPanelController.clearTextFields();
        emptyPanelController.setScreen(loginPanel);
    }

    @FXML
    public void friends() {

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
        notificationPanelController.refresh();
        emptyPanelController.setScreen(pane);
    }

    @FXML
    public void messages() {

    }


    @FXML
    public void writeMessage() {

    }

    @FXML
    public void wave() {

    }

    @FXML
    public void sendKiss() {

    }

    @FXML
    public void remove() {

    }

    @FXML
    public void viewProfile() {

    }

    @FXML
    public void accept() {

    }

    @FXML
    public void cancel() {

    }

    @FXML
    public void viewProfileInvit() {

    }

    @FXML
    public void back() {

    }

    @FXML
    public void chooseFriend() {

    }

    @FXML
    public void chooseInvitation() {

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
}
