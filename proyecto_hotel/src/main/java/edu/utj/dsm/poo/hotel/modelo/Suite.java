
package edu.utj.dsm.poo.hotel.modelo;

/**
 *
 * @author Victor Meza
 */
public final class Suite  extends Habitacion{

 
    private boolean incluyeDesayuno;
    private boolean miniBar;
    private boolean jacuzzi;
    private int numeroDeCamas;
    
    
    
    
    public Suite(){
        this.incluyeDesayuno = false;
        this.miniBar = false;
        this.jacuzzi = false;
        this.numeroDeCamas = 0;
    }
    
    public Suite(String numeroHabitacion, String tipo, int piso, 
                 int numeroDeCamas, boolean jacuzzi, boolean miniBar) {
        super(numeroHabitacion, tipo, piso); // Pasa los datos a la clase padre
        this.numeroDeCamas = numeroDeCamas;
        this.jacuzzi = jacuzzi;
        this.miniBar = miniBar;
    }

    public boolean isIncluyeDesayuno() {
        return incluyeDesayuno;
    }

    public void setIncluyeDesayuno(boolean incluyeDesayuno) {
        this.incluyeDesayuno = incluyeDesayuno;
    }

    public boolean isMiniBar() {
        return miniBar;
    }

    public void setMiniBar(boolean miniBar) {
        this.miniBar = miniBar;
    }

    public boolean isJacuzzi() {
        return jacuzzi;
    }

    public void setJacuzzi(boolean jacuzzi) {
        this.jacuzzi = jacuzzi;
    }

    public int getNumeroDeCamas() {
        return numeroDeCamas;
    }

    public void setNumeroDeCamas(int numeroDeCamas) {
        this.numeroDeCamas = numeroDeCamas;
    }
       @Override
    public String toString() {
        return "Suite{" + "incluyeDesayuno=" + incluyeDesayuno + ", miniBar=" + miniBar + ", jacuzzi=" + jacuzzi + ", numeroDeCamas=" + numeroDeCamas + '}';
    }
}
