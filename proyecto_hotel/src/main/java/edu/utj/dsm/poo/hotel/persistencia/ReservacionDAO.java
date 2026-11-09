package edu.utj.dsm.poo.hotel.persistencia;

import edu.utj.dsm.poo.hotel.modelo.Reservacion;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class ReservacionDAO {

    public boolean guardar(Reservacion r) {
        String sql = "INSERT INTO reservaciones (folio, nombre_cliente, telefono_cliente, numero_habitacion, "
                + "fecha_entrada, fecha_salida, costo_total, estado) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, r.getFolio());
            ps.setString(2, r.getCliente().getNombre());
            ps.setString(3, r.getCliente().getTelefono());
            ps.setString(4, r.getHabitacion().getNumeroHabitacion());
            ps.setDate(5, Date.valueOf(r.getFechaEntrada()));
            ps.setDate(6, Date.valueOf(r.getFechaSalida()));
            ps.setDouble(7, r.getCostoTotal());
            ps.setString(8, r.getEstado());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /** true si la habitación ya está reservada en alguna parte de esas fechas. */
    public boolean hayEmpalme(String numeroHabitacion, LocalDate entrada, LocalDate salida) {
        String sql = "SELECT COUNT(*) FROM reservaciones WHERE numero_habitacion=? AND estado<>'CANCELADA' "
                + "AND fecha_entrada < ? AND fecha_salida > ?";
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, numeroHabitacion);
            ps.setDate(2, Date.valueOf(salida));
            ps.setDate(3, Date.valueOf(entrada));
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /** Folio consecutivo: RES-0001, RES-0002, ... */
    public String siguienteFolio() {
        String sql = "SELECT COUNT(*) FROM reservaciones";
        int numero = 1;
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                numero = rs.getInt(1) + 1;
            }
            // por si se borró alguna, avanzar hasta encontrar uno libre
            try (PreparedStatement existe = conn.prepareStatement("SELECT 1 FROM reservaciones WHERE folio=?")) {
                while (true) {
                    existe.setString(1, String.format("RES-%04d", numero));
                    try (ResultSet r2 = existe.executeQuery()) {
                        if (!r2.next()) {
                            break;
                        }
                    }
                    numero++;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "RES-" + (System.currentTimeMillis() % 100000);
        }
        return String.format("RES-%04d", numero);
    }

    /** Reservaciones cuya fecha de entrada es hoy. */
    public int contarEntradasHoy() {
        String sql = "SELECT COUNT(*) FROM reservaciones WHERE fecha_entrada = CURDATE() AND estado<>'CANCELADA'";
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }
}
