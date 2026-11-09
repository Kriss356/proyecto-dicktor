package edu.utj.dsm.poo.hotel.persistencia;

import edu.utj.dsm.poo.hotel.modelo.Habitacion;
import edu.utj.dsm.poo.hotel.modelo.HabitacionSencilla;
import edu.utj.dsm.poo.hotel.modelo.Suite;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class HabitacionDAO {

    private static final String COLUMNAS = "numero, tipo, piso, precio_noche, capacidad, estado, numero_camas, "
            + "balcon, vista_al_mar, jacuzzi, mini_bar, incluye_desayuno, ruta_imagen";

    public boolean guardar(Habitacion h) {
        String sql = "INSERT INTO habitaciones (" + COLUMNAS + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            llenarParametros(ps, h);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /** numeroOriginal = número que tenía antes de editar (por si lo cambiaron). */
    public boolean actualizar(Habitacion h, String numeroOriginal) {
        String sql = "UPDATE habitaciones SET numero=?, tipo=?, piso=?, precio_noche=?, capacidad=?, estado=?, "
                + "numero_camas=?, balcon=?, vista_al_mar=?, jacuzzi=?, mini_bar=?, incluye_desayuno=?, ruta_imagen=? "
                + "WHERE numero=?";
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            llenarParametros(ps, h);
            ps.setString(14, numeroOriginal);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Elimina la habitación. Regresa false si no se pudo (por ejemplo,
     * si tiene reservaciones registradas).
     */
    public boolean eliminar(String numero) {
        String sql = "DELETE FROM habitaciones WHERE numero=?";
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, numero);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Habitacion> listarTodas() {
        return listar("SELECT " + COLUMNAS + " FROM habitaciones ORDER BY numero");
    }

    public List<Habitacion> listarDisponibles() {
        return listar("SELECT " + COLUMNAS + " FROM habitaciones WHERE estado='Disponible' ORDER BY numero");
    }

    /** Cuenta habitaciones; si estado es null cuenta todas. */
    public int contar(String estado) {
        String sql = estado == null
                ? "SELECT COUNT(*) FROM habitaciones"
                : "SELECT COUNT(*) FROM habitaciones WHERE estado=?";
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (estado != null) {
                ps.setString(1, estado);
            }
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    private List<Habitacion> listar(String sql) {
        List<Habitacion> lista = new ArrayList<>();
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(desdeResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    private Habitacion desdeResultSet(ResultSet rs) throws SQLException {
        String tipo = rs.getString("tipo");
        Habitacion h;
        if ("Suite".equalsIgnoreCase(tipo)) {
            Suite s = new Suite();
            s.setNumeroDeCamas(rs.getInt("numero_camas"));
            s.setJacuzzi(rs.getBoolean("jacuzzi"));
            s.setMiniBar(rs.getBoolean("mini_bar"));
            s.setIncluyeDesayuno(rs.getBoolean("incluye_desayuno"));
            h = s;
        } else {
            HabitacionSencilla hs = new HabitacionSencilla();
            hs.setNumeroDeCamas(rs.getInt("numero_camas"));
            hs.setBalcon(rs.getBoolean("balcon"));
            hs.setVistaAlMar(rs.getBoolean("vista_al_mar"));
            h = hs;
        }
        h.setNumeroHabitacion(rs.getString("numero"));
        h.setTipo(tipo);
        h.setPiso(rs.getInt("piso"));
        h.setPrecioPorNoche(rs.getDouble("precio_noche"));
        h.setCapacidad(rs.getInt("capacidad"));
        h.setEstado(rs.getString("estado"));
        h.setRutaImagen(rs.getString("ruta_imagen"));
        return h;
    }

    private void llenarParametros(PreparedStatement ps, Habitacion h) throws SQLException {
        int camas = 1;
        boolean balcon = false, vistaMar = false, jacuzzi = false, miniBar = false, desayuno = false;

        if (h instanceof HabitacionSencilla) {
            HabitacionSencilla hs = (HabitacionSencilla) h;
            camas = hs.getNumeroDeCamas();
            balcon = hs.isBalcon();
            vistaMar = hs.isVistaAlMar();
        } else if (h instanceof Suite) {
            Suite s = (Suite) h;
            camas = s.getNumeroDeCamas();
            jacuzzi = s.isJacuzzi();
            miniBar = s.isMiniBar();
            desayuno = s.isIncluyeDesayuno();
        }

        ps.setString(1, h.getNumeroHabitacion());
        ps.setString(2, h.getTipo());
        ps.setInt(3, h.getPiso());
        ps.setDouble(4, h.getPrecioPorNoche());
        ps.setInt(5, h.getCapacidad());
        ps.setString(6, h.getEstado());
        ps.setInt(7, camas);
        ps.setBoolean(8, balcon);
        ps.setBoolean(9, vistaMar);
        ps.setBoolean(10, jacuzzi);
        ps.setBoolean(11, miniBar);
        ps.setBoolean(12, desayuno);
        ps.setString(13, h.getRutaImagen());
    }
}
