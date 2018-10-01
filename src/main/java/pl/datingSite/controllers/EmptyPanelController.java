package pl.datingSite.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.jboss.resteasy.client.ClientRequest;

import javax.ws.rs.core.MediaType;
import java.io.IOException;


public class EmptyPanelController {

    private Stage stage;

    @FXML
    private AnchorPane emptyPanel;

    @FXML
    public void initialize(){
        loadLoginScreen();
    }

    public void loadLoginScreen() {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("LoginPanel.fxml"));
        AnchorPane pane = null;
        try {
            pane = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        LoginPanelController panel = loader.getController();
        panel.setEmptyPanelController(this);
        panel.setLoginPane(pane);
        panel.setStage(stage);
        panel.refresh();
        setScreen(pane);
    }

    public void setScreen(AnchorPane pane) {
        emptyPanel.getChildren().clear();
        emptyPanel.getChildren().add(pane);
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    //    @FXML
//    private Label test;
//
//    private String applicationUrl = "http://localhost:8090/test";
//
//
//    @SuppressWarnings("deprecation")
//    public void load() throws Exception {
//        ClientRequest clientRequest = new ClientRequest(applicationUrl);
//        clientRequest.accept(MediaType.APPLICATION_JSON);
//        test.setText((String)clientRequest.get().getEntity(String.class));
//    }
}
