package edu.utj.dsm.poo.hotel.controlador;

import edu.utj.dsm.poo.hotel.modelo.Cliente;
import edu.utj.dsm.poo.hotel.persistencia.ActividadDAO;
import edu.utj.dsm.poo.hotel.persistencia.ClienteDAO;
import edu.utj.dsm.poo.hotel.persistencia.GestorImagenes;
import edu.utj.dsm.poo.hotel.validaciones.ValidadorForm;
import edu.utj.dsm.poo.hotel.validaciones.Validaciones;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

/**
 * FXML Controller class
 *
 * @author Victor Meza
 */
public class GestionUsuariosController implements Initializable {

    @FXML
    private Button btnGuardar;
    @FXML
    private Button btnCancelar;
    @FXML
    private Button btnLimpiar;
    @FXML
    private Button btnEditar;
    @FXML
    private Button btnEliminar;
    @FXML
    private Button btnValidar;

    @FXML
    private TextField txtNombre;
    @FXML
    private TextField txtEmail;
    @FXML
    private TextField txtRfc;
    @FXML
    private TextField txtTelefono;

    // Componente visual para la gestión de la foto
    @FXML
    private ImageView imgVistaPrevia;

    @FXML
    private TableView<Cliente> tblClientes;
    @FXML
    private TableColumn<Cliente, String> colNombre;
    @FXML
    private TableColumn<Cliente, String> colCorreo;
    @FXML
    private TableColumn<Cliente, String> colTelefono;
    @FXML
    private TableColumn<Cliente, String> colRfc;

    @FXML
    private Label errNombre, errEmail, errRfc, errTelefono;

    private ObservableList<Cliente> listaClientes = FXCollections.observableArrayList();
    private ValidadorForm validador;
    private File archivoImagenSeleccionado = null;
    private final ClienteDAO clienteDAO = new ClienteDAO();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarValidaciones();

        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colCorreo.setCellValueFactory(new PropertyValueFactory<>("email"));
        colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        colRfc.setCellValueFactory(new PropertyValueFactory<>("rfc"));

        tblClientes.setItems(listaClientes);
        cargarDatosDesdeBD();

