package controller;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Empleado;
import services.ServicesLocator;


import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class EdicionEmpleadoController implements Initializable {

    private MainMenuController mainMenuController;

    @FXML
    private TableView<Empleado> tablaEmpleados;

    @FXML
    private TableColumn<Empleado, String> columnaNombre;

    @FXML
    private TableColumn<Empleado, String> columnaApellido;

    @FXML
    private Label nombreLabel;

    @FXML
    private Label apellidoLabel;

    @FXML
    private Label NIFLabel;

    @FXML
    private Label NoAfiliacionLabel;

    @FXML
    private Label empresaLabel;

    public void setMainMenuController(MainMenuController mainMenuController) {
        this.mainMenuController = mainMenuController;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ArrayList<Empleado> listaEmpleados = ServicesLocator.getEmpleado().listadoEmpleados();
        tablaEmpleados.setItems(FXCollections.observableArrayList(listaEmpleados));

        columnaNombre.setCellValueFactory(cellData -> cellData.getValue().getNombreProperty());
        columnaApellido.setCellValueFactory(cellData -> cellData.getValue().getPrimer_apellidoProperty().concat(" ").concat(cellData.getValue().getSegundo_apellidoProperty()));

        showPersonDetails(null);

        tablaEmpleados.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> showPersonDetails(newValue));
    }

    private void showPersonDetails(Empleado empleado) {
        if (empleado != null) {
            // Fill the labels with info from the person object.
            nombreLabel.setText(empleado.getNombre());
            apellidoLabel.setText(empleado.getPrimer_apellido().concat(" ").concat(empleado.getSegundo_apellido()));
            NIFLabel.setText(empleado.getNif());
            NoAfiliacionLabel.setText(empleado.getNumero_afiliacion());
            empresaLabel.setText(ServicesLocator.getEmpresa().empresaById(empleado.getCod_empresa()));
        } else {
            // Person is null, remove all the text.
            nombreLabel.setText("");
            apellidoLabel.setText("");
            NIFLabel.setText("");
            NoAfiliacionLabel.setText("");
            empresaLabel.setText("");
        }
    }

    public void insertarEmpleado(ActionEvent event) {
        try {
            Empleado empleado = new Empleado();

            // Load the fxml file and create a new stage for the popup dialog.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainMenuController.class.getResource("../view/VentanaEdicion.fxml"));
            AnchorPane page = (AnchorPane) loader.load();

            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Edit Person");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            //dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            // Set the person into the controller.
            VentanaEdicionController controller = loader.getController();
            controller.setEmpleado(empleado);

            // Show the dialog and wait until the user closes it
            dialogStage.setAlwaysOnTop(true);
            dialogStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
