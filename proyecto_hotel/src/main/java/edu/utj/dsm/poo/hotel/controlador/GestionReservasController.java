package edu.utj.dsm.poo.hotel.controlador;

import edu.utj.dsm.poo.hotel.validaciones.ValidadorForm;
import edu.utj.dsm.poo.hotel.validaciones.Validaciones;
import javafx.scene.control.Label;
import edu.utj.dsm.poo.hotel.modelo.Cliente;
import edu.utj.dsm.poo.hotel.modelo.Habitacion;
import edu.utj.dsm.poo.hotel.modelo.Reservacion;
import edu.utj.dsm.poo.hotel.persistencia.ActividadDAO;
import edu.utj.dsm.poo.hotel.persistencia.HabitacionDAO;
import edu.utj.dsm.poo.hotel.persistencia.ReservacionDAO;
import java.util.List;
import java.net.URL;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;

public class GestionReservasController implements Initializable {

    @FXML
    private ImageView imgLogo;
    @FXML
    private Button btnGuardarReserva;
    @FXML
    private Button btnCancelar;
    @FXML
    private TextField txtNombreCompleto;
    @FXML
    private TextField txtTelefono;
    @FXML
    private ComboBox<String> cbHabitacion;
    @FXML
    private DatePicker dpFechaInicio;
    @FXML
    private DatePicker dpFechaFin;

