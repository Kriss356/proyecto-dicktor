/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.utj.dsm.poo.hotel.modelo;

/**
 *
 * @author Victor Meza
 */
public final class HabitacionSencilla extends Habitacion{

  
    private int numeroDeCamas;
    private boolean balcon;
    private boolean vistaAlMar;
    
    
    public HabitacionSencilla(){
        this.numeroDeCamas = 0;
        this.balcon = false;
        this.vistaAlMar = false;
    }
    public HabitacionSencilla(String numeroHabitacion, String tipo, int piso, 
                         int numeroDeCamas, boolean balcon, boolean vistaAlMar) {
    super(numeroHabitacion, tipo, piso); 
    this.numeroDeCamas = numeroDeCamas;
    this.balcon = balcon;
    this.vistaAlMar = vistaAlMar;
}

    public int getNumeroDeCamas() {
        return numeroDeCamas;
    }

    public void setNumeroDeCamas(int numeroDeCamas) {
        this.numeroDeCamas = numeroDeCamas;
    }

    public boolean isBalcon() {
        return balcon;
    }

    public void setBalcon(boolean balcon) {
        this.balcon = balcon;
    }

    public boolean isVistaAlMar() {
        return vistaAlMar;
    }

    public void setVistaAlMar(boolean vistaAlMar) {
        this.vistaAlMar = vistaAlMar;
    }
      @Override
    public String toString() {
        return "HabitacionSencilla{" + "numeroDeCamas=" + numeroDeCamas + ", balcon=" + balcon + ", vistaAlMar=" + vistaAlMar + '}';
    }
}
