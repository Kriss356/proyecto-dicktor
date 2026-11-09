package edu.utj.dsm.poo.hotel.modelo;


/**
 *
 * @author Victor Meza
 */
public abstract class Empleado  extends Persona{
    protected String usuario;
    protected String contrasena;
    protected String salario;
    
   


public Empleado(){
    this.salario = "";
    this.usuario = "";
    this.contrasena = "";
}

    public String getSalario() {
        return salario;
    }

    public void setSalario(String salario) {
        this.salario = salario;
    }

public void setUsuario(String usuario){
        this.usuario = usuario;
        
    }
public void setContrasena(String contraseña){
        this.contrasena = contraseña;
        
    }



public String getUsuario(){
        return this.usuario;
    }
public String getContrasena(){
        return this.contrasena;
}



    public abstract String getRol();

}