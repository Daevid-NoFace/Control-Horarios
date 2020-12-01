package controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import model.Empleado;
import services.ServicesLocator;


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
}
