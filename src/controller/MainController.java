package controller;

import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import model.Controller;

import java.io.File;
import java.io.FileInputStream;
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
}
