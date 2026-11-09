package edu.utj.dsm.poo.hotel.modelo;

import java.time.LocalDate;

public class Reservacion {

    private String folio;
    private Cliente cliente;
    private Habitacion habitacion;
    private LocalDate fechaEntrada;
    private LocalDate fechaSalida;
    private double costoTotal;
    private String estado;

    public Reservacion() {
        this.folio = "";
        this.cliente = null;
        this.habitacion = null;
        this.fechaEntrada = LocalDate.now();
        this.fechaSalida = LocalDate.now();
        this.costoTotal = 0.0;
        this.estado = "PENDIENTE";
    }

    // Constructor completo
    public Reservacion(String folio, Cliente cliente, Habitacion habitacion, LocalDate fechaEntrada, LocalDate fechaSalida, double costoTotal, String estado) {
        this.folio = folio;
        this.cliente = cliente;
        this.habitacion = habitacion;
        this.fechaEntrada = fechaEntrada;
        this.fechaSalida = fechaSalida;
        this.costoTotal = costoTotal;
        this.estado = estado;
    }

    public void setFolio(String folio) { 
        this.folio = folio; 
    }
    public void setCliente(Cliente cliente) { 
        this.cliente = cliente; 
    }
    public void setHabitacion(Habitacion habitacion) { 
        this.habitacion = habitacion;
    }
    public void setFechaEntrada(LocalDate fechaEntrada) { 
        this.fechaEntrada = fechaEntrada;
    }
    public void setFechaSalida(LocalDate fechaSalida) { 
        this.fechaSalida = fechaSalida;
    }
    public void setCostoTotal(double costoTotal) { 
        this.costoTotal = costoTotal; 
    }
    public void setEstado(String estado) { 
        this.estado = estado; 
    }

    public String getFolio() { 
        return folio; 
    }
    public Cliente getCliente() { 
        return cliente; 
    }
    public Habitacion getHabitacion() { 
        return habitacion; 
    }
    public LocalDate getFechaEntrada() { 
        return fechaEntrada;
    }
    public LocalDate getFechaSalida() { 
        return fechaSalida; 
    }
    public double getCostoTotal() { 
        return costoTotal; 
    }
    public String getEstado() { 
        return estado;
    }
}