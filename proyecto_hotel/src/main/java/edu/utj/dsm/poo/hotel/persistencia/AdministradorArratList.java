
package edu.utj.dsm.poo.hotel.persistencia;

import edu.utj.dsm.poo.hotel.modelo.Administrador;
import java.util.ArrayList;

/**
 *
 * @author Victor Meza
 */
public class AdministradorArratList implements Listable<Administrador>{

    
    private final ArrayList<Administrador> lista;

    public AdministradorArratList() {
        this.lista = new ArrayList<>();
    }
    
    
    
    
    @Override
    public boolean agregar(Administrador elemento) {
        return lista.add(elemento);
    }

    @Override
    public boolean actualizar(Administrador elemento, int posicion) {
        if(lista.contains(elemento)){
            lista.set(posicion, elemento);
            return true;
        }
        return false;
    }

    @Override
    public boolean eliminar(Administrador elemento) {
        return lista.remove(elemento);


    }

    @Override
    public ArrayList<Administrador> listar() {
        return (ArrayList<Administrador>) lista.clone();
        


    }

    @Override
    public ArrayList<Administrador> consultar(String dato) {
        return null;
        }

    @Override
    public Administrador traer(String dato) {
        Administrador aux = new Administrador();
        aux.setRfc(dato);
        if(lista.contains(aux)){
            int posicion = lista.indexOf(aux);
            return lista.get(posicion);
        }
        return null;

    }

  
    
}
