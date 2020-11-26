package controller;

import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import model.Controller;
import model.Controller2;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML
    private Label resultLabel;

    @FXML
    private ProgressBar progresBar;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        resultLabel.setText(" ");
        progresBar.setProgress(0);
    }

    public void mergeExcel(ActionEvent actionEvent) {

        try {
            ArrayList<FileInputStream> list = new ArrayList<>();
            FileInputStream inputStream1 = new FileInputStream("2021.xlsx");
            FileInputStream inputStream2 = new FileInputStream("Horary Model.xlsx");

            list.add(inputStream1);
            list.add(inputStream2);

            Task<Void> longTask = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    Controller.mergeExcelFiles(new File("Test.xlsx"), list);

                    return null;
                }
            };

            longTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                @Override
                public void handle(WorkerStateEvent event) {
                    progresBar.setProgress(100);
                    resultLabel.setText("DONE!!!");
                }
            });

            longTask.setOnRunning(new EventHandler<WorkerStateEvent>() {
                @Override
                public void handle(WorkerStateEvent event) {
                    resultLabel.setText("Working on it");
                    progresBar.setProgress(longTask.getProgress()); //el progressbar este esta al berro
                }
            });

            new Thread(longTask).start();


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //esto fue para hacer pruebas. El metodo que sirve es el de arriba
    public void merge2(ActionEvent actionEvent) throws IOException {
        ArrayList<FileInputStream> list = new ArrayList<>();

        FileInputStream inputStream1 = new FileInputStream("C:/Users/DaVid/Downloads/Telegram Desktop/Archivos/2021.xlsx");

        FileInputStream inputStream2 = new FileInputStream("C:/Users/DaVid/Downloads/Telegram Desktop/Archivos/Horary Model.xlsx");

        list.add(inputStream1);
        list.add(inputStream2);

        Controller2.mergeExcelFiles(list);
    }
}
