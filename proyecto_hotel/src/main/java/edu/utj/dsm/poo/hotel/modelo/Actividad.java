    package edu.utj.dsm.poo.hotel.modelo;

public class Actividad {

    private String fechaHora;
    private String usuario;
    private String accion;

    public Actividad() {
        this.fechaHora = "";
        this.usuario = "";
        this.accion = "";
    }   

    public Actividad(String fechaHora, String usuario, String accion) {
        this.fechaHora = fechaHora;
        this.usuario = usuario;
        this.accion = accion;
    }

    public String getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(String fechaHora) {
        this.fechaHora = fechaHora;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getAccion() {
        return accion;
    }

    public void setAccion(String accion) {
        this.accion = accion;
    }
}