        // Al seleccionar un cliente de la tabla, se cargan sus datos y su imagen física
        tblClientes.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                llenarFormulario(newSelection);
            }
        });
    }

    private void cargarDatosDesdeBD() {
        listaClientes.setAll(clienteDAO.listarTodos());
    }

    // ==========================================
    // SELECCIONAR IMAGEN (FileChooser)
    // ==========================================
    @FXML
    private void seleccionarImagen(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar Foto de Cliente");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Archivos de Imagen", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        File seleccionado = fileChooser.showOpenDialog(imgVistaPrevia.getScene().getWindow());
        if (seleccionado != null) {
            this.archivoImagenSeleccionado = seleccionado;
            imgVistaPrevia.setImage(new Image(seleccionado.toURI().toString()));
        }
    }


    @FXML
    private void guardarCliente(ActionEvent event) {
        String rfc = txtRfc.getText().trim();
        String nombre = txtNombre.getText().trim();
        String email = txtEmail.getText().trim();
        String telefono = txtTelefono.getText().trim();

        if (buscarPorRfc(rfc) != -1) {
            mostrarAlerta(Alert.AlertType.WARNING, "RFC Repetido", "Ya existe un cliente registrado con ese RFC.");
            return;
        }

        try {
            String rutaGuardada = "";
            if (archivoImagenSeleccionado != null) {
                rutaGuardada = GestorImagenes.guardarImagen(archivoImagenSeleccionado);
            }

            // AQUÍ: Creas el cliente con el constructor de 4 parámetros y luego le asignas la imagen
            Cliente nuevoCliente = new Cliente(rfc, nombre, email, telefono);
            nuevoCliente.setRutaImagen(rutaGuardada);

            if (clienteDAO.guardar(nuevoCliente)) {
                ActividadDAO.registrar("Registró al cliente " + nombre);
                cargarDatosDesdeBD();
                limpiarCampos(event);
                mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Cliente guardado en la base de datos.");
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "Error de BD", "No se pudo guardar el cliente en MySQL.");
            }

        } catch (IOException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error al guardar imagen", "No se pudo copiar el archivo al proyecto: " + e.getMessage());
        }
    }


    @FXML
    private void editarCliente(ActionEvent event) {
        Cliente seleccionado = tblClientes.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Sin Selección", "Selecciona un cliente de la tabla para editar.");
            return;
        }
        if (!validador.validarTodo()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Datos inválidos", "Corrige los campos marcados en rojo.");
            return;
        }

        String rfc = txtRfc.getText().trim();
        int indexExistente = buscarPorRfc(rfc);
        int indexActual = listaClientes.indexOf(seleccionado);
        if (indexExistente != -1 && indexExistente != indexActual) {
            mostrarAlerta(Alert.AlertType.WARNING, "RFC Repetido", "Ya existe otro cliente con ese RFC.");
            return;
        }

        try {
            String rutaFinal = seleccionado.getRutaImagen();
            if (archivoImagenSeleccionado != null) {
                GestorImagenes.eliminarImagen(seleccionado.getRutaImagen());
                rutaFinal = GestorImagenes.guardarImagen(archivoImagenSeleccionado);
            }

            Cliente actualizado = new Cliente(rfc, txtNombre.getText().trim(),
                    txtEmail.getText().trim(), txtTelefono.getText().trim());
            actualizado.setRutaImagen(rutaFinal);

            if (clienteDAO.actualizar(actualizado, seleccionado.getRfc())) {
                ActividadDAO.registrar("Actualizó al cliente " + actualizado.getNombre());
                cargarDatosDesdeBD();
                limpiarCampos(event);
                mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Cliente actualizado en la base de datos.");
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "Error de BD", "No se pudo actualizar el cliente en MySQL.");
            }
        } catch (IOException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de Archivo", "No se pudo actualizar la imagen: " + e.getMessage());
        }
    }

    @FXML
    private void eliminarCliente(ActionEvent event) {
        Cliente seleccionado = tblClientes.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Sin Selección", "Selecciona un cliente de la tabla para eliminar.");
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar Eliminación");
        confirmacion.setHeaderText(null);
        confirmacion.setContentText("¿Deseas eliminar al cliente con RFC: " + seleccionado.getRfc() + "?");

        Optional<ButtonType> resultado = confirmacion.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            if (clienteDAO.eliminar(seleccionado.getRfc())) {
                // Elimina la imagen física del sistema de archivos
                GestorImagenes.eliminarImagen(seleccionado.getRutaImagen());
                ActividadDAO.registrar("Eliminó al cliente " + seleccionado.getNombre());
                cargarDatosDesdeBD();
                limpiarCampos(event);
                mostrarAlerta(Alert.AlertType.INFORMATION, "Eliminado", "Cliente eliminado de la base de datos.");
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "Error de BD", "No se pudo eliminar el cliente en MySQL.");
            }
        }
    }

    @FXML
    private void limpiarCampos(ActionEvent event) {
        txtNombre.clear();
        txtEmail.clear();
        txtRfc.clear();
        txtTelefono.clear();

        archivoImagenSeleccionado = null;
        if (imgVistaPrevia != null) {
            imgVistaPrevia.setImage(null);
        }

        tblClientes.getSelectionModel().clearSelection();
        validador.limpiarMensajes();
        actualizarBotones();
    }

    private void llenarFormulario(Cliente c) {
        txtNombre.setText(c.getNombre());
        txtEmail.setText(c.getEmail());
        txtTelefono.setText(c.getTelefono());
        txtRfc.setText(c.getRfc());

        archivoImagenSeleccionado = null;
        String ruta = c.getRutaImagen();

        if (ruta != null && !ruta.trim().isEmpty()) {
            File f = new File(ruta);
            if (f.exists()) {
                imgVistaPrevia.setImage(new Image(f.toURI().toString()));
            } else {
                imgVistaPrevia.setImage(null);
            }
        } else {
            imgVistaPrevia.setImage(null);
        }
    }

    private int buscarPorRfc(String rfc) {
        for (int i = 0; i < listaClientes.size(); i++) {
            if (listaClientes.get(i).getRfc().equalsIgnoreCase(rfc)) {
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

    // ---------- Validaciones en tiempo real ----------

    private void configurarValidaciones() {
        validador = new ValidadorForm(this::actualizarBotones);

        // Filtros
        Validaciones.soloLetras(txtNombre, errNombre);
        Validaciones.rfc(txtRfc, errRfc);
        Validaciones.soloDigitos(txtTelefono, errTelefono, 10);

        // Reglas
        validador.agregar(errNombre, () -> Validaciones.letras(txtNombre.getText()),
                txtNombre.textProperty());
        validador.agregar(errEmail, () -> Validaciones.email(txtEmail.getText()),
                txtEmail.textProperty());
        validador.agregar(errRfc, () -> Validaciones.rfc(txtRfc.getText()),
                txtRfc.textProperty());
        validador.agregar(errTelefono, () -> Validaciones.digitos(txtTelefono.getText(), 10),
                txtTelefono.textProperty());

        tblClientes.getSelectionModel().selectedItemProperty().addListener((obs, anterior, nuevo) -> actualizarBotones());
        actualizarBotones();
    }

    private void actualizarBotones() {
        btnGuardar.setDisable(!validador.esValido());
        boolean haySeleccion = tblClientes.getSelectionModel().getSelectedItem() != null;
        btnEditar.setDisable(!haySeleccion);
        btnEliminar.setDisable(!haySeleccion);
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
}