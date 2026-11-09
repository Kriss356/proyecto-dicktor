package edu.utj.dsm.poo.hotel.controlador;

import edu.utj.dsm.poo.hotel.modelo.Administrador;
import edu.utj.dsm.poo.hotel.modelo.Empleado;
import edu.utj.dsm.poo.hotel.modelo.Mantenimiento;
import edu.utj.dsm.poo.hotel.modelo.Recepcion;
import edu.utj.dsm.poo.hotel.persistencia.ActividadDAO;
import edu.utj.dsm.poo.hotel.persistencia.EmpleadoDAO;
import edu.utj.dsm.poo.hotel.persistencia.GestorImagenes;
import edu.utj.dsm.poo.hotel.validaciones.ValidadorForm;
import edu.utj.dsm.poo.hotel.validaciones.Validaciones;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

/**
 * Controlador para la Gestión de Empleados integrado con JDBC / MySQL
 * 
 * @author Victor Meza
 */
public class GestionEmpleadosController implements Initializable {

    // Campos de Texto y Formulario
    @FXML private TextField txtNombre;
    @FXML private TextField txtPrimerApellido;
    @FXML private TextField txtSegundoApellido;
    @FXML private DatePicker dpNacimiento;
    @FXML private TextField txtEmail;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtRfc;
    @FXML private TextField txtUsuario;
    @FXML private PasswordField pwdContrasena;
    @FXML private ComboBox<String> cbRol;

    // Componentes de Imagen y Botones
    @FXML private Button btnImagen;
    @FXML private ImageView imgEmpleado;

    @FXML private Button btnGuardar;
    @FXML private Button btnValidar;
    @FXML private Button btnCancelar;
    @FXML private Button btnLimpiar;
    @FXML private Button btnEditar;
    @FXML private Button btnEliminar;

    // Etiquetas de Error para Validación
    @FXML private Label errNombre, errPrimerApellido, errSegundoApellido, errNacimiento;
    @FXML private Label errEmail, errTelefono, errRfc, errUsuario, errContrasena, errRol;

    // Tabla y Columnas
    @FXML private TableView<Empleado> tblEmpleados;
    @FXML private TableColumn<Empleado, String> colNombre;
    @FXML private TableColumn<Empleado, String> colApellido;
    @FXML private TableColumn<Empleado, String> colUsuario;
    @FXML private TableColumn<Empleado, String> colRol;
    @FXML private TableColumn<Empleado, String> colCorreo;
    @FXML private TableColumn<Empleado, String> colTelefono;
    @FXML private TableColumn<Empleado, String> colRfc;

    private ObservableList<Empleado> listaEmpleados = FXCollections.observableArrayList();
    private EmpleadoDAO empleadoDAO = new EmpleadoDAO();
    private ValidadorForm validador;
    private File archivoImagenSeleccionado = null;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarTabla();
        configurarValidaciones();

        cbRol.getItems().addAll("Administrador", "Recepción", "Mantenimiento");

        tblEmpleados.setItems(listaEmpleados);
        cargarDatosDesdeBD();

