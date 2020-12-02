package controller;

import com.jfoenix.controls.JFXComboBox;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.Empleado;
import model.Empresa;
import services.ServicesLocator;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class VentanaEdicionController implements Initializable {

    @FXML
    private TextField nombreTextField;

    @FXML
    private TextField apellidoTextField;

    @FXML
    private TextField NIFTextField;

    @FXML
    private TextField NoAfiliacionTextField;

    @FXML
    private JFXComboBox<String> empresaComboBox;

    Empleado empleado;


    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void setEmpleado(Empleado empleado) {
        this.empleado = empleado;

        nombreTextField.setText(empleado.getNombre());
        apellidoTextField.setText(empleado.getPrimer_apellido());
        NIFTextField.setText(empleado.getNif());
        NoAfiliacionTextField.setText(empleado.getNumero_afiliacion());

        ArrayList empresas = ServicesLocator.getEmpresa().listadoEmpresas();
        empresaComboBox.setItems(FXCollections.observableArrayList(empresas));
        empresaComboBox.getSelectionModel().select(empleado.getCod_empresa() - 1);
    }

    public void handleOk(ActionEvent event) {   //hay que validar campos vacios
        empleado.setNombre(nombreTextField.getText());
        empleado.setPrimer_apellido(apellidoTextField.getText());
        empleado.setSegundo_apellido(apellidoTextField.getText());
        empleado.setNif(NIFTextField.getText());
        empleado.setNumero_afiliacion(NoAfiliacionTextField.getText());
        empleado.setCod_empresa(empresaComboBox.getSelectionModel().getSelectedIndex() + 1);
    }
}
