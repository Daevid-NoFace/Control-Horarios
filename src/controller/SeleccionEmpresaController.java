package controller;

import com.jfoenix.controls.JFXComboBox;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;
import model.Controller;
import model.Empleado;
import model.Empresa;
import services.ServicesLocator;
import tray.animations.AnimationType;
import tray.notification.NotificationType;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class SeleccionEmpresaController implements Initializable {

    @FXML
    private JFXComboBox<String> empresaComboBox;

    private ArrayList<FileInputStream> listFiles;
    private ArrayList<Empresa> listaEmpresas;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        listFiles = new ArrayList<>();
        listaEmpresas = ServicesLocator.getEmpresa().listadoEmpresas();
        ArrayList<String> nombresEmpresas = new ArrayList<>();
        for (Empresa empresa: listaEmpresas) {
            nombresEmpresas.add(empresa.getNombre());
        }
        empresaComboBox.setItems(FXCollections.observableArrayList(nombresEmpresas));
    }

    public void mergeExcel(javafx.event.ActionEvent actionEvent) throws IOException {
        ArrayList<Empleado> lista = ServicesLocator.getEmpleado().listadoEmpleadosXEmpresa(listaEmpresas.get(empresaComboBox.getSelectionModel().getSelectedIndex()).getNombre());
        System.out.println(lista.size());

        FileInputStream inputStream1 = new FileInputStream("2021.xlsx");
        FileInputStream inputStream2 = new FileInputStream("Horary Model.xlsx");

        listFiles.add(inputStream1);
        listFiles.add(inputStream2);
        //list.add(inputStream2);
        if(listFiles.size()<2){
            System.out.println("ERROR");
        }

        Controller.mergeExcelFiles(lista, listaEmpresas.get(empresaComboBox.getSelectionModel().getSelectedIndex()), listFiles);

        /*for (int i = 0; i < lista.size(); i++) {
            System.out.println("i: " + i);
            Controller.mergeExcelFiles(new File(empresaComboBox.getSelectionModel().getSelectedItem().getNombre() + lista.get(i).getNombre()  + ".xlsx"), listFiles);
        }*/
    }
}
