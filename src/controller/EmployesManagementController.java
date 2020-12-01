package controller;

import com.jfoenix.controls.JFXComboBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import model.EmpleadoTableModel;
import services.ServicesLocator;

import java.net.URL;
import java.util.ResourceBundle;

public class EmployesManagementController implements Initializable {

    @FXML
    private TableView<EmpleadoTableModel> employesTable;

    @FXML
    private TableColumn<EmpleadoTableModel, Integer> codCol;

    @FXML
    private TableColumn<EmpleadoTableModel, String> nombreCol;

    @FXML
    private TableColumn<EmpleadoTableModel, String> primerApCol;

    @FXML
    private TableColumn<EmpleadoTableModel, String> segundoApCol;

    @FXML
    private TableColumn<EmpleadoTableModel, String> nifCol;

    @FXML
    private TableColumn<EmpleadoTableModel, String> numCol;

    @FXML
    private TableColumn<EmpleadoTableModel, String> empresaCol;


    private ObservableList<EmpleadoTableModel> employes;
    private ObservableList<String> empresas;

    @FXML
    private JFXComboBox<String> comboEmpresa;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        codCol.setCellValueFactory(
                new PropertyValueFactory<EmpleadoTableModel,Integer>("cod_empleado")
        );

        nombreCol.setCellValueFactory(
                new PropertyValueFactory<EmpleadoTableModel, String>("nombre_empleado")
        );

        primerApCol.setCellValueFactory(
                new PropertyValueFactory<EmpleadoTableModel, String>("primer_apellido")
        );
        segundoApCol.setCellValueFactory(
                new PropertyValueFactory<EmpleadoTableModel, String>("segundo_apellido")
        );
        nifCol.setCellValueFactory(
                new PropertyValueFactory<EmpleadoTableModel, String>("nif")
        );
        numCol.setCellValueFactory(
                new PropertyValueFactory<EmpleadoTableModel, String>("numero_afiliacion")
        );
        empresaCol.setCellValueFactory(
                new PropertyValueFactory<EmpleadoTableModel, String>("cod_empresa")
        );


        empresas = FXCollections.observableArrayList(ServicesLocator.getEmpresa().nombreEmpresas());
        comboEmpresa.setItems(empresas);

        populateTable();

    }


    private void populateTable(){
        employes = FXCollections.observableArrayList(ServicesLocator.getEmpleado().listadoEmpleadosModelo());
        employesTable.setItems(employes);

    }
}