        // Listener de selección en la tabla
        tblEmpleados.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                llenarFormulario(newSel);
            }
        });
    }

    private void cargarDatosDesdeBD() {
        listaEmpleados.setAll(empleadoDAO.listarTodos());
    }

    private void configurarTabla() {
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colCorreo.setCellValueFactory(new PropertyValueFactory<>("email"));
        colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        colRfc.setCellValueFactory(new PropertyValueFactory<>("rfc"));

        colApellido.setCellValueFactory(cellData -> {
            Empleado emp = cellData.getValue();
            String primer = emp.getPrimerApellido() != null ? emp.getPrimerApellido() : "";
            String segundo = emp.getSegundoApellido() != null ? emp.getSegundoApellido() : "";
            return new SimpleStringProperty((primer + " " + segundo).trim());
        });

        colUsuario.setCellValueFactory(cellData -> {
            Empleado emp = cellData.getValue();
            return new SimpleStringProperty(emp.getUsuario() != null ? emp.getUsuario() : "");
        });

        colRol.setCellValueFactory(cellData -> {
            Empleado emp = cellData.getValue();
            return new SimpleStringProperty(emp.getRol() != null ? emp.getRol() : "");
        });
    }

    @FXML
    private void seleccionarImagen(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar Foto de Empleado");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Archivos de Imagen", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        File seleccionado = fileChooser.showOpenDialog(imgEmpleado.getScene().getWindow());
        if (seleccionado != null) {
            this.archivoImagenSeleccionado = seleccionado;
            imgEmpleado.setImage(new Image(seleccionado.toURI().toString()));
        }
    }

    @FXML
    private void guardarEmpleado(ActionEvent event) {
        Empleado nuevo = construirDesdeFormulario();
        if (nuevo == null) return;

        if (buscarPorRfc(nuevo.getRfc()) != -1) {
            mostrarAlerta(Alert.AlertType.WARNING, "RFC Repetido", "Ya existe un empleado con ese RFC.");
            return;
        }

        try {
            if (archivoImagenSeleccionado != null) {
                String rutaGuardada = GestorImagenes.guardarImagen(archivoImagenSeleccionado);
                nuevo.setRutaImagen(rutaGuardada);
            }

            if (empleadoDAO.guardar(nuevo)) {
                ActividadDAO.registrar("Registró al empleado " + nuevo.getUsuario());
                cargarDatosDesdeBD();
                limpiarCampos(event);
                mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Empleado guardado en la base de datos.");
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "Error de BD", "No se pudo guardar el empleado en MySQL.");
            }

        } catch (IOException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de Archivo", "No se pudo copiar la imagen: " + e.getMessage());
        }
    }

    @FXML
    private void editarEmpleado(ActionEvent event) {
        Empleado seleccionado = tblEmpleados.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Sin Selección", "Selecciona un empleado de la tabla.");
            return;
        }

        Empleado datosNuevos = construirDesdeFormulario();
        if (datosNuevos == null) return;

        int indexExistente = buscarPorRfc(datosNuevos.getRfc());
        int indexActual = listaEmpleados.indexOf(seleccionado);

        if (indexExistente != -1 && indexExistente != indexActual) {
            mostrarAlerta(Alert.AlertType.WARNING, "RFC Repetido", "Ya existe otro empleado registrado con ese RFC.");
            return;
        }

        try {
            String rutaFinal = seleccionado.getRutaImagen();

            if (archivoImagenSeleccionado != null) {
                GestorImagenes.eliminarImagen(seleccionado.getRutaImagen());
                rutaFinal = GestorImagenes.guardarImagen(archivoImagenSeleccionado);
            }

            datosNuevos.setRutaImagen(rutaFinal);

            if (empleadoDAO.actualizar(datosNuevos)) {
                ActividadDAO.registrar("Actualizó al empleado " + datosNuevos.getUsuario());
                cargarDatosDesdeBD();
                limpiarCampos(event);
                mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Empleado actualizado en la base de datos.");
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "Error de BD", "No se pudo actualizar el empleado en MySQL.");
            }

        } catch (IOException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de Archivo", "No se pudo actualizar la imagen: " + e.getMessage());
        }
    }

    @FXML
    private void eliminarEmpleado(ActionEvent event) {
        Empleado seleccionado = tblEmpleados.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Sin Selección", "Selecciona un empleado para eliminar.");
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar Eliminación");
        confirmacion.setHeaderText(null);
        confirmacion.setContentText("¿Deseas eliminar al empleado: " + seleccionado.getNombre() + " " + seleccionado.getPrimerApellido() + "?");

        Optional<ButtonType> res = confirmacion.showAndWait();
        if (res.isPresent() && res.get() == ButtonType.OK) {
            if (empleadoDAO.eliminar(seleccionado.getRfc())) {
                ActividadDAO.registrar("Eliminó al empleado " + seleccionado.getUsuario());
                GestorImagenes.eliminarImagen(seleccionado.getRutaImagen());
                cargarDatosDesdeBD();
                limpiarCampos(event);
                mostrarAlerta(Alert.AlertType.INFORMATION, "Eliminado", "Empleado eliminado de la base de datos.");
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "Error de BD", "No se pudo eliminar el empleado en MySQL.");
            }
        }
    }

    @FXML
    private void limpiarCampos(ActionEvent event) {
        txtNombre.clear();
        txtPrimerApellido.clear();
        txtSegundoApellido.clear();
        dpNacimiento.setValue(null);
        txtEmail.clear();
        txtTelefono.clear();
        txtRfc.clear();
        txtUsuario.clear();
        pwdContrasena.clear();
        cbRol.setValue(null);

        archivoImagenSeleccionado = null;
        if (imgEmpleado != null) {
            imgEmpleado.setImage(null);
        }

        tblEmpleados.getSelectionModel().clearSelection();
        validador.limpiarMensajes();
        actualizarBotones();
    }

    private Empleado construirDesdeFormulario() {
        String rol = cbRol.getValue();
        if (rol == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Falta Rol", "Selecciona un rol para el empleado.");
            return null;
        }

        Empleado emp;
        switch (rol) {
            case "Administrador":
                emp = new Administrador();
                break;
            case "Recepción":
                emp = new Recepcion();
                break;
            case "Mantenimiento":
            default:
                emp = new Mantenimiento();
                break;
        }

        emp.setNombre(txtNombre.getText().trim());
        emp.setPrimerApellido(txtPrimerApellido.getText().trim());
        emp.setSegundoApellido(txtSegundoApellido.getText().trim());
        emp.setEmail(txtEmail.getText().trim());
        emp.setTelefono(txtTelefono.getText().trim());
        emp.setRfc(txtRfc.getText().trim());
        emp.setUsuario(txtUsuario.getText().trim());
        emp.setContrasena(pwdContrasena.getText().trim());

        if (dpNacimiento.getValue() != null) {
            Date fecha = Date.from(dpNacimiento.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());
            emp.setNacimiento(fecha);
        }

        return emp;
    }

    private void llenarFormulario(Empleado emp) {
        txtNombre.setText(emp.getNombre());
        txtPrimerApellido.setText(emp.getPrimerApellido());
        txtSegundoApellido.setText(emp.getSegundoApellido());
        txtEmail.setText(emp.getEmail());
        txtTelefono.setText(emp.getTelefono());
        txtRfc.setText(emp.getRfc());
        txtUsuario.setText(emp.getUsuario());
        pwdContrasena.setText(emp.getContrasena());

        if (emp.getRol() != null) {
            cbRol.setValue(emp.getRol());
        }

        archivoImagenSeleccionado = null;
        String ruta = emp.getRutaImagen();
        if (ruta != null && !ruta.trim().isEmpty()) {
            File f = new File(ruta);
            if (f.exists()) {
                imgEmpleado.setImage(new Image(f.toURI().toString()));
            } else {
                imgEmpleado.setImage(null);
            }
        } else {
            imgEmpleado.setImage(null);
        }
    }

    private int buscarPorRfc(String rfc) {
        for (int i = 0; i < listaEmpleados.size(); i++) {
            if (listaEmpleados.get(i).getRfc().equalsIgnoreCase(rfc)) {
                return i;
            }
        }
        return -1;
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    private void configurarValidaciones() {
        validador = new ValidadorForm(this::actualizarBotones);

        Validaciones.soloLetras(txtNombre, errNombre);
        Validaciones.soloLetras(txtPrimerApellido, errPrimerApellido);
        Validaciones.rfc(txtRfc, errRfc);
        Validaciones.soloDigitos(txtTelefono, errTelefono, 10);

        validador.agregar(errNombre, () -> Validaciones.letras(txtNombre.getText()), txtNombre.textProperty());
        validador.agregar(errPrimerApellido, () -> Validaciones.letras(txtPrimerApellido.getText()), txtPrimerApellido.textProperty());
        validador.agregar(errEmail, () -> Validaciones.email(txtEmail.getText()), txtEmail.textProperty());
        validador.agregar(errRfc, () -> Validaciones.rfc(txtRfc.getText()), txtRfc.textProperty());
        validador.agregar(errTelefono, () -> Validaciones.digitos(txtTelefono.getText(), 10), txtTelefono.textProperty());
        validador.agregar(errRol, () -> Validaciones.seleccion(cbRol.getValue()), cbRol.valueProperty());

        tblEmpleados.getSelectionModel().selectedItemProperty().addListener((obs, anterior, nuevo) -> actualizarBotones());
        actualizarBotones();
    }

    private void actualizarBotones() {
        btnGuardar.setDisable(!validador.esValido());
        boolean haySeleccion = tblEmpleados.getSelectionModel().getSelectedItem() != null;
        btnEditar.setDisable(!haySeleccion);
        btnEliminar.setDisable(!haySeleccion);
    }

    @FXML
    private void validarFormulario(ActionEvent event) {
        if (validador.validarTodo()) {
            mostrarAlerta(Alert.AlertType.INFORMATION, "Validación", "Todos los datos del empleado cumplen las reglas.");
        }
        actualizarBotones();
    }
}