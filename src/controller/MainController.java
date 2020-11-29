package controller;

import com.jfoenix.controls.JFXProgressBar;
import javafx.animation.*;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import model.Controller;

import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.sql.Time;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    int count =0;
    @FXML
    private ImageView slideShow;

    private ArrayList<Image> images;

    @FXML
    private Label resultLabel;

    @FXML
    private JFXProgressBar progressBar;

    @FXML
    private AnchorPane root;

    ImageView[] slides;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        slides = new ImageView[2];
        Image image1 = new Image("/resources/palobiofarma.jpg");
        Image image2 = new Image("/resources/medibiofarma.png");
        //Image image3 = new Image(SlideShowTest.class.getResource("pic3").toExternalForm());
        //Image image4 = new Image(SlideShowTest.class.getResource("pic4").toExternalForm());
        slides[0] = new ImageView(image1);
        slides[0].setFitHeight(200);
        slides[0].setFitWidth(794);
        slides[1] = new ImageView(image2);
        slides[1].setFitHeight(200);
        slides[1].setFitWidth(794);
        createSlideShow();
        //createSlideShow();
        resultLabel.setText(" ");
        progressBar.setProgress(0);
    }

    private void createSlideShow(){

        /*images = new ArrayList<>();
        images.add(new Image("/resources/medibiofarma.png"));
        images.add(new Image("/resources/palobiofarma.jpg"));


        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(3),event -> {
            SequentialTransition sequentialTransition = new SequentialTransition();

            //slideShow.setImage(null);
            slideShow.setImage(images.get(count));
            FadeTransition ft = getFadeTransition(slideShow,0.0,1.0,2000);
            PauseTransition pauseTransition = new PauseTransition(Duration.millis(2000));
            FadeTransition fadeOut = getFadeTransition(slideShow, 1.0, 0.0, 2000);
            sequentialTransition.getChildren().addAll(ft, pauseTransition, fadeOut);
            count++;
            if(count == 2){
                count = 0;
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();*/
            SequentialTransition slideshow = new SequentialTransition();
            for (ImageView slide : slides) {

                SequentialTransition sequentialTransition = new SequentialTransition();

                FadeTransition fadeIn = getFadeTransition(slide, 0.0, 1.0, 2000);
                PauseTransition stayOn = new PauseTransition(Duration.millis(2000));
                FadeTransition fadeOut = getFadeTransition(slide, 1.0, 0.0, 2000);

                sequentialTransition.getChildren().addAll(fadeIn, stayOn, fadeOut);
                slide.setOpacity(0);
                this.root.getChildren().add(slide);
                slideshow.getChildren().add(sequentialTransition);

            }
            //root.getChildren().clear();
            //slideshow.setAutoReverse(true);
            slideshow.play();


    }


    public FadeTransition getFadeTransition(ImageView imageView, double fromValue, double toValue, int durationInMilliseconds) {

        FadeTransition ft = new FadeTransition(Duration.millis(durationInMilliseconds), imageView);
        ft.setFromValue(fromValue);
        ft.setToValue(toValue);

        return ft;

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
                    //progressBar.setProgress(this.getProgress());
                    return null;
                }
            };

            longTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                @Override
                public void handle(WorkerStateEvent event) {
                    progressBar.setProgress(100);
                    resultLabel.setText("DONE!!!");
                }
            });

            longTask.setOnRunning(new EventHandler<WorkerStateEvent>() {
                @Override
                public void handle(WorkerStateEvent event) {
                    resultLabel.setText("Working on it");
                    progressBar.setProgress(longTask.getProgress()); //el progressbar este esta al berro
                }
            });

            new Thread(longTask).start();


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public AnchorPane getRoot() {
        return root;
    }
}
