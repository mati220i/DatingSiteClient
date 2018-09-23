package pl.datingSite.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
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
import pl.datingSite.model.User;
import pl.datingSite.tools.CSVReader;
import pl.datingSite.tools.DatabaseGenerator;

import java.io.File;
import java.io.IOException;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;


public class LoginPanelController {

    private EmptyPanelController emptyPanelController;
    private AnchorPane loginPane;
    private Stage stage;

    @FXML
    private Label errorLabel;
    @FXML
    private TextField login;
    @FXML
    private PasswordField password;

    private final String applicationTestUrl = "http://localhost:8090/test";
    private final String getUserUrl = "http://localhost:8090/user/getUser?";

    private final String getCitiesUrl = "http://localhost:8090/city/getCities";
    private final String getCityByNameUrl = "http://localhost:8090/city/getByName?";

    @FXML
    public void initialize() throws Exception {
        errorLabel.setVisible(false);

        try {
            ClientRequest clientRequest = new ClientRequest(applicationTestUrl);
            clientRequest.get();
        } catch (ConnectException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Dating Site");
            alert.setHeaderText(null);
            alert.setContentText("Brak połączenia z serwerem!");

            Optional<ButtonType> result = alert.showAndWait();
            if((result.get() == ButtonType.OK)){
                System.exit(0);
            }
        }
    }

    private User test() throws Exception {
        ClientRequest clientRequest = new ClientRequest(getCitiesUrl);
        List<City> cities = (List<City>)clientRequest.get().getEntity(new GenericType<List<City>>() {});
        City city = getCity("Kielce").get(0);

//        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
//
//        File female = new File(classloader.getResource("images/avatars/woman").toURI());
//        File male = new File(classloader.getResource("images/avatars/man").toURI());

//        List<File> females = new ArrayList<>(Arrays.asList(female.listFiles()));
//        List<File> males = new ArrayList<>(Arrays.asList(male.listFiles()));

        DatabaseGenerator generator = new DatabaseGenerator(cities);
        generator.generatePrepareData(city);
        return null;
// return generator.generate(4, true, city);
    }

    private List<City> getCity(String name) throws Exception {
        ClientRequest clientRequest = new ClientRequest(getCityByNameUrl + "name=" + name);
        return (List<City>)clientRequest.get().getEntity(new GenericType<List<City>>() {});
    }

    @FXML
    public void login() throws Exception {
        //test();

        DefaultHttpClient client = new DefaultHttpClient();
        Credentials credentials = new UsernamePasswordCredentials(login.getText(), password.getText());
        client.getCredentialsProvider().setCredentials(AuthScope.ANY, credentials);
        ApacheHttpClient4Executor executor = new ApacheHttpClient4Executor(client);

        ClientRequest clientRequest = new ClientRequest(getUserUrl + "login=" + login.getText(), executor);
        ClientResponse response = clientRequest.post();


        if(response.getStatus() == 401)
            errorLabel.setVisible(true);
        else if(response.getStatus() == 404)
            errorLabel.setVisible(true);
        else {
            User user = (User) response.getEntity(User.class);

            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("MainPanel.fxml"));
            AnchorPane pane = null;
            try {
                pane = loader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }

            MainPanelController mainPanelController = loader.getController();
            mainPanelController.setUser(user);
            mainPanelController.setMainPanel(pane);
            mainPanelController.setLoginPanel(loginPane);
            mainPanelController.setEmptyPanelController(emptyPanelController);
            mainPanelController.setLoginPanelController(this);
            mainPanelController.setPassword(password.getText());
            mainPanelController.setStage(stage);
            mainPanelController.refresh();

            emptyPanelController.setScreen(pane);
        }
    }

    @FXML
    public void register() {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("RegistrationPanel.fxml"));
        AnchorPane pane = null;
        try {
            pane = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        RegistrationPanelController registrationPanelController= loader.getController();
        registrationPanelController.setLoginPane(loginPane);
        registrationPanelController.setEmptyPanelController(emptyPanelController);
        registrationPanelController.setLoginPanelController(this);
        emptyPanelController.setScreen(pane);
    }

    @FXML
    public void typeData() {
        errorLabel.setVisible(false);
    }

    public void clearTextFields() {
        login.clear();
        password.clear();
    }

    public void setEmptyPanelController(EmptyPanelController emptyPanelController) {
        this.emptyPanelController = emptyPanelController;
    }

    public void setLoginPane(AnchorPane loginPane) {
        this.loginPane = loginPane;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
}
