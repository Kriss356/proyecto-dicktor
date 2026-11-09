package edu.utj.dsm.poo.hotel.persistencia;

import edu.utj.dsm.poo.hotel.modelo.Administrador;
import edu.utj.dsm.poo.hotel.modelo.Empleado;
import edu.utj.dsm.poo.hotel.modelo.Mantenimiento;
import edu.utj.dsm.poo.hotel.modelo.Recepcion;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmpleadoDAO {

    public boolean guardar(Empleado emp) {
        String sql = "INSERT INTO empleados (nombre, primer_apellido, segundo_apellido, nacimiento, email, telefono, rfc, usuario, contrasena, rol, ruta_imagen) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, emp.getNombre());
            ps.setString(2, emp.getPrimerApellido());
            ps.setString(3, emp.getSegundoApellido());
            ps.setDate(4, emp.getNacimiento() != null ? new java.sql.Date(emp.getNacimiento().getTime()) : null);
            ps.setString(5, emp.getEmail());
            ps.setString(6, emp.getTelefono());
            ps.setString(7, emp.getRfc());
            ps.setString(8, emp.getUsuario());
            ps.setString(9, emp.getContrasena());
            ps.setString(10, emp.getRol());
            ps.setString(11, emp.getRutaImagen());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean actualizar(Empleado emp) {
        String sql = "UPDATE empleados SET nombre=?, primer_apellido=?, segundo_apellido=?, nacimiento=?, email=?, telefono=?, usuario=?, contrasena=?, rol=?, ruta_imagen=? WHERE rfc=?";
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, emp.getNombre());
            ps.setString(2, emp.getPrimerApellido());
            ps.setString(3, emp.getSegundoApellido());
            ps.setDate(4, emp.getNacimiento() != null ? new java.sql.Date(emp.getNacimiento().getTime()) : null);
            ps.setString(5, emp.getEmail());
            ps.setString(6, emp.getTelefono());
            ps.setString(7, emp.getUsuario());
            ps.setString(8, emp.getContrasena());
            ps.setString(9, emp.getRol());
            ps.setString(10, emp.getRutaImagen());
            ps.setString(11, emp.getRfc());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean eliminar(String rfc) {
        String sql = "DELETE FROM empleados WHERE rfc=?";
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, rfc);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Empleado> listarTodos() {
        List<Empleado> lista = new ArrayList<>();
        String sql = "SELECT * FROM empleados";
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String rol = rs.getString("rol");
                Empleado emp;
                if ("Administrador".equalsIgnoreCase(rol)) {
                    Administrador admin = new Administrador();
                    admin.setRol("Administrador"); // para que coincida con el ComboBox
                    emp = admin;
                }
                else if ("Recepción".equalsIgnoreCase(rol)) emp = new Recepcion();
                else emp = new Mantenimiento();

                emp.setNombre(rs.getString("nombre"));
                emp.setPrimerApellido(rs.getString("primer_apellido"));
                emp.setSegundoApellido(rs.getString("segundo_apellido"));
                emp.setNacimiento(rs.getDate("nacimiento"));
                emp.setEmail(rs.getString("email"));
                emp.setTelefono(rs.getString("telefono"));
                emp.setRfc(rs.getString("rfc"));
                emp.setUsuario(rs.getString("usuario"));
                emp.setContrasena(rs.getString("contrasena"));
                emp.setRutaImagen(rs.getString("ruta_imagen"));

                lista.add(emp);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
}