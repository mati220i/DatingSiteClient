package pl.datingSite;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import pl.datingSite.controllers.EmptyPanelController;

import java.io.FileOutputStream;
import java.io.PrintStream;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        /**  Turn on the production version **/
//        PrintStream out = new PrintStream(new FileOutputStream("output.txt"));
//        PrintStream errOut = new PrintStream(new FileOutputStream("errorOutput.txt"));
//        System.setOut(out);
//        System.setErr(errOut);

        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("EmptyPanel.fxml"));
        AnchorPane pane = loader.load();

        Scene scene = new Scene(pane);

        Image icon = new Image("images/logoMini.png");

        primaryStage.setTitle("Dating Site");
        primaryStage.setResizable(false);
        primaryStage.setWidth(1206);
        primaryStage.setHeight(755);
        primaryStage.getIcons().setAll(icon);
        primaryStage.setScene(scene);

        EmptyPanelController emptyPanelController = loader.getController();
        emptyPanelController.setStage(primaryStage);

        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
