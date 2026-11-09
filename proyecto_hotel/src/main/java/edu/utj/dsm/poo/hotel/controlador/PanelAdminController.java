package edu.utj.dsm.poo.hotel.controlador;

import edu.utj.dsm.poo.hotel.App;
import edu.utj.dsm.poo.hotel.modelo.Actividad;
import edu.utj.dsm.poo.hotel.persistencia.ActividadDAO;
import edu.utj.dsm.poo.hotel.persistencia.HabitacionDAO;
import edu.utj.dsm.poo.hotel.persistencia.ReservacionDAO;
import edu.utj.dsm.poo.hotel.persistencia.Sesion;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class PanelAdminController implements Initializable {

    @FXML private BorderPane contenedor;
    @FXML private Label lblNombreHotel;
    @FXML private Label lblUsuarioActual;
    @FXML private Button btnInicio;
    @FXML private Button btnClientes;
    @FXML private Button btnEmpleados;
    @FXML private Button btnHabitaciones;
    @FXML private Button btnReservas;
    @FXML private Button btnConfiguracion;
    @FXML private VBox panelCentral;
    @FXML private Label lblReservasDia;
    @FXML private Label lblHabitacionesLibres;
    @FXML private TableView<Actividad> tblActividad;
    @FXML private TableColumn<Actividad, String> colFechaHora;
    @FXML private TableColumn<Actividad, String> colUsuario;
    @FXML private TableColumn<Actividad, String> colAccion;

    private ObservableList<Actividad> listaActividades = FXCollections.observableArrayList();
    private Map<String, Parent> vistas = new HashMap<>();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colFechaHora.setCellValueFactory(new PropertyValueFactory<>("fechaHora"));
        colUsuario.setCellValueFactory(new PropertyValueFactory<>("usuario"));
        colAccion.setCellValueFactory(new PropertyValueFactory<>("accion"));
        tblActividad.setItems(listaActividades);

        lblUsuarioActual.setText(Sesion.getNombre() + " (" + Sesion.getUsuario() + ")");
        cargarDashboard();
    }

    /** Lee de la base de datos los contadores y la bitácora de actividad. */
    private void cargarDashboard() {
        HabitacionDAO habitacionDAO = new HabitacionDAO();
        ReservacionDAO reservacionDAO = new ReservacionDAO();

        lblReservasDia.setText(String.valueOf(reservacionDAO.contarEntradasHoy()));
        lblHabitacionesLibres.setText(habitacionDAO.contar("Disponible") + " / " + habitacionDAO.contar(null));
        listaActividades.setAll(actividadDAO.listarRecientes(50));
    }

    private final ActividadDAO actividadDAO = new ActividadDAO();

    public void registrarActividad(String usuario, String accion) {
        ActividadDAO.registrar(usuario, accion);
        listaActividades.setAll(actividadDAO.listarRecientes(50));
    }

    @FXML
    private void mostrarInicio(ActionEvent event) {
        cargarDashboard(); // refresca los datos al volver al inicio
        contenedor.setCenter(panelCentral);
    }

    @FXML
    private void abrirClientes(ActionEvent event) {
        // Abre el módulo de Clientes (tabla clientes)
        mostrarVista("GestionUsuarios");
    }

    @FXML
    private void abrirConfiguracion(ActionEvent event) {
        // Abre el módulo del Administrador (tabla administradores)
        mostrarVista("administrador");
    }

    @FXML
    private void abrirEmpleados(ActionEvent event) {
        // Abre el módulo de Gestión de Empleados
        mostrarVista("GestionEmpleados");
    }

    @FXML
    private void abrirHabitaciones(ActionEvent event) {
        mostrarVista("GestionHabitaciones");
    }

    @FXML
    private void abrirReservas(ActionEvent event) {
        mostrarVista("GestionReservas");
    }

    private void mostrarVista(String nombreFxml) {
        Parent vista = vistas.get(nombreFxml);
        try {
            // GestionReservas se recarga siempre para ver las habitaciones nuevas
            if (vista == null || "GestionReservas".equals(nombreFxml)) {
                FXMLLoader cargador = new FXMLLoader(App.class.getResource(nombreFxml + ".fxml"));
                vista = cargador.load();
                vistas.put(nombreFxml, vista);
            }
            contenedor.setCenter(vista);
        } catch (IOException ex) {
            ex.printStackTrace(); // Imprime el error exacto en la consola de NetBeans
            Alert alerta = new Alert(Alert.AlertType.ERROR);
            alerta.setTitle("Error");
            alerta.setHeaderText(null);
            alerta.setContentText("No se pudo abrir la pantalla " + nombreFxml + ".");
            alerta.showAndWait();
        }
    }

    @FXML
    private void cerrarSesion(ActionEvent event) {
        try {
            ActividadDAO.registrar("Cierre de sesión");
            Sesion.cerrar();
            App.setRoot("Login");
        } catch (IOException ex) {
            Alert alerta = new Alert(Alert.AlertType.ERROR);
            alerta.setTitle("Error");
            alerta.setHeaderText(null);
            alerta.setContentText("No se pudo regresar a la pantalla de Login.");
            alerta.showAndWait();
        }
    }
}