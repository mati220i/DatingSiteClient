package pl.datingSite.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import org.apache.log4j.Logger;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.util.GenericType;
import pl.datingSite.tools.EmailValidator;
import pl.datingSite.model.ActivationCode;
import pl.datingSite.model.City;
import pl.datingSite.model.User;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.ws.rs.core.MediaType;
import java.net.ConnectException;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.*;

public class RegistrationPanelController {
    @FXML
    private Label nameError, sexError, emailError, ageError, surnameError, passwordError, loginError, activationCodeError, cityError;
    @FXML
    private TextField surname, name, activationCode, email, login;
    @FXML
    private ComboBox sex, city;
    @FXML
    private Button sendCode, register;
    @FXML
    private PasswordField password;
    @FXML
    private DatePicker age;

    private AnchorPane loginPane, registrationPanel;
    private EmptyPanelController emptyPanelController;
    private LoginPanelController loginPanelController;

    private EmailValidator emailValidator = new EmailValidator();

    private List<City> foundedCties;
    private int choosedCity;


    static final Logger logger = Logger.getLogger(RegistrationPanelController.class);

    private final String applicationTestUrl = "http://localhost:8090/test";
    private final String checkLoginUrl = "http://localhost:8090/user/registration/checkLogin/";
    private final String checkEmailUrl = "http://localhost:8090/user/registration/checkEmail/";
    private final String addCodeUrl = "http://localhost:8090/activationCode/addCode";
    private final String checkCodeUrl = "http://localhost:8090/activationCode/checkCode";
    private final String deleteCodeUrl = "http://localhost:8090/activationCode/deleteCode";
    private final String addUserUrl = "http://localhost:8090/user/registration/addUser/";
    private final String getCityByNameUrl = "http://localhost:8090/city/getByName?";

    @FXML
    public void initialize() throws Exception {
        try {
            ClientRequest clientRequest = new ClientRequest(applicationTestUrl);
            clientRequest.get();
        } catch (ConnectException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            ((Stage)alert.getDialogPane().getScene().getWindow()).getIcons().add(new Image("images/logoMini.png"));
            alert.setContentText("Brak połączenia z serwerem!");
            alert.setTitle("Dating Site");
            logger.error("Brak połączenia z serwerem");

            Optional<ButtonType> result = alert.showAndWait();
            if((result.get() == ButtonType.OK)){
                System.exit(0);
            }
        }

        nameError.setVisible(false);
        sexError.setVisible(false);
        emailError.setVisible(false);
        ageError.setVisible(false);
        surnameError.setVisible(false);
        passwordError.setVisible(false);
        loginError.setVisible(false);
        activationCodeError.setVisible(false);
        cityError.setVisible(false);
        register.setDisable(true);

        ObservableList<String> options =
                FXCollections.observableArrayList(
                        "Kobieta",
                        "Mężczyzna"
                );
        sex.setItems(options);
    }

    public void refresh() {
        Random generator = new Random();
        int val = generator.nextInt(16) + 1;
        String path = "images/background/background" + val + ".jpg";
        registrationPanel.setStyle("-fx-background-image: url('" + path + "'); -fx-background-size: 1200 720; -fx-background-size: cover");
    }

    @FXML
    public void register() throws Exception {
        if (checkActivationCode(activationCode.getText())) {
            choosedCity = city.getSelectionModel().getSelectedIndex();
            registerAccount();
            deleteActivationCode(activationCode.getText());

            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Rejestracja");
            ((Stage)alert.getDialogPane().getScene().getWindow()).getIcons().add(new Image("images/logoMini.png"));
            alert.setHeaderText(null);
            alert.setContentText("Konto zostało założone, możesz teraz się na nie zalogować");

            Optional<ButtonType> result = alert.showAndWait();
            if((result.get() == ButtonType.OK)){
                cancel();
            }
        } else {
            activationCodeError.setTextFill(Color.RED);
            activationCodeError.setText("Błędny kod");
            activationCodeError.setVisible(true);
        }
    }

    @FXML
    public void sendCode() throws Exception {
        try {
            if (checkData()) {
                sendActivationCode();
                activationCodeError.setTextFill(Color.GREEN);
                activationCodeError.setText("Wysłano kod na podany Email");
                activationCodeError.setVisible(true);
                register.setDisable(false);
            }
        } catch (ConnectException e) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Dating Site");
            ((Stage)alert.getDialogPane().getScene().getWindow()).getIcons().add(new Image("images/logoMini.png"));
            alert.setHeaderText(null);
            alert.setContentText("Brak połączenia z serwerem!");

            Optional<ButtonType> result = alert.showAndWait();
            if((result.get() == ButtonType.OK)){
                cancel();
            }
        }
    }

    @FXML
    public void cancel() {
        loginPanelController.clearTextFields();
        loginPanelController.refresh();
        emptyPanelController.setScreen(loginPane);
    }

    @FXML
    public void typeLogin() {
        loginError.setVisible(false);
    }

    @FXML
    public void typePassword() {
        passwordError.setVisible(false);
    }

    @FXML
    public void typeName() {
        nameError.setVisible(false);
    }

    @FXML
    public void typeSurname() {
        surnameError.setVisible(false);
    }

    @FXML
    public void typeEmail() {
        emailError.setVisible(false);
    }

    @FXML
    public void typeCity() throws Exception {
        cityError.setVisible(false);
        city.show();

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
    public void typeAge() {
        ageError.setVisible(false);
    }

    @FXML
    public void typeSex() {
        sexError.setVisible(false);
    }

    @FXML
    public void typeActivationCode() {
        activationCodeError.setVisible(false);
    }

    @FXML
    public void clickCity() {
        cityError.setVisible(false);
    }

    private boolean checkData() throws Exception {
        if (checkLogin(login.getText()) && checkPassword(password.getText()) && checkName(name.getText()) && checkSurname(surname.getText())
                && checkEmail(email.getText()) && checkCity(city.getSelectionModel().getSelectedIndex()) && checkAge(age.getValue()) && checkSex())
            return true;
        else
            return false;
    }

    private boolean checkLogin(String login) throws Exception {
        if (this.login.getText().equals("")) {
            loginError.setText("Podaj Login");
            loginError.setVisible(true);
            return false;
        }
        ClientRequest clientRequest = new ClientRequest(checkLoginUrl + login);
        if ((boolean) clientRequest.get().getEntity(boolean.class)) {
            loginError.setText("Login zajęty");
            loginError.setVisible(true);
            return false;
        } else
            return true;
    }

    private boolean checkPassword(String password) {
        if (password.equals("")) {
            passwordError.setVisible(true);
            return false;
        } else
            return true;
    }

    private boolean checkName(String name) {
        if (name.equals("")) {
            nameError.setVisible(true);
            return false;
        } else
            return true;
    }

    private boolean checkSurname(String surname) {
        if (surname.equals("")) {
            surnameError.setVisible(true);
            return false;
        } else
            return true;
    }

    private boolean checkEmail(String email) throws Exception {
        if (email.equals("")) {
            emailError.setText("Podaj email");
            emailError.setVisible(true);
            return false;
        }
        ClientRequest clientRequest = new ClientRequest(checkEmailUrl + email);
        boolean ifExist = (boolean) clientRequest.get().getEntity(boolean.class);
        if (ifExist) {
            emailError.setText("Email zajęty");
            emailError.setVisible(true);
            return false;
        } else if (emailValidator.isValid(email))
            return true;
        else {
            emailError.setText("Nieprawidłowy format");
            emailError.setVisible(true);
            return false;
        }
    }

    private boolean checkCity(int cityIndex) {
        if (cityIndex < 0) {
            cityError.setVisible(true);
            return false;
        } else
            return true;
    }

    private boolean checkAge(LocalDate dateOfBirth) {
        try {
            if (dateOfBirth == null || dateOfBirth.equals("")) {
                ageError.setText("Podaj wiek");
                ageError.setVisible(true);
                return false;
            }

            LocalDate now = LocalDate.now();
            Period period = Period.between(dateOfBirth, now);
            //Integer ageInt = Integer.valueOf(dateOfBirth);

            if (period.getYears() <= 18 || period.getYears() > 90) {
                ageError.setText("Zakres od 18 do 90");
                ageError.setVisible(true);
                return false;
            } else
                return true;
        } catch (NumberFormatException e) {
            ageError.setText("Podaj liczbę");
            ageError.setVisible(true);
            return false;
        }
    }

    private boolean checkSex() {
        if (sex.getSelectionModel().isEmpty()) {
            sexError.setVisible(true);
            return false;
        } else
            return true;
    }

    private void sendActivationCode() throws Exception {
        ActivationCode activationCode = new ActivationCode(email.getText());
        saveActivationCode(activationCode);
        sendEmailWithCode(activationCode);
    }

    private void saveActivationCode(ActivationCode activationCode) throws Exception {
        ClientRequest clientRequest = new ClientRequest(addCodeUrl);
        clientRequest.body(MediaType.APPLICATION_JSON_TYPE, activationCode);
        clientRequest.put();
    }

    private void sendEmailWithCode(ActivationCode activationCode) {

        final String username = "datingsite@wp.pl";
        final String password = "xxxxxxxxxxxxxxxx";

        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.wp.pl");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class",
                "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");


        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        try {

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("datingsite@wp.pl"));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(email.getText()));
            message.setSubject("Kod aktywacyjny do serwisu randkowego");
            message.setText("Witaj, na portalu randkowym. Jeśli to ty aktywowałeś konto, skopiuj poniższy kod i wklej go do aplikacji." +
                    " Życzymy miłego korzystania!" +
                    "\n\nKod Aktywacyjny:" +
                    "\n\n" + activationCode.getActivationCode());

            Transport.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean checkActivationCode(String activationCode) throws Exception {
        ActivationCode code = new ActivationCode(email.getText());
        code.setActivationCode(activationCode);

        ClientRequest clientRequest = new ClientRequest(checkCodeUrl);
        clientRequest.body(MediaType.APPLICATION_JSON_TYPE, code);
        return (boolean) clientRequest.post().getEntity(boolean.class);
    }

    private void registerAccount() throws Exception {
        Date date = Date.from(age.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());
        City city = foundedCties.get(this.choosedCity);
        User user = new User(login.getText(), password.getText(), email.getText(),
                name.getText(), surname.getText(), city,
                sex.getSelectionModel().getSelectedItem().toString(),
                date);
        user.setFake(false);

        ClientRequest clientRequest = new ClientRequest(addUserUrl);
        clientRequest.body(MediaType.APPLICATION_JSON_TYPE, user);
        clientRequest.put();
    }

    private void deleteActivationCode(String activationCode) throws Exception {
        ActivationCode code = new ActivationCode(email.getText());
        code.setActivationCode(activationCode);

        ClientRequest clientRequest = new ClientRequest(deleteCodeUrl);
        clientRequest.body(MediaType.APPLICATION_JSON_TYPE, code);
        clientRequest.delete();
    }

    public void setLoginPane(AnchorPane loginPane) {
        this.loginPane = loginPane;
    }

    public void setEmptyPanelController(EmptyPanelController emptyPanelController) {
        this.emptyPanelController = emptyPanelController;
    }

    public void setLoginPanelController(LoginPanelController loginPanelController) {
        this.loginPanelController = loginPanelController;
    }

    public void setRegistrationPanel(AnchorPane registrationPanel) {
        this.registrationPanel = registrationPanel;
    }
}
