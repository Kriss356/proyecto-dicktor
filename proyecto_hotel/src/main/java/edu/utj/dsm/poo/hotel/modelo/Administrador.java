package edu.utj.dsm.poo.hotel.modelo;

public class Administrador extends Empleado {
    private int id;
    private String direccion;
    private String estado;
    private String rolPersonalizado;

    public Administrador() {
        super();
        this.id = 0;
        this.direccion = "";
        this.estado = "ACTIVO";
        this.rolPersonalizado = "ADMINISTRADOR";
    }

    public Administrador(int id, String nombre, String telefono, String email, String direccion, 
                         String usuario, String claveAcceso, String rutaImagen, String rol, String estado) {
        super();
        this.id = id;
        setNombre(nombre);
        setTelefono(telefono);
        setEmail(email);
        this.direccion = direccion;
        setUsuario(usuario);
        setContrasena(claveAcceso);
        setRutaImagen(rutaImagen);
        this.rolPersonalizado = rol;
        this.estado = estado;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getClaveAcceso() {
        return getContrasena();
    }

    public void setClaveAcceso(String claveAcceso) {
        setContrasena(claveAcceso);
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    @Override
    public String getRol() {
        return (rolPersonalizado != null && !rolPersonalizado.isEmpty()) ? rolPersonalizado : "ADMINISTRADOR";
    }

    public void setRol(String rol) {
        this.rolPersonalizado = rol;
    }
}