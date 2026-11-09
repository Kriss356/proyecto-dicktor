package edu.utj.dsm.poo.hotel.controlador;

import edu.utj.dsm.poo.hotel.App;
import edu.utj.dsm.poo.hotel.persistencia.ActividadDAO;
import edu.utj.dsm.poo.hotel.persistencia.ConexionBD;
import edu.utj.dsm.poo.hotel.persistencia.LoginDAO;
import java.io.IOException;
import java.sql.SQLException;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    @FXML
    private TextField txtUsuario;

    @FXML
    private PasswordField txtPassword;

    private final LoginDAO loginDAO = new LoginDAO();

    @FXML
    private void handleEntrar(ActionEvent event) {
        String usuario = txtUsuario.getText().trim();
        String password = txtPassword.getText();

        if (usuario.isEmpty() || password.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campos vacíos", "Escribe tu usuario y contraseña.");
            return;
        }

        // Primero revisamos que MySQL (XAMPP) esté encendido y la base exista
        String errorConexion = ConexionBD.probarConexion();
        if (errorConexion != null) {
            mostrarAlerta(Alert.AlertType.ERROR, "Sin conexión a la base de datos", errorConexion);
            return;
        }

        try {
            if (loginDAO.autenticar(usuario, password)) {
                ActividadDAO.registrar("Inicio de sesión en el sistema");
                App.setRoot("PanelAdmin");
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "Acceso denegado", "Usuario o contraseña incorrectos.");
                txtPassword.clear();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Error de BD", "No se pudo validar el usuario: " + e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCerrar(ActionEvent event) {
        Platform.exit();
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
