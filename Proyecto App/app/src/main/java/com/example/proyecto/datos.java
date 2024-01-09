package com.example.proyecto;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.List;

@IgnoreExtraProperties
public class datos {
    private String nombre;
    private String email;
    private String direccion;
    private String fechaini;
    private String fechafin;
    private String descripcion;
    private String rutaImagen;
    private String opcion;
    private List<String> fechas;
    private String telefono;
    private static datos instance;

    public datos() {
        fechas = new ArrayList<>();
    }

    public datos(String nombre, String email, String direccion, String fechaini, String fechafin, String descripcion) {
        this.nombre = nombre;
        this.email = email;
        this.direccion = direccion;
        this.fechaini = fechaini;
        this.fechafin = fechafin;
        this.descripcion = descripcion;
        fechas = new ArrayList<>();
    }
    public datos(String nombre, String email, String telef, String direccion, String fechaini, String fechafin, String descripcion) {
        this.nombre = nombre;
        this.email = email;
        this.telefono = telef;
        this.direccion = direccion;
        this.fechaini = fechaini;
        this.fechafin = fechafin;
        this.descripcion = descripcion;
        fechas = new ArrayList<>();
    }
    public static synchronized datos getInstance() {
        if (instance == null) {
            instance = new datos();
        }
        return instance;
    }
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getFechaini() {
        return fechaini;
    }

    public void setFechaini(String fechaini) {
        this.fechaini = fechaini;
    }

    public String getFechafin() {
        return fechafin;
    }

    public void setFechafin(String fechafin) {
        this.fechafin = fechafin;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getRutaImagen() {
        return rutaImagen;
    }

    public void setRutaImagen(String rutaImagen) {
        this.rutaImagen = rutaImagen;
    }

    public String getOpcion() {
        return opcion;
    }

    public void setOpcion(String opcion) {
        this.opcion = opcion;
    }


    public List<String> getFechas() {
        return fechas;
    }

    public void addFecha(String fecha) {
        fechas.add(fecha);
    }
}
