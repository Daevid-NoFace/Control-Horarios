package main;

import eu.mihosoft.scaledfx.ScalableContentPane;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        ScalableContentPane scale = new ScalableContentPane();
        Parent root = FXMLLoader.load(getClass().getResource("../view/MainMenu.fxml"));
        scale.setContent(root);
        primaryStage.setTitle("Hello World");
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setScene(new Scene(scale));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
