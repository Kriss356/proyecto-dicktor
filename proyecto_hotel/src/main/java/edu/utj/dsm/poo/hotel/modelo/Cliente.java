/*
Victor Uriel Meza Arias
*/
package edu.utj.dsm.poo.hotel.modelo;



/**
 *
 * @author Victor Meza
 */
public final class Cliente extends Persona{
    private String domicilio;
    private String token;
    
    public Cliente(String rfc, String nombre, String email, String telefono){
        setRfc(rfc);
        setNombre(nombre);
        setEmail(email);
        setTelefono(telefono);
       
    }
}
 