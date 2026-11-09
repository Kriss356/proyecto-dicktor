package edu.utj.dsm.poo.hotel.persistencia;

import edu.utj.dsm.poo.hotel.modelo.Cliente;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {

    public boolean guardar(Cliente c) {
        String sql = "INSERT INTO clientes (rfc, nombre, email, telefono, ruta_imagen) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getRfc());
            ps.setString(2, c.getNombre());
            ps.setString(3, c.getEmail());
            ps.setString(4, c.getTelefono());
            ps.setString(5, c.getRutaImagen());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /** rfcOriginal = RFC que tenía antes de editar (por si lo cambiaron). */
    public boolean actualizar(Cliente c, String rfcOriginal) {
        String sql = "UPDATE clientes SET rfc=?, nombre=?, email=?, telefono=?, ruta_imagen=? WHERE rfc=?";
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getRfc());
            ps.setString(2, c.getNombre());
            ps.setString(3, c.getEmail());
            ps.setString(4, c.getTelefono());
            ps.setString(5, c.getRutaImagen());
            ps.setString(6, rfcOriginal);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean eliminar(String rfc) {
        String sql = "DELETE FROM clientes WHERE rfc=?";
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, rfc);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Cliente> listarTodos() {
        List<Cliente> lista = new ArrayList<>();
        String sql = "SELECT rfc, nombre, email, telefono, ruta_imagen FROM clientes ORDER BY nombre";
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Cliente c = new Cliente(rs.getString("rfc"), rs.getString("nombre"),
                        rs.getString("email"), rs.getString("telefono"));
                c.setRutaImagen(rs.getString("ruta_imagen"));
                lista.add(c);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
}
