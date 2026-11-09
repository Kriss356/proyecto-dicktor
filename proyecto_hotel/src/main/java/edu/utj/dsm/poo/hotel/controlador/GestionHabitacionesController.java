package edu.utj.dsm.poo.hotel.controlador;

import edu.utj.dsm.poo.hotel.modelo.Habitacion;
import edu.utj.dsm.poo.hotel.modelo.HabitacionSencilla;
import edu.utj.dsm.poo.hotel.modelo.Suite;
import edu.utj.dsm.poo.hotel.persistencia.ActividadDAO;
import edu.utj.dsm.poo.hotel.persistencia.GestorImagenes;
import edu.utj.dsm.poo.hotel.persistencia.HabitacionDAO;
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
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
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
public class GestionHabitacionesController implements Initializable {

    @FXML
    private TextField txtNumero;
    @FXML
    private ComboBox<String> cbTipo;
    @FXML
    private TextField txtPiso;
    @FXML
    private TextField txtPrecio;
    @FXML
    private TextField txtCapacidad;
    @FXML
    private TextField txtCamas;
    @FXML
    private ComboBox<String> cbEstado;
    @FXML
    private CheckBox chkBalcon;
    @FXML
    private CheckBox chkVistaAlMar;
    @FXML
    private CheckBox chkJacuzzi;
    @FXML
    private CheckBox chkMiniBar;
    @FXML
    private CheckBox chkDesayuno;

    // Componente para la gestión visual de imágenes
    @FXML
    private ImageView imgVistaPrevia;

    @FXML
    private Button btnGuardar;
    @FXML
    private Button btnEditar;
    @FXML
    private Button btnEliminar;
    @FXML
    private Button btnLimpiar;
    @FXML
    private Button btnCancelar;
    @FXML
    private TableView<Habitacion> tblHabitaciones;

    @FXML
    private TableColumn<Habitacion, String> colNumero;
    @FXML
    private TableColumn<Habitacion, String> colTipo;
    @FXML
    private TableColumn<Habitacion, Integer> colPiso;
    @FXML
    private TableColumn<Habitacion, Double> colPrecio;
    @FXML
    private TableColumn<Habitacion, Integer> colCapacidad;
    @FXML
    private TableColumn<Habitacion, String> colEstado;

