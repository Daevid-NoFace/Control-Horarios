package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXPopup;
import com.jfoenix.controls.JFXProgressBar;
import javafx.animation.*;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import main.Main;
import model.Controller;

import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import model.Empleado;
import model.Empresa;
import services.ServicesLocator;
import tray.animations.AnimationType;
import tray.notification.NotificationType;
import tray.notification.TrayNotification;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class MainMenuController implements Initializable {

    private TrayNotification notification;

    @FXML
    private JFXButton fileMenu;

    @FXML
    private JFXButton editMenu;

    @FXML
    private Label resultLabel;

    @FXML
    private JFXProgressBar progressBar;

    @FXML
    private AnchorPane root;

    public static Empresa empresa;
    private File calendarFile;

    private ArrayList<FileInputStream> listFiles;

    ImageView[] slides;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        fileMenu.requestFocus();
        editMenu.requestFocus();

        //Archivo
        AnchorPane popupPane = new AnchorPane();
        VBox vBox = new VBox();

        //Edicion
        AnchorPane popupEditionPane = new AnchorPane();
        VBox vBoxEdition = new VBox();

        JFXListView<JFXButton> list = new JFXListView<JFXButton>();

        JFXButton btnLoadCalendar = new JFXButton("Cargar Calendario");
        ImageView view = new ImageView(new Image("/resources/calendario.png"));
        view.setFitWidth(25);
        view.setFitHeight(25);
        btnLoadCalendar.setGraphic(view);
        btnLoadCalendar.setCursor(Cursor.HAND);
        JFXButton btnHorary = new JFXButton("Generar Horarios");
        ImageView view1 = new ImageView(new Image(getClass().getResourceAsStream("/resources/excel.png")));
        view1.setFitWidth(25);
        view1.setFitHeight(25);
        btnHorary.setGraphic(view1);
        btnHorary.setCursor(Cursor.HAND);
        JFXButton close = new JFXButton("Cerrar");
        ImageView view2 = new ImageView(new Image(getClass().getResourceAsStream("/resources/logout.png")));
        view2.setFitWidth(25);
        view2.setFitHeight(25);
        close.setGraphic(view2);
        close.setCursor(Cursor.HAND);

        //poner los botones en el popup
        vBox.getChildren().add(btnLoadCalendar);
        vBox.getChildren().add(btnHorary);
        vBox.getChildren().add(close);
        popupPane.getChildren().add(vBox);
        JFXPopup popup = new JFXPopup(popupPane);

        //acciones de los botones
        fileMenu.setOnAction(event -> {
            popup.show(fileMenu, JFXPopup.PopupVPosition.TOP, JFXPopup.PopupHPosition.LEFT,fileMenu.getLayoutX(),fileMenu.getLayoutY()+50);
        });
        btnLoadCalendar.setOnAction(event -> {
            loadSchedule(event);
            popup.hide();
        });
        btnHorary.setOnAction(event -> {
            //mergeExcel(event);
            mostrarPanelSeleccionarEmpresa(event);
            popup.hide();

        });
        close.setOnAction(event -> {
            closeApplication(event);
            popup.hide();
        });

        //creacion de los menus de edicion
        JFXButton btnEditWorker = new JFXButton("Empleados");
        ImageView view3 = new ImageView(new Image("/resources/address_book_32.png"));
        view.setFitWidth(25);
        view.setFitHeight(25);
        btnEditWorker.setGraphic(view3);
        btnEditWorker.setCursor(Cursor.HAND);

        //poner los botones en el popup
        vBoxEdition.getChildren().addAll(btnEditWorker);
        popupEditionPane.getChildren().add(vBoxEdition);
        JFXPopup popupEdition = new JFXPopup(popupEditionPane);

        //acciones de los botones
        editMenu.setOnAction(event -> {
            popupEdition.show(editMenu, JFXPopup.PopupVPosition.TOP, JFXPopup.PopupHPosition.LEFT, editMenu.getLayoutX() - 100, editMenu.getLayoutY() + 50);
        });
        btnEditWorker.setOnAction(event -> {
            mostrarPanelEmpleados(event);
            popupEdition.hide();
        });



        notification = new TrayNotification();
        listFiles = new ArrayList<>();
        getSLides();
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

    @FXML
    void loadSchedule(ActionEvent event) {
        Stage stage = new Stage();
        FileChooser fc = new FileChooser();

        fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Documento Excel", "*xlsx"));
        File file = fc.showOpenDialog(stage);

        try {
            if (file != null) {
               listFiles.add(new FileInputStream(file));
               calendarFile = file;
               notification.setMessage("Calendario importado con éxito");
               notification.setTitle("Importación de calendario");
               notification.setNotificationType(NotificationType.SUCCESS);
            }
            else{
                notification.setMessage("Falló la importación del calendario");
                notification.setTitle("Importación de calendario");
                notification.setNotificationType(NotificationType.ERROR);
            }
            notification.showAndDismiss(Duration.millis(5000));
            notification.setAnimationType(AnimationType.POPUP);
        } catch (IOException e) {
            notification.setMessage("Falló la importación de Calendario");
            notification.setTitle("Importacion de calendario");
            notification.setNotificationType(NotificationType.ERROR);
            notification.showAndDismiss(Duration.millis(5000));
            notification.setAnimationType(AnimationType.POPUP);
            e.printStackTrace();
        }
    }

    public void mergeExcel(ActionEvent actionEvent) {

        try {

            //FileInputStream inputStream1 = new FileInputStream("2021.xlsx");
            FileInputStream inputStream2 = new FileInputStream("Horary Model.xlsx");

            //if(listFiles.get)
            listFiles.add(inputStream2);
            //list.add(inputStream2);
            if(listFiles.size()<2){
                notification.setMessage("Debe importar el calendario a analizar");
                notification.setTitle("Importacion de calendario");
                notification.setNotificationType(NotificationType.ERROR);
                notification.showAndDismiss(Duration.millis(5000));
                notification.setAnimationType(AnimationType.POPUP);
            }
            else{
                Task<Void> longTask = new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        ArrayList<Empleado> lista = ServicesLocator.getEmpleado().listadoEmpleadosXEmpresa(empresa.getNombre());
                        Controller.mergeExcelFiles(lista, empresa, calendarFile);
                        //progressBar.setProgress(this.getProgress());
                        return null;
                    }
                };

                longTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                    @Override
                    public void handle(WorkerStateEvent event) {
                        progressBar.setProgress(100);
                        resultLabel.setText("DONE!!!");
                        notification.setMessage("Modelos de horarios creados");
                        notification.setTitle("Control de horario");
                        notification.setNotificationType(NotificationType.SUCCESS);
                        notification.showAndDismiss(Duration.millis(5000));
                        notification.setAnimationType(AnimationType.POPUP);
                        File file = new File("Test.xlsx");

                        //first check if Desktop is supported by Platform or not
                        try{
                            if(!Desktop.isDesktopSupported()){
                                System.out.println("Desktop is not supported");
                                return;
                            }

                            Desktop desktop = Desktop.getDesktop();

                            //let's try to open PDF file
                            if(file.exists()) desktop.open(file);

                        }catch (Exception i){
                            i.printStackTrace();
                        }
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
            }

            //listFiles.remove(listFiles.size()-1);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @FXML
    void showCalendarMenu(ActionEvent event) throws IOException {

    }

    private void mostrarPanelSeleccionarEmpresa(ActionEvent event) {
        try {
            System.out.println("Panel de seleccionar empresas" + "\n" + "-------------------------");

            FXMLLoader loader =new FXMLLoader();
            loader.setLocation(MainMenuController.class.getResource("../view/SeleccionEmpresa.fxml"));
            AnchorPane page = (AnchorPane) loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Seleccionar Empresa");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.setAlwaysOnTop(true);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            //setController
            SeleccionEmpresaController controller =loader.getController();
            controller.setMainMenuController(this);

            dialogStage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void closeApplication(ActionEvent event) {
        System.exit(0);
    }

    private ImageView [] getSLides(){
        slides = new ImageView[100];
        Image image1 = new Image("/resources/palobiofarma.png");
        Image image2 = new Image("/resources/medibiofarma.png");
        for(int i=0; i <100;i++){
            if(i%2==0){
                slides[i] = new ImageView(image1);
            }
            else{
                slides[i] = new ImageView(image2);
            }

            slides[i].setFitHeight(200);
            slides[i].setFitWidth(794);
        }
        //Image image3 = new Image(SlideShowTest.class.getResource("pic3").toExternalForm());
        //Image image4 = new Image(SlideShowTest.class.getResource("pic4").toExternalForm());


        return slides;
    }

    public AnchorPane getRoot() {
        return root;
    }

    public ArrayList<FileInputStream> getFiles(){
        return this.listFiles;
    }

    public void mostrarPanelEmpleados(ActionEvent event) {
        try {
            System.out.println("Panel de edicion de empleados" + "\n" + "-------------------------");

            FXMLLoader loader =new FXMLLoader();
            loader.setLocation(MainMenuController.class.getResource("../view/EdicionEmpleado.fxml"));
            AnchorPane page = (AnchorPane) loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Gestionar Empleados");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.setAlwaysOnTop(true);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            //setController
            EdicionEmpleadoController controller =loader.getController();
            controller.setMainMenuController(this);

            dialogStage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //CREATION OF FRAMES
    /*public void createPage(Object instance, AnchorPane home, String loc) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(MainMenuController.class.getResource(loc));
        home = loader.load();

        if (instance instanceof CreateStructureController) {
            instance = loader.getController();
            ((CreateStructureController) instance).setMainController(this);
        } if (instance instanceof StructureManagementController) {
            instance = loader.getController();
            ((StructureManagementController) instance).setMainController(this);
        } if (instance instanceof CalculationsController) {
            instance = loader.getController();
            ((CalculationsController) instance).setMainController(this);
        }

        setNode(home);
    }

    public void setNode(Node node) {
        innerPane.getChildren().clear();
        innerPane.getChildren().add(node);
        FadeTransition ft = new FadeTransition(Duration.millis(2000));
        ft.setNode(node);
        ft.setFromValue(0.1);
        ft.setToValue(1);
        ft.setCycleCount(1);
        ft.setAutoReverse(false);
        ft.play();
    }*/
}
