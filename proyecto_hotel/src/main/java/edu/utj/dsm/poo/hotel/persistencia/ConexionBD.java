package edu.utj.dsm.poo.hotel.persistencia;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Conexión a MySQL de XAMPP.
 * XAMPP por defecto: host localhost, puerto 3306, usuario root y SIN contraseña.
 * Si en tu phpMyAdmin le pusiste contraseña a root, escríbela en PASSWORD.
 */
public class ConexionBD {

    private static final String HOST = "localhost";
    private static final String PUERTO = "3306";
    private static final String BASE_DATOS = "hotel_db";

    private static final String URL = "jdbc:mysql://" + HOST + ":" + PUERTO + "/" + BASE_DATOS
            + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=America/Mexico_City"
            + "&useUnicode=true&characterEncoding=UTF-8";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    static {
        try {
            // Carga explícita del driver (necesario en algunos entornos modulares)
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("No se encontró el driver de MySQL (mysql-connector-j). "
                    + "Haz 'Clean and Build' en NetBeans para que Maven lo descargue.");
        }
    }

    public static Connection obtenerConexion() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    /**
     * Prueba la conexión. Regresa null si todo está bien, o un mensaje
     * entendible con lo que falló.
     */
    public static String probarConexion() {
        try (Connection conn = obtenerConexion()) {
            return null;
        } catch (SQLException e) {
            String msg = e.getMessage() == null ? "" : e.getMessage();
            if (msg.contains("Unknown database")) {
                return "La base de datos '" + BASE_DATOS + "' no existe.\n"
                        + "Impórtala en phpMyAdmin con el archivo database/hotel_db.sql";
            }
            if (msg.contains("Access denied")) {
                return "Usuario o contraseña de MySQL incorrectos.\n"
                        + "Revisa USER y PASSWORD en ConexionBD.java";
            }
            if (msg.contains("Communications link failure") || msg.contains("Connection refused")) {
                return "No se pudo conectar a MySQL.\n"
                        + "Abre el panel de XAMPP y presiona 'Start' en MySQL.";
            }
            if (msg.contains("No suitable driver")) {
                return "Falta el driver de MySQL. Haz 'Clean and Build' en NetBeans.";
            }
            return "Error de conexión: " + msg;
        }
    }
}
