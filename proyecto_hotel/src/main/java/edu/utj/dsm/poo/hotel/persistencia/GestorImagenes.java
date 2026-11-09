package edu.utj.dsm.poo.hotel.persistencia;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;

public class GestorImagenes {

    private static final String DIRECTORIO_IMAGENES = "uploads/imagenes/";

    /**
     * Copia la imagen seleccionada a la carpeta local del proyecto con un nombre único.
     */
    public static String guardarImagen(File archivoOrigen) throws IOException {
        if (archivoOrigen == null) return null;

        File directorio = new File(DIRECTORIO_IMAGENES);
        if (!directorio.exists()) {
            directorio.mkdirs();
        }

        String extension = "";
        int i = archivoOrigen.getName().lastIndexOf('.');
        if (i > 0) {
            extension = archivoOrigen.getName().substring(i);
        }

        String nombreNuevo = "img_" + System.currentTimeMillis() + extension;
        Path destino = Paths.get(DIRECTORIO_IMAGENES + nombreNuevo);

        Files.copy(archivoOrigen.toPath(), destino, StandardCopyOption.REPLACE_EXISTING);

        return destino.toString();
    }

    /**
     * Elimina el archivo físico de la imagen cuando se borra un registro.
     */
    public static void eliminarImagen(String ruta) {
        if (ruta != null && !ruta.trim().isEmpty()) {
            try {
                Path path = Paths.get(ruta);
                Files.deleteIfExists(path);
            } catch (IOException e) {
                System.err.println("No se pudo eliminar la imagen: " + e.getMessage());
            }
        }
    }
}