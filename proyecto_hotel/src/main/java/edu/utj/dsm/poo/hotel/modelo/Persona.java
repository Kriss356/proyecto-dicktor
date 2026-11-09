/*
  Nombre Victor Uriel Meza Arias
 */
package edu.utj.dsm.poo.hotel.modelo;

import java.util.Date;
import java.util.Objects;

/**
 *
 * @author Victor Meza
 */
public abstract class Persona {

    private String rfc;
    private String nombre;
    private String email;
    private String primerApellido;
    private String segundoApellido;
    private Date nacimiento;
    private String rutaImagen;
    private String telefono;

    /**
     * Constructor por defecto: Inicializa los atributos con valores vacíos
     */
    public Persona() {
        this.rfc = "";
        this.nombre = "";
        this.email = "";
        this.primerApellido = "";
        this.segundoApellido = "";
        this.telefono = "";
        this.nacimiento = null;
        this.rutaImagen = "";
    }

    /**
     * Constructor completo
     */
    public Persona(String rfc, String nombre, String primerApellido, String segundoApellido, String email, String telefono, Date nacimiento, String rutaImagen) {
        this.rfc = rfc;
        this.nombre = nombre;
        this.primerApellido = primerApellido;
        this.segundoApellido = segundoApellido;
        this.email = email;
        this.telefono = telefono;
        this.nacimiento = nacimiento;
        this.rutaImagen = rutaImagen;
    }

    public void setRfc(String rfc) {
        this.rfc = rfc;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public void setPrimerApellido(String primerApellido) {
        this.primerApellido = primerApellido;
    }

    public void setSegundoApellido(String segundoApellido) {
        this.segundoApellido = segundoApellido;
    }

    public void setNacimiento(Date nacimiento) {
        this.nacimiento = nacimiento;
    }

    public void setRutaImagen(String rutaImagen) {
        this.rutaImagen = rutaImagen;
    }

    public String getRfc() {
        return this.rfc;
    }

    public String getEmail() {
        return this.email;
    }

    public String getNombre() {
        return this.nombre;
    }

    public String getTelefono() {
        return this.telefono;
    }

    public String getPrimerApellido() {
        return this.primerApellido;
    }

    public String getSegundoApellido() {
        return this.segundoApellido;
    }

    public Date getNacimiento() {
        return this.nacimiento;
    }

    public String getRutaImagen() {
        return this.rutaImagen;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 79 * hash + Objects.hashCode(this.rfc);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Persona other = (Persona) obj;
        return Objects.equals(this.rfc, other.rfc);
    }

    @Override
    public String toString() {
        return "Persona{" + "rfc=" + rfc + ", nombre=" + nombre + ", primerApellido=" + primerApellido + ", segundoApellido=" + segundoApellido + ", nacimiento=" + nacimiento + ", rutaImagen=" + rutaImagen + '}';
    }
}