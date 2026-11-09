package edu.utj.dsm.poo.hotel.validaciones;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Label;

/**
 * Lleva las reglas de validación de un formulario. Cada regla tiene su propio Label de error,
 * así los mensajes de un campo nunca sobrescriben los de otro.
 * Una regla es una función que regresa el mensaje de error, o null si el campo es válido.
 *
 * @author Victor Meza
 */
public class ValidadorForm {

    private static class Regla {
        Label error;
        Supplier<String> regla;
    }

    private final List<Regla> reglas = new ArrayList<>();
    private final Runnable alCambiar; // se ejecuta cada vez que cambia un campo (para actualizar botones)

    public ValidadorForm(Runnable alCambiar) {
        this.alCambiar = alCambiar;
    }

    // La regla se vuelve a evaluar cada vez que cambia alguno de los "origenes"
    public void agregar(Label error, Supplier<String> regla, ObservableValue<?>... origenes) {
        Regla r = new Regla();
        r.error = error;
        r.regla = regla;
        reglas.add(r);
        for (ObservableValue<?> origen : origenes) {
            origen.addListener((obs, anterior, nuevo) -> {
                mostrar(r);
                alCambiar.run();
            });
        }
    }

    // Revisa en silencio (sin mostrar ningun tipo de mensaje): sirve para habilitar o no el botón Guardar
    public boolean esValido() {
        for (Regla r : reglas) {
            if (r.regla.get() != null) {
                return false;
            }
        }
        return true;
    }

    // Revisa todo y muestra los mensajes de los campos con error
    public boolean validarTodo() {
        boolean valido = true;
        for (Regla r : reglas) {
            mostrar(r);
            if (r.regla.get() != null) {
                valido = false;
            }
        }
        return valido;
    }

    public void limpiarMensajes() {
        for (Regla r : reglas) {
            r.error.setText("");
        }
    }

    private void mostrar(Regla r) {
        String mensaje = r.regla.get();
        r.error.setText(mensaje == null ? "" : mensaje);
    }
}