package model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import services.ServicesLocator;

public class Empleado {
    private int cod_empleado;
    private StringProperty nombre;
    private StringProperty primer_apellido;
    private StringProperty segundo_apellido;
    private String nif;
    private String numero_afiliacion;
    private int cod_empresa;

    public Empleado(String nombre, String primer_apellido, String segundo_apellido, String nif, String numero_afiliacion, int cod_empresa) {
        this.nombre = new SimpleStringProperty(nombre);
        this.primer_apellido = new SimpleStringProperty(primer_apellido);
        this.segundo_apellido = new SimpleStringProperty(segundo_apellido);
        this.nif = nif;
        this.numero_afiliacion = numero_afiliacion;
        this.cod_empresa = cod_empresa;
    }

    public Empleado(int cod_empleado, String nombre, String primer_apellido, String segundo_apellido, String nif, String numero_afiliacion, int cod_empresa) {
        this.cod_empleado = cod_empleado;
        this.nombre = new SimpleStringProperty(nombre);
        this.primer_apellido = new SimpleStringProperty(primer_apellido);
        this.segundo_apellido = new SimpleStringProperty(segundo_apellido);
        this.nif = nif;
        this.numero_afiliacion = numero_afiliacion;
        this.cod_empresa = cod_empresa;
    }

    public int getCod_empleado() {
        return cod_empleado;
    }

    public void setCod_empleado(int cod_empleado) {
        this.cod_empleado = cod_empleado;
    }

    public String getNombre() {
        return nombre.get();
    }

    public StringProperty getNombreProperty() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre.set(nombre);
    }

    public String getPrimer_apellido() {
        return primer_apellido.get();
    }

    public StringProperty getPrimer_apellidoProperty() {
        return primer_apellido;
    }

    public void setPrimer_apellido(String primer_apellido) {
        this.primer_apellido.set(primer_apellido);
    }

    public String getSegundo_apellido() {
        return segundo_apellido.get();
    }

    public void setSegundo_apellido(String segundo_apellido) {
        this.segundo_apellido.set(segundo_apellido);
    }

    public StringProperty getSegundo_apellidoProperty() {
        return segundo_apellido;
    }

    public String getNif() {
        return nif;
    }

    public void setNif(String nif) {
        this.nif = nif;
    }

    public String getNumero_afiliacion() {
        return numero_afiliacion;
    }

    public void setNumero_afiliacion(String numero_afiliacion) {
        this.numero_afiliacion = numero_afiliacion;
    }

    public int getCod_empresa() {
        return cod_empresa;
    }

    public void setCod_empresa(int cod_empresa) {
        this.cod_empresa = cod_empresa;
    }


    public String toStringCode() {
        return "Empleado{" +
                "cod_empleado=" + cod_empleado +
                ", nombre='" + nombre + '\'' +
                ", primer_apellido='" + primer_apellido + '\'' +
                ", segundo_apellido='" + segundo_apellido + '\'' +
                ", nif='" + nif + '\'' +
                ", numero_afiliacion='" + numero_afiliacion + '\'' +
                ", cod_empresa=" + cod_empresa +
                '}';
    }

    @Override
    public String toString() {
        return "Empleado{" +
                "nombre='" + nombre + '\'' +
                ", primer_apellido='" + primer_apellido + '\'' +
                ", segundo_apellido='" + segundo_apellido + '\'' +
                ", nif='" + nif + '\'' +
                ", numero_afiliacion='" + numero_afiliacion + '\'' +
                ", cod_empresa=" + cod_empresa +
                '}';
    }
}
