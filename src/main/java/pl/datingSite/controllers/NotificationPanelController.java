package pl.datingSite.controllers;

import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableBooleanValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.core.executors.ApacheHttpClient4Executor;
import org.jboss.resteasy.util.GenericType;
import pl.datingSite.model.Notification;
import pl.datingSite.model.User;

import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class NotificationPanelController {

    private Stage stage;
    private AnchorPane mainPanel, loginPanel;
    private User user;
    private EmptyPanelController emptyPanelController;
    private LoginPanelController loginPanelController;
    private MainPanelController mainPanelController;
    private String password;

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
    private TextArea content;
    @FXML
    private Separator line, line2;

    private final String countNotificationUrl = "http://localhost:8090/notification/count?";
    private final String getNotificationsUrl = "http://localhost:8090/notification/getAll?";
    private final String readNotificationsUrl = "http://localhost:8090/notification/read?";
    private final String deleteNotificationsUrl = "http://localhost:8090/notification/delete";

    private Set<Notification> notificationsList;

    @FXML
    public void initialize() {
        topic.setVisible(false);
        date.setVisible(false);
        content.setVisible(false);
        line.setVisible(false);
        line2.setVisible(false);
        delete.setVisible(false);
    }

    public void refresh() throws Exception {
        nameAndSurname.setText(user.getName() + " " + user.getSurname());


        getNotification();
        setBoldRows();
        setCounters();
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

        getNotification();
        setBoldRows();

        if(friendsCount.getText().equals("0"))
            friendsCounter.setVisible(false);
        if(notificationCount.getText().equals("0"))
            notificationsCounter.setVisible(false);
        if(messageCount.getText().equals("0"))
            messagesCounter.setVisible(false);
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
            content.setText(notification.getContent());

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

    private boolean displayAlert(String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle("Dating Site");
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
    public void notifications() {

    }

    @FXML
    public void messages() {

    }

    @FXML
    public void back() throws Exception {
        mainPanelController.refresh();
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
}
