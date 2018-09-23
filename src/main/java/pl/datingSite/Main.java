package pl.datingSite;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import pl.datingSite.controllers.EmptyPanelController;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
//        FXMLLoader loader = new FXMLLoader();
//        loader.setLocation(getClass().getResource("/EmptyPanel.fxml"));
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("EmptyPanel.fxml"));
        AnchorPane pane = loader.load();

        Scene scene = new Scene(pane);


        primaryStage.setTitle("Dating Site");
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);

        EmptyPanelController emptyPanelController = loader.getController();
        emptyPanelController.setStage(primaryStage);

        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
