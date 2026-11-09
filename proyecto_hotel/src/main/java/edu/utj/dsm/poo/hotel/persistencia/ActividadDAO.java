package edu.utj.dsm.poo.hotel.persistencia;

import edu.utj.dsm.poo.hotel.modelo.Actividad;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Bitácora de lo que hacen los usuarios (se muestra en el Panel de inicio).
 */
public class ActividadDAO {

    /** Registra una acción del usuario que tiene la sesión abierta. */
    public static void registrar(String accion) {
        registrar(Sesion.getUsuario(), accion);
    }

    public static void registrar(String usuario, String accion) {
        String sql = "INSERT INTO actividad (usuario, accion) VALUES (?, ?)";
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, usuario);
            ps.setString(2, accion);
            ps.executeUpdate();
        } catch (SQLException e) {
            // La bitácora nunca debe detener al sistema
            System.err.println("No se pudo registrar la actividad: " + e.getMessage());
        }
    }

    public List<Actividad> listarRecientes(int limite) {
        List<Actividad> lista = new ArrayList<>();
        String sql = "SELECT DATE_FORMAT(fecha_hora, '%Y-%m-%d %H:%i:%s') AS fh, usuario, accion "
                + "FROM actividad ORDER BY fecha_hora DESC, id DESC LIMIT ?";
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limite);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new Actividad(rs.getString("fh"), rs.getString("usuario"), rs.getString("accion")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
}
