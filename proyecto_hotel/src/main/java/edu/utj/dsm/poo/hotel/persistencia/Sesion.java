package edu.utj.dsm.poo.hotel.persistencia;

/**
 * Guarda quién inició sesión, para mostrarlo en el panel y en la bitácora.
 */
public class Sesion {

    private static String usuario = "invitado";
    private static String nombre = "";
    private static String rol = "";

    public static void iniciar(String usuarioNuevo, String nombreNuevo, String rolNuevo) {
        usuario = usuarioNuevo;
        nombre = nombreNuevo;
        rol = rolNuevo;
    }

    public static void cerrar() {
        usuario = "invitado";
        nombre = "";
        rol = "";
    }

    public static String getUsuario() {
        return usuario;
    }

    public static String getNombre() {
        return nombre;
    }

    public static String getRol() {
        return rol;
    }
}
