package model;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class EmpleadoTableModel {

    private SimpleIntegerProperty cod_empleado;
    private SimpleStringProperty nombre_empleado;
    private SimpleStringProperty primer_apellido;
    private SimpleStringProperty segundo_apellido;
    private SimpleStringProperty nif;
    private SimpleStringProperty numero_afiliacion;
    private SimpleStringProperty cod_empresa;


    public EmpleadoTableModel(int cod_empleado, String nombre_empleado,
                              String primer_apellido, String segundo_apellido,
                              String nif, String numero_afiliacion, String cod_empresa) {
        this.cod_empleado =  new SimpleIntegerProperty(cod_empleado);
        this.nombre_empleado = new SimpleStringProperty(nombre_empleado);
        this.primer_apellido = new SimpleStringProperty(primer_apellido);
        this.segundo_apellido = new SimpleStringProperty(segundo_apellido);
        this.nif = new SimpleStringProperty(nif);
        this.numero_afiliacion = new SimpleStringProperty(numero_afiliacion);
        this.cod_empresa = new SimpleStringProperty(cod_empresa);
    }

    public int getCod_empleado() {
        return cod_empleado.get();
    }

    public SimpleIntegerProperty cod_empleadoProperty() {
        return cod_empleado;
    }

    public void setCod_empleado(int cod_empleado) {
        this.cod_empleado.set(cod_empleado);
    }

    public String getNombre_empleado() {
        return nombre_empleado.get();
    }

    public SimpleStringProperty nombre_empleadoProperty() {
        return nombre_empleado;
    }

    public void setNombre_empleado(String nombre_empleado) {
        this.nombre_empleado.set(nombre_empleado);
    }

    public String getPrimer_apellido() {
        return primer_apellido.get();
    }

    public SimpleStringProperty primer_apellidoProperty() {
        return primer_apellido;
    }

    public void setPrimer_apellido(String primer_apellido) {
        this.primer_apellido.set(primer_apellido);
    }

    public String getSegundo_apellido() {
        return segundo_apellido.get();
    }

    public SimpleStringProperty segundo_apellidoProperty() {
        return segundo_apellido;
    }

    public void setSegundo_apellido(String segundo_apellido) {
        this.segundo_apellido.set(segundo_apellido);
    }

    public String getNif() {
        return nif.get();
    }

    public SimpleStringProperty nifProperty() {
        return nif;
    }

    public void setNif(String nif) {
        this.nif.set(nif);
    }

    public String getNumero_afiliacion() {
        return numero_afiliacion.get();
    }

    public SimpleStringProperty numero_afiliacionProperty() {
        return numero_afiliacion;
    }

    public void setNumero_afiliacion(String numero_afiliacion) {
        this.numero_afiliacion.set(numero_afiliacion);
    }

    public String getCod_empresa() {
        return cod_empresa.get();
    }

    public SimpleStringProperty cod_empresaProperty() {
        return cod_empresa;
    }

    public void setCod_empresa(String cod_empresa) {
        this.cod_empresa.set(cod_empresa);
    }
}
