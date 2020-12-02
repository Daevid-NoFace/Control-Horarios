package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import model.Empleado;
import services.ServicesLocator;

import java.net.URL;
import java.util.ResourceBundle;

public class EmployesManagementController implements Initializable {

    private MainMenuController mainMenuController;


    @FXML
    private JFXTextField nombreTextField;

    @FXML
    private JFXTextField primApellidoTextField;

    @FXML
    private JFXTextField segApellidoTextfield;

    @FXML
    private JFXTextField nifTextfield;

    @FXML
    private JFXTextField numTextfield;


    @FXML
    private TableView<Empleado> employesTable;

    @FXML
    private TableColumn<Empleado, Integer> codCol;

    @FXML
    private TableColumn<Empleado, String> nombreCol;

    @FXML
    private TableColumn<Empleado, String> primerApCol;

    @FXML
    private TableColumn<Empleado, String> segundoApCol;

    @FXML
    private TableColumn<Empleado, String> nifCol;

    @FXML
    private TableColumn<Empleado, String> numCol;

    @FXML
    private TableColumn<Empleado, String> empresaCol;

    @FXML
    private TableColumn<Empleado, Integer> horasColum;

    @FXML
    private JFXTextField horasLaborables;



    private ObservableList<Empleado> employes;
    private ObservableList<String> empresas;

    @FXML
    private JFXComboBox<String> comboEmpresa;

    @FXML
    private JFXButton btnInsert;

    @FXML
    private JFXButton btnUpdate;

    @FXML
    private JFXButton btnDelete;



