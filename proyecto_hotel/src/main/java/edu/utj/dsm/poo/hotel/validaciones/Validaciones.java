package edu.utj.dsm.poo.hotel.validaciones;

import java.time.LocalDate;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;

/**
 * Reglas de validación (regresan el mensaje de error o null si es válido)
 * y filtros de teclado/pegado para los campos de texto.
 *
 * @author Victor Meza
 */
public final class Validaciones {

    private static final String LETRAS = "[A-Za-zÁÉÍÓÚÜÑáéíóúüñ ]*";

    private Validaciones() {
    }


    private static boolean vacio(String texto) {
        return texto == null || texto.trim().isEmpty();
    }

    public static String letras(String texto) {
        if (vacio(texto)) {
            return "Este campo es obligatorio";
        }
        if (!texto.matches(LETRAS)) {
            return "Solo se permiten letras y espacios";
        }
        return null;
    }

    public static String email(String texto) {
        if (vacio(texto)) {
            return "Este campo es obligatorio";
        }
        if (!texto.trim().matches("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$")) {
            return "Formato: nombre@dominio.com";
        }
        return null;
    }

    public static String digitos(String texto, int longitud) {
        if (vacio(texto)) {
            return "Este campo es obligatorio";
        }
        if (!texto.matches("\\d{" + longitud + "}")) {
            return "Debe tener " + longitud + " dígitos";
        }
        return null;
    }

    public static String rfc(String texto) {
        if (vacio(texto)) {
            return "Este campo es obligatorio";
        }
        if (!texto.matches("[A-ZÑ&0-9]{12,13}")) {
            return "Debe tener 12 o 13 letras y números";
        }
        return null;
    }

    public static String usuario(String texto) {
        if (vacio(texto)) {
            return "Este campo es obligatorio";
        }
        if (!texto.matches("[A-Za-z0-9._]{4,20}")) {
            return "De 4 a 20 caracteres: letras, números, . o _";
        }
        return null;
    }

    public static String contrasena(String texto) {
        if (vacio(texto)) {
            return "Este campo es obligatorio";
        }
        if (texto.length() < 6) {
            return "Debe tener al menos 6 caracteres";
        }
        return null;
    }

    public static String entero(String texto, int minimo) {
        if (vacio(texto)) {
            return "Este campo es obligatorio";
        }
        if (!texto.matches("\\d{1,9}") || Integer.parseInt(texto) < minimo) {
            return "Escribe un número entero mayor o igual a " + minimo;
        }
        return null;
    }

    public static String decimalPositivo(String texto) {
        if (vacio(texto)) {
            return "Este campo es obligatorio";
        }
        if (!texto.matches("\\d{1,9}(\\.\\d{1,2})?") || Double.parseDouble(texto) <= 0) {
            return "Escribe un número mayor que 0 (ej. 850.50)";
        }
        return null;
    }

    public static String seleccion(Object valor) {
        return valor == null ? "Selecciona una opción" : null;
    }

    public static String fecha(LocalDate fecha) {
        return fecha == null ? "Selecciona una fecha" : null;
    }

    public static String fechaPasadaOpcional(LocalDate fecha) {
        if (fecha != null && !fecha.isBefore(LocalDate.now())) {
            return "Debe ser una fecha anterior a hoy";
        }
        return null;
    }

    //Filtros (bloquean caracteres al escribir y al pegar)

    private static void filtrar(TextField campo, Label error, String patron, int max, String aviso, boolean mayusculas) {
        campo.setTextFormatter(new TextFormatter<String>(cambio -> {
            if (mayusculas) {
                cambio.setText(cambio.getText().toUpperCase());
            }
            String nuevo = cambio.getControlNewText();
            if (nuevo.length() <= max && nuevo.matches(patron)) {
                return cambio;
            }
            error.setText(aviso); // se borra solo en el siguiente cambio válido
            return null;
        }));
    }

    public static void soloLetras(TextField campo, Label error) {
        filtrar(campo, error, LETRAS, 60, "Solo se permiten letras y espacios", false);
    }

    public static void soloDigitos(TextField campo, Label error, int max) {
        filtrar(campo, error, "\\d*", max, "Solo se permiten números (máx. " + max + " dígitos)", false);
    }

    public static void decimal(TextField campo, Label error) {
        filtrar(campo, error, "\\d{0,9}(\\.\\d{0,2})?", 12, "Solo números, con punto decimal (máx. 2 decimales)", false);
    }

    public static void rfc(TextField campo, Label error) {
        filtrar(campo, error, "[A-ZÑ&0-9]*", 13, "Solo letras y números (máx. 13)", true);
    }

    public static void usuario(TextField campo, Label error) {
        filtrar(campo, error, "[A-Za-z0-9._]*", 20, "Solo letras, números, . o _ (sin espacios)", false);
    }
}