    private final HabitacionDAO habitacionDAO = new HabitacionDAO();
    private final ReservacionDAO reservacionDAO = new ReservacionDAO();
    // Habitaciones en el mismo orden que aparecen en el ComboBox
    private List<Habitacion> habitacionesDisponibles;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarValidaciones();
        cargarHabitaciones();
    }

    /** Llena el ComboBox con las habitaciones 'Disponible' de la base de datos. */
    private void cargarHabitaciones() {
        habitacionesDisponibles = habitacionDAO.listarDisponibles();
        ObservableList<String> opciones = FXCollections.observableArrayList();
        for (Habitacion h : habitacionesDisponibles) {
            opciones.add(h.getNumeroHabitacion() + " - " + h.getTipo()
                    + " ($" + String.format("%.2f", h.getPrecioPorNoche()) + "/noche)");
        }
        cbHabitacion.setItems(opciones);
    }

    @FXML
    private void guardarReserva(ActionEvent event) {
        String nombre = txtNombreCompleto.getText();
        String telefono = txtTelefono.getText();
        String habitacionSeleccionada = cbHabitacion.getValue();
        LocalDate fechaEntrada = dpFechaInicio.getValue();
        LocalDate fechaSalida = dpFechaFin.getValue();

        
        if (nombre.trim().isEmpty() || telefono.trim().isEmpty() || habitacionSeleccionada == null || fechaEntrada == null || fechaSalida == null) {
            mostrarAlerta("Campos Incompletos", "Por favor llena todos los campos para continuar.", Alert.AlertType.WARNING);
            return;
        }

    
        if (fechaSalida.isBefore(fechaEntrada) || fechaSalida.isEqual(fechaEntrada)) {
            mostrarAlerta("Fechas Inválidas", "La fecha de salida debe ser posterior a la fecha de entrada.", Alert.AlertType.ERROR);
            return;
        }

        
        int indice = cbHabitacion.getSelectionModel().getSelectedIndex();
        if (indice < 0 || indice >= habitacionesDisponibles.size()) {
            mostrarAlerta("Habitación", "Selecciona una habitación de la lista.", Alert.AlertType.WARNING);
            return;
        }
        Habitacion habitacion = habitacionesDisponibles.get(indice);

        if (reservacionDAO.hayEmpalme(habitacion.getNumeroHabitacion(), fechaEntrada, fechaSalida)) {
            mostrarAlerta("Habitación ocupada",
                    "La habitación " + habitacion.getNumeroHabitacion() + " ya tiene una reservación en esas fechas.",
                    Alert.AlertType.WARNING);
            return;
        }

        Cliente cliente = new Cliente("", nombre.trim(), "", telefono.trim());

        long noches = ChronoUnit.DAYS.between(fechaEntrada, fechaSalida);
        double costoTotal = noches * habitacion.getPrecioPorNoche();

        Reservacion nuevaReserva = new Reservacion();
        nuevaReserva.setFolio(reservacionDAO.siguienteFolio());
        nuevaReserva.setCliente(cliente);
        nuevaReserva.setHabitacion(habitacion);
        nuevaReserva.setFechaEntrada(fechaEntrada);
        nuevaReserva.setFechaSalida(fechaSalida);
        nuevaReserva.setCostoTotal(costoTotal);
        nuevaReserva.setEstado("CONFIRMADA");

        if (!reservacionDAO.guardar(nuevaReserva)) {
            mostrarAlerta("Error de BD", "No se pudo guardar la reservación en MySQL.", Alert.AlertType.ERROR);
            return;
        }
        ActividadDAO.registrar("Creó la reservación " + nuevaReserva.getFolio()
                + " (hab. " + habitacion.getNumeroHabitacion() + ")");

        mostrarAlerta("Reserva Guardada", 
            "Folio: " + nuevaReserva.getFolio() + 
            "\nCliente: " + cliente.getNombre() + 
            "\nHabitación: " + habitacion.getNumeroHabitacion() +
            "\nTotal (" + noches + " noches): $" + String.format("%.2f", costoTotal), 
            Alert.AlertType.INFORMATION);

        limpiarCampos(event);
    }

    @FXML
    private void cancelar(ActionEvent event) {
        limpiarCampos(event);
    }

    private void limpiarCampos(ActionEvent event) {
        txtNombreCompleto.clear();
        txtTelefono.clear();
        cbHabitacion.getSelectionModel().clearSelection();
        dpFechaInicio.setValue(null);
        dpFechaFin.setValue(null);
        validador.limpiarMensajes();
        actualizarBotones();
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    // ---------- Validaciones en tiempo real ----------

    @FXML
    private Label errNombreCompleto, errTelefono, errHabitacion, errFechaInicio, errFechaFin;
    @FXML
    private Button btnNuevo, btnValidar;

    private ValidadorForm validador;

    private void configurarValidaciones() {
        validador = new ValidadorForm(this::actualizarBotones);

        // Filtros: bloquean caracteres no permitidos al teclear y al pegar
        Validaciones.soloLetras(txtNombreCompleto, errNombreCompleto);
        Validaciones.soloDigitos(txtTelefono, errTelefono, 10);

        // Reglas: se evalúan cada vez que el campo cambia (escribir, borrar o pegar)
        validador.agregar(errNombreCompleto, () -> Validaciones.letras(txtNombreCompleto.getText()),
                txtNombreCompleto.textProperty());
        validador.agregar(errTelefono, () -> Validaciones.digitos(txtTelefono.getText(), 10),
                txtTelefono.textProperty());
        validador.agregar(errHabitacion, () -> Validaciones.seleccion(cbHabitacion.getValue()),
                cbHabitacion.valueProperty());
        validador.agregar(errFechaInicio, () -> dpFechaInicio.getValue() == null ? "Selecciona una fecha" : (dpFechaInicio.getValue().isBefore(LocalDate.now()) ? "La entrada no puede ser anterior a hoy" : null),
                dpFechaInicio.valueProperty());
        validador.agregar(errFechaFin, () -> dpFechaFin.getValue() == null ? "Selecciona una fecha" : (dpFechaInicio.getValue() != null && !dpFechaFin.getValue().isAfter(dpFechaInicio.getValue()) ? "Debe ser posterior a la fecha de entrada" : null),
                dpFechaFin.valueProperty(), dpFechaInicio.valueProperty());

        actualizarBotones();
    }

    // Guardar solo se habilita si todo es válido; Editar y Eliminar, si hay un registro seleccionado
    private void actualizarBotones() {
        btnGuardarReserva.setDisable(!validador.esValido());
    }

    @FXML
    private void validarFormulario(ActionEvent event) {
        if (validador.validarTodo()) {
            Alert alerta = new Alert(Alert.AlertType.INFORMATION);
            alerta.setTitle("Validación");
            alerta.setHeaderText(null);
            alerta.setContentText("Todos los datos cumplen las validaciones.");
            alerta.showAndWait();
        }
        actualizarBotones();
    }

    @FXML
    private void nuevo(ActionEvent event) {
        limpiarCampos(event);
    }
}