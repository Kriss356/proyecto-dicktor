package edu.utj.dsm.poo.hotel.persistencia;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Valida usuario y contraseña contra las tablas administradores y empleados.
 */
public class LoginDAO {

    /**
     * Si las credenciales son correctas inicia la Sesion y regresa true.
     */
    public boolean autenticar(String usuario, String contrasena) throws SQLException {
        String sqlAdmin = "SELECT nombre, rol FROM administradores "
                + "WHERE usuario=? AND clave_acceso=? AND estado='ACTIVO'";
        String sqlEmpleado = "SELECT nombre, primer_apellido, rol FROM empleados "
                + "WHERE usuario=? AND contrasena=?";

        try (Connection conn = ConexionBD.obtenerConexion()) {
            try (PreparedStatement ps = conn.prepareStatement(sqlAdmin)) {
                ps.setString(1, usuario);
                ps.setString(2, contrasena);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        Sesion.iniciar(usuario, rs.getString("nombre"), rs.getString("rol"));
                        return true;
                    }
                }
            }
            try (PreparedStatement ps = conn.prepareStatement(sqlEmpleado)) {
                ps.setString(1, usuario);
                ps.setString(2, contrasena);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        String nombre = rs.getString("nombre") + " " + rs.getString("primer_apellido");
                        Sesion.iniciar(usuario, nombre, rs.getString("rol"));
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
