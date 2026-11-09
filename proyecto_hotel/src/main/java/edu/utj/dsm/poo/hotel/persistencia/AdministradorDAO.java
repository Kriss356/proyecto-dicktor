package edu.utj.dsm.poo.hotel.persistencia;

import edu.utj.dsm.poo.hotel.modelo.Administrador;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AdministradorDAO {

    public boolean guardar(Administrador admin) {
        String sql = "INSERT INTO administradores (nombre, telefono, email, direccion, usuario, clave_acceso, ruta_imagen, rol, estado) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, admin.getNombre());
            ps.setString(2, admin.getTelefono());
            ps.setString(3, admin.getEmail());
            ps.setString(4, admin.getDireccion());
            ps.setString(5, admin.getUsuario());
            ps.setString(6, admin.getClaveAcceso());
            ps.setString(7, admin.getRutaImagen());
            ps.setString(8, admin.getRol());
            ps.setString(9, admin.getEstado());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean actualizar(Administrador admin) {
        String sql = "UPDATE administradores SET nombre=?, telefono=?, email=?, direccion=?, usuario=?, clave_acceso=?, ruta_imagen=?, rol=?, estado=? WHERE id=?";
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, admin.getNombre());
            ps.setString(2, admin.getTelefono());
            ps.setString(3, admin.getEmail());
            ps.setString(4, admin.getDireccion());
            ps.setString(5, admin.getUsuario());
            ps.setString(6, admin.getClaveAcceso());
            ps.setString(7, admin.getRutaImagen());
            ps.setString(8, admin.getRol());
            ps.setString(9, admin.getEstado());
            ps.setInt(10, admin.getId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean eliminar(int id) {
        String sql = "DELETE FROM administradores WHERE id=?";
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Administrador> listarTodos() {
        List<Administrador> lista = new ArrayList<>();
        String sql = "SELECT id, nombre, telefono, email, direccion, usuario, clave_acceso, ruta_imagen, rol, estado FROM administradores";
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Administrador admin = new Administrador(
                    rs.getInt("id"),
                    rs.getString("nombre"),
                    rs.getString("telefono"),
                    rs.getString("email"),
                    rs.getString("direccion"),
                    rs.getString("usuario"),
                    rs.getString("clave_acceso"),
                    rs.getString("ruta_imagen"),
                    rs.getString("rol"),
                    rs.getString("estado")
                );
                lista.add(admin);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
}