    @Override
    public void initialize(URL location, ResourceBundle resources) {

        RequiredFieldValidator validator = new RequiredFieldValidator();
        validator.setMessage("Debe rellenar el campo");

        nombreTextField.getValidators().add(validator);
        nombreTextField.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if(!newValue)
                    nombreTextField.validate();
            }
        });
        primApellidoTextField.getValidators().add(validator);
        primApellidoTextField.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if(!newValue)
                    primApellidoTextField.validate();
            }
        });
        nifTextfield.getValidators().add(validator);
        nifTextfield.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if(!newValue)
                    nifTextfield.validate();
            }
        });
        numTextfield.getValidators().add(validator);
        numTextfield.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if(!newValue)
                    numTextfield.validate();
            }
        });
        horasLaborables.getValidators().add(validator);
        horasLaborables.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if(!newValue)
                    horasLaborables.validate();
            }
        });
        horasLaborables.setText("8");

        horasLaborables.setTextFormatter(new TextFormatter<>(change ->
                (change.getControlNewText().matches("^[4-8]*$")) ? change : null));

        codCol.setCellValueFactory(
                new PropertyValueFactory<Empleado,Integer>("cod_empleado")
        );

        nombreCol.setCellValueFactory(
                new PropertyValueFactory<Empleado, String>("nombre")
        );

        primerApCol.setCellValueFactory(
                new PropertyValueFactory<Empleado, String>("primer_apellido")
        );
        segundoApCol.setCellValueFactory(
                new PropertyValueFactory<Empleado, String>("segundo_apellido")
        );
        nifCol.setCellValueFactory(
                new PropertyValueFactory<Empleado, String>("nif")
        );
        numCol.setCellValueFactory(
                new PropertyValueFactory<Empleado, String>("numero_afiliacion")
        );
        empresaCol.setCellValueFactory(
                new PropertyValueFactory<Empleado, String>("nombre_empresa")
        );

        horasColum.setCellValueFactory(
                new PropertyValueFactory<Empleado,Integer>("horas_laborables")
        );


        btnInsert.setDisable(false);
        btnDelete.setDisable(true);
        btnUpdate.setDisable(true);


        empresas = FXCollections.observableArrayList(ServicesLocator.getEmpresa().nombreEmpresas());
        comboEmpresa.setItems(empresas);

        populateTable();

        employesTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> showEmployeeDetails(newValue));

        btnInsert.setOnAction(event -> insertEmployee());
        btnUpdate.setOnAction(event -> updateEmployee());
        btnDelete.setOnAction(event -> deleteEmployee());

    }


    private void populateTable(){
        employes = FXCollections.observableArrayList(ServicesLocator.getEmpleado().listadoEmpleadosModelo());
        employesTable.setItems(employes);

    }

    public void setMainMenuController(MainMenuController mainMenuController) {
        this.mainMenuController = mainMenuController;
    }

    public void showEmployeeDetails(Empleado empleado){
        btnInsert.setDisable(true);
        btnDelete.setDisable(false);
        btnUpdate.setDisable(false);
        if(empleado !=null){
            nombreTextField.setText(empleado.getNombre());
            primApellidoTextField.setText(empleado.getPrimer_apellido());
            segApellidoTextfield.setText(empleado.getSegundo_apellido());
            nifTextfield.setText(empleado.getNif());
            numTextfield.setText(empleado.getNumero_afiliacion());
            //ObservableList<String> empresa = FXCollections.observableArrayList(empleado.getCod_empresa());
            comboEmpresa.getSelectionModel().select(empleado.getNombre_empresa());
           horasLaborables.setText(String.valueOf(empleado.getHoras_laborables()));
        }

        else{
            btnInsert.setDisable(false);
            btnDelete.setDisable(true);
            btnUpdate.setDisable(true);
        }
    }


    void deleteEmployee() {
        Empleado empleado = employesTable.getSelectionModel().getSelectedItem();
        ServicesLocator.getEmpleado().deleteEmpleado(empleado);
        resetValues();

        populateTable();
    }


    void insertEmployee() {

            Empleado empleado = new Empleado();
            empleado.setNombre(nombreTextField.getText());
            empleado.setPrimer_apellido(primApellidoTextField.getText());
            empleado.setSegundo_apellido(segApellidoTextfield.getText());
            empleado.setNif(nifTextfield.getText());
            empleado.setNumero_afiliacion(numTextfield.getText());
            int cod_empresa = ServicesLocator.getEmpresa().getEmpresaCodByName(comboEmpresa.getSelectionModel().getSelectedItem());
            empleado.setCod_empresa(cod_empresa);
            empleado.setHoras_laborables(Integer.parseInt(horasLaborables.getText()));
            ServicesLocator.getEmpleado().insertarEmpleado(empleado);
            resetValues();
            populateTable();

    }


    void updateEmployee() {
        Empleado empleado = employesTable.getSelectionModel().getSelectedItem();
        empleado.setNombre(nombreTextField.getText());
        empleado.setPrimer_apellido(primApellidoTextField.getText());
        empleado.setSegundo_apellido(segApellidoTextfield.getText());
        empleado.setNif(nifTextfield.getText());
        empleado.setNumero_afiliacion(numTextfield.getText());
        int cod_empresa = ServicesLocator.getEmpresa().getEmpresaCodByName(comboEmpresa.getSelectionModel().getSelectedItem());
        empleado.setCod_empresa(cod_empresa);
        empleado.setHoras_laborables(Integer.parseInt(horasLaborables.getText()));
        ServicesLocator.getEmpleado().updateEmpleado(empleado);
        resetValues();
        populateTable();
    }


    private void resetValues(){
        nombreTextField.setText("");
        primApellidoTextField.setText("");
        segApellidoTextfield.setText("");
        nifTextfield.setText("");
        numTextfield.setText("");
        comboEmpresa.getSelectionModel().select(-1);
        horasLaborables.setText("8");
    }

    @FXML
    void resetAllValues(KeyEvent event) {
        if(event.getCode().getName().equalsIgnoreCase("Esc")){
            btnInsert.setDisable(false);
            btnDelete.setDisable(true);
            btnUpdate.setDisable(true);
            nombreTextField.setText("");
            primApellidoTextField.setText("");
            segApellidoTextfield.setText("");
            nifTextfield.setText("");
            numTextfield.setText("");
            comboEmpresa.getSelectionModel().select(-1);
            horasLaborables.setText("8");
        }

    }
}