    private ObservableList<Habitacion> listaHabitaciones = FXCollections.observableArrayList();
    private File archivoImagenSeleccionado = null;
    private final HabitacionDAO habitacionDAO = new HabitacionDAO();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarValidaciones();
        colNumero.setCellValueFactory(new PropertyValueFactory<>("numeroHabitacion"));
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));
        colPiso.setCellValueFactory(new PropertyValueFactory<>("piso"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precioPorNoche"));
        colCapacidad.setCellValueFactory(new PropertyValueFactory<>("capacidad"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));

        cbTipo.getItems().addAll("Sencilla", "Suite");
        cbEstado.getItems().addAll("Disponible", "Ocupada", "Mantenimiento");
        cbEstado.setValue("Disponible");

        tblHabitaciones.setItems(listaHabitaciones);
        cargarDatosDesdeBD();

        cbTipo.valueProperty().addListener((obs, tipoAnterior, tipoNuevo) -> {
            ajustarExtras(tipoNuevo);
        });
        ajustarExtras(null);

        tblHabitaciones.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                llenarFormulario(newSelection);
            }
        });
    }

    private void cargarDatosDesdeBD() {
        listaHabitaciones.setAll(habitacionDAO.listarTodas());
    }

    // ==========================================
    // SELECCIÓN DE IMAGEN MEDIANTE FILECHOOSER
    // ==========================================
    @FXML
    private void seleccionarImagen(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar Imagen de Habitación");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Archivos de Imagen", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        File seleccionado = fileChooser.showOpenDialog(imgVistaPrevia.getScene().getWindow());
        if (seleccionado != null) {
            this.archivoImagenSeleccionado = seleccionado;
            imgVistaPrevia.setImage(new Image(seleccionado.toURI().toString()));
        }
    }

    // ==========================================
    // GUARDAR (CREAR REGISTRO + IMAGEN FÍSICA)
    // ==========================================
    @FXML
    private void guardarHabitacion(ActionEvent event) {
        Habitacion nueva = construirDesdeFormulario();
        if (nueva == null) {
            return;
        }

        if (buscarPorNumero(nueva.getNumeroHabitacion()) != -1) {
            mostrarAlerta(Alert.AlertType.WARNING, "Número repetido", "Ya existe una habitación con ese número.");
            return;
        }

        try {
            // Guardar físicamente la imagen si fue seleccionada
            if (archivoImagenSeleccionado != null) {
                String rutaGuardada = GestorImagenes.guardarImagen(archivoImagenSeleccionado);
                nueva.setRutaImagen(rutaGuardada);
            }

            if (habitacionDAO.guardar(nueva)) {
                ActividadDAO.registrar("Registró la habitación " + nueva.getNumeroHabitacion());
                cargarDatosDesdeBD();
                limpiarCampos(event);
                mostrarAlerta(Alert.AlertType.INFORMATION, "Listo", "Habitación guardada en la base de datos.");
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "Error de BD", "No se pudo guardar la habitación en MySQL.");
            }
        } catch (IOException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de Archivo", "No se pudo copiar la imagen al proyecto: " + e.getMessage());
        }
    }

    // ==========================================
    // EDITAR (ACTUALIZAR REGISTRO + REEMPLAZO DE IMAGEN)
    // ==========================================
    @FXML
    private void editarHabitacion(ActionEvent event) {
        Habitacion seleccionada = tblHabitaciones.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Sin Selección", "Selecciona una habitación de la tabla para editar.");
            return;
        }

        Habitacion datosNuevos = construirDesdeFormulario();
        if (datosNuevos == null) {
            return;
        }

        // Si cambió el número de habitación, verificar que no choque con otra existente
        int indexExistente = buscarPorNumero(datosNuevos.getNumeroHabitacion());
        int indexActual = listaHabitaciones.indexOf(seleccionada);
        if (indexExistente != -1 && indexExistente != indexActual) {
            mostrarAlerta(Alert.AlertType.WARNING, "Número repetido", "Ya existe otra habitación con ese número.");
            return;
        }

        try {
            String rutaFinal = seleccionada.getRutaImagen();

            // Si se seleccionó una nueva imagen en la edición
            if (archivoImagenSeleccionado != null) {
                // Se borra la imagen anterior físicamente de la carpeta
                GestorImagenes.eliminarImagen(seleccionada.getRutaImagen());
                // Se copia la nueva imagen
                rutaFinal = GestorImagenes.guardarImagen(archivoImagenSeleccionado);
            }

            datosNuevos.setRutaImagen(rutaFinal);

            if (habitacionDAO.actualizar(datosNuevos, seleccionada.getNumeroHabitacion())) {
                ActividadDAO.registrar("Actualizó la habitación " + datosNuevos.getNumeroHabitacion());
                cargarDatosDesdeBD();
                limpiarCampos(event);
                mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Habitación actualizada en la base de datos.");
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "Error de BD", "No se pudo actualizar la habitación en MySQL.");
            }
        } catch (IOException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de Archivo", "No se pudo actualizar la imagen: " + e.getMessage());
        }
    }

    // ==========================================
    // ELIMINAR (BORRADO FÍSICO DE DISCO + LISTA)
    // ==========================================
    @FXML
    private void eliminarHabitacion(ActionEvent event) {
        Habitacion seleccionada = tblHabitaciones.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Sin Selección", "Selecciona una habitación de la tabla para eliminar.");
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar Eliminación");
        confirmacion.setHeaderText(null);
        confirmacion.setContentText("¿Estás seguro de eliminar la habitación N° " + seleccionada.getNumeroHabitacion() + "?");

        Optional<ButtonType> resultado = confirmacion.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            if (habitacionDAO.eliminar(seleccionada.getNumeroHabitacion())) {
                // Borra la imagen física de disco
                GestorImagenes.eliminarImagen(seleccionada.getRutaImagen());
                ActividadDAO.registrar("Eliminó la habitación " + seleccionada.getNumeroHabitacion());
                cargarDatosDesdeBD();
                limpiarCampos(event);
                mostrarAlerta(Alert.AlertType.INFORMATION, "Eliminado", "Habitación eliminada de la base de datos.");
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "No se pudo eliminar",
                        "No se pudo eliminar la habitación. Si tiene reservaciones registradas, primero cámbiala a Mantenimiento.");
            }
        }
    }

    @FXML
    private void limpiarCampos(ActionEvent event) {
        txtNumero.clear();
        txtPiso.clear();
        txtPrecio.clear();
        txtCapacidad.clear();
        txtCamas.clear();
        cbTipo.setValue(null);
        cbEstado.setValue("Disponible");
        chkBalcon.setSelected(false);
        chkVistaAlMar.setSelected(false);
        chkJacuzzi.setSelected(false);
        chkMiniBar.setSelected(false);
        chkDesayuno.setSelected(false);
        
        archivoImagenSeleccionado = null;
        if (imgVistaPrevia != null) {
            imgVistaPrevia.setImage(null);
        }

        tblHabitaciones.getSelectionModel().clearSelection();
        validador.limpiarMensajes();
        actualizarBotones();
    }

    private Habitacion construirDesdeFormulario() {
        if (txtNumero.getText().trim().isEmpty()
                || txtPiso.getText().trim().isEmpty()
                || txtPrecio.getText().trim().isEmpty()
                || txtCapacidad.getText().trim().isEmpty()
                || txtCamas.getText().trim().isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Faltan datos", "Llena todos los campos.");
            return null;
        }
        if (cbTipo.getValue() == null || cbEstado.getValue() == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Faltan datos", "Elige el tipo y el estado de la habitación.");
            return null;
        }

        try {
            int piso = Integer.parseInt(txtPiso.getText().trim());
            double precio = Double.parseDouble(txtPrecio.getText().trim());
            int capacidad = Integer.parseInt(txtCapacidad.getText().trim());
            int camas = Integer.parseInt(txtCamas.getText().trim());

            if (piso < 0 || precio <= 0 || capacidad <= 0 || camas <= 0) {
                mostrarAlerta(Alert.AlertType.WARNING, "Datos inválidos",
                        "El precio, la capacidad y las camas deben ser mayores que cero, y el piso no puede ser negativo.");
                return null;
            }

            Habitacion h = crearHabitacionPorTipo(cbTipo.getValue());
            h.setNumeroHabitacion(txtNumero.getText().trim());
            h.setTipo(cbTipo.getValue());
            h.setPiso(piso);
            h.setPrecioPorNoche(precio);
            h.setCapacidad(capacidad);
            h.setEstado(cbEstado.getValue());

            if (h instanceof HabitacionSencilla) {
                HabitacionSencilla sencilla = (HabitacionSencilla) h;
                sencilla.setNumeroDeCamas(camas);
                sencilla.setBalcon(chkBalcon.isSelected());
                sencilla.setVistaAlMar(chkVistaAlMar.isSelected());
            } else if (h instanceof Suite) {
                Suite suite = (Suite) h;
                suite.setNumeroDeCamas(camas);
                suite.setJacuzzi(chkJacuzzi.isSelected());
                suite.setMiniBar(chkMiniBar.isSelected());
                suite.setIncluyeDesayuno(chkDesayuno.isSelected());
            }
            return h;

        } catch (NumberFormatException e) {
            mostrarAlerta(Alert.AlertType.WARNING, "Datos inválidos",
                    "Piso, precio, capacidad y camas deben ser números.");
            return null;
        }
    }

    private Habitacion crearHabitacionPorTipo(String tipo) {
        Habitacion nueva = null;
        if ("Sencilla".equals(tipo)) {
            nueva = new HabitacionSencilla();
        } else if ("Suite".equals(tipo)) {
            nueva = new Suite();
        }
        return nueva;
    }

    private void llenarFormulario(Habitacion h) {
        txtNumero.setText(h.getNumeroHabitacion());
        txtPiso.setText(String.valueOf(h.getPiso()));
        txtPrecio.setText(String.valueOf(h.getPrecioPorNoche()));
        txtCapacidad.setText(String.valueOf(h.getCapacidad()));
        cbEstado.setValue(h.getEstado());
        cbTipo.setValue(h.getTipo());

        if (h instanceof HabitacionSencilla) {
            HabitacionSencilla sencilla = (HabitacionSencilla) h;
            txtCamas.setText(String.valueOf(sencilla.getNumeroDeCamas()));
            chkBalcon.setSelected(sencilla.isBalcon());
            chkVistaAlMar.setSelected(sencilla.isVistaAlMar());
        } else if (h instanceof Suite) {
            Suite suite = (Suite) h;
            txtCamas.setText(String.valueOf(suite.getNumeroDeCamas()));
            chkJacuzzi.setSelected(suite.isJacuzzi());
            chkMiniBar.setSelected(suite.isMiniBar());
            chkDesayuno.setSelected(suite.isIncluyeDesayuno());
        }

        // Cargar vista previa de la imagen guardada
        archivoImagenSeleccionado = null;
        String ruta = h.getRutaImagen();
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

    private void ajustarExtras(String tipo) {
        boolean esSencilla = "Sencilla".equals(tipo);
        boolean esSuite = "Suite".equals(tipo);

        chkBalcon.setDisable(!esSencilla);
        chkVistaAlMar.setDisable(!esSencilla);
        chkJacuzzi.setDisable(!esSuite);
        chkMiniBar.setDisable(!esSuite);
        chkDesayuno.setDisable(!esSuite);

        if (!esSencilla) {
            chkBalcon.setSelected(false);
            chkVistaAlMar.setSelected(false);
        }
        if (!esSuite) {
            chkJacuzzi.setSelected(false);
            chkMiniBar.setSelected(false);
            chkDesayuno.setSelected(false);
        }
    }

    private int buscarPorNumero(String numero) {
        for (int i = 0; i < listaHabitaciones.size(); i++) {
            if (listaHabitaciones.get(i).getNumeroHabitacion().equalsIgnoreCase(numero)) {
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

    @FXML
    private Label errNumero, errTipo, errPiso, errPrecio, errCapacidad, errCamas, errEstado;
    @FXML
    private Button btnValidar;

    private ValidadorForm validador;

    private void configurarValidaciones() {
        validador = new ValidadorForm(this::actualizarBotones);

        Validaciones.soloDigitos(txtNumero, errNumero, 4);
        Validaciones.soloDigitos(txtPiso, errPiso, 2);
        Validaciones.decimal(txtPrecio, errPrecio);
        Validaciones.soloDigitos(txtCapacidad, errCapacidad, 2);
        Validaciones.soloDigitos(txtCamas, errCamas, 2);

        validador.agregar(errNumero, () -> Validaciones.entero(txtNumero.getText(), 1),
                txtNumero.textProperty());
        validador.agregar(errTipo, () -> Validaciones.seleccion(cbTipo.getValue()),
                cbTipo.valueProperty());
        validador.agregar(errPiso, () -> Validaciones.entero(txtPiso.getText(), 0),
                txtPiso.textProperty());
        validador.agregar(errPrecio, () -> Validaciones.decimalPositivo(txtPrecio.getText()),
                txtPrecio.textProperty());
        validador.agregar(errCapacidad, () -> Validaciones.entero(txtCapacidad.getText(), 1),
                txtCapacidad.textProperty());
        validador.agregar(errCamas, () -> Validaciones.entero(txtCamas.getText(), 1),
                txtCamas.textProperty());
        validador.agregar(errEstado, () -> Validaciones.seleccion(cbEstado.getValue()),
                cbEstado.valueProperty());

        tblHabitaciones.getSelectionModel().selectedItemProperty().addListener((obs, anterior, nuevo) -> actualizarBotones());
        actualizarBotones();
    }

    private void actualizarBotones() {
        btnGuardar.setDisable(!validador.esValido());
        boolean haySeleccion = tblHabitaciones.getSelectionModel().getSelectedItem() != null;
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