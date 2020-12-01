package services;

import model.Empleado;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class EmpleadoServices {

    public ArrayList<Empleado> listadoEmpleados(){
        ArrayList<Empleado> lista= new ArrayList<>();
        try{
            Connection conexion = ServicesLocator.getConnection();
            String consulta = "Select empleado.* from empleado";
            PreparedStatement prepare = conexion.prepareStatement(consulta);//para consultas
            prepare.execute();
            ResultSet result = prepare.getResultSet();//para quedarme con lo q devuelve la consulta
            while (result.next()){ //para varias filas
                Empleado a = new Empleado(result.getInt(1), result.getString(2),
                        result.getString(3),result.getString(4),result.getString(5),
                        result.getString(6),result.getInt(7));
                lista.add(a);
            }
            conexion.close();
        }
        catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return lista;
    }

    public ArrayList<Empleado> listadoEmpleadosXEmpresa(String nombreEmpresa) {
        ArrayList<Empleado> list = new ArrayList<>();

        try {
            Connection connection = ServicesLocator.getConnection();
            String consulta = "Select empleado.* from empleado join empresa on empleado.cod_empresa = empresa.cod_empresa where empresa.nombre_empresa = " + "'" + nombreEmpresa + "'";
            PreparedStatement prepare = connection.prepareStatement(consulta);//para consultas
            prepare.execute();
            ResultSet result = prepare.getResultSet();//para quedarme con lo q devuelve la consulta
            while (result.next()){ //para varias filas
                Empleado a = new Empleado(result.getInt(1), result.getString(2),
                        result.getString(3),result.getString(4),result.getString(5),
                        result.getString(6),result.getInt(7));
                list.add(a);
            }

            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }
}
