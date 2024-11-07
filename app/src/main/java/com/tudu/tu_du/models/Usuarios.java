package com.tudu.tu_du.models;

public class Usuarios {
    private String id;
    private String email;
    private String Nombre;
    private String telefono;
    private String Region;
    private String Comuna;
    private long timestamp;
    private long ultimaconexion;
    private boolean online;
    private String imagenperfil;
    private String imagencover;





    public Usuarios(){

    }


    public Usuarios(String id, String email, String nombre, String telefono, String region, String comuna, long timestamp, long ultimaconexion, boolean online, String imagenperfil, String imagencover) {
        this.id = id;
        this.email = email;
        this.Nombre = nombre;
        this.telefono = telefono;
        this.Region = region;
        this.Comuna = comuna;
        this.timestamp = timestamp;
        this.ultimaconexion = ultimaconexion;
        this.online = online;
        this.imagenperfil = imagenperfil;
        this.imagencover = imagencover;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String nombre) {
        Nombre = nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getRegion() {
        return Region;
    }

    public void setRegion(String region) {
        Region = region;
    }

    public String getComuna() {
        return Comuna;
    }

    public void setComuna(String comuna) {
        Comuna = comuna;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getImagenperfil() {
        return imagenperfil;
    }

    public void setImagenperfil(String imagenperfil) {
        this.imagenperfil = imagenperfil;
    }

    public String getImagencover() {
        return imagencover;
    }

    public void setImagencover(String imagencover) {
        this.imagencover = imagencover;
    }

    public long getUltimaconexion() {
        return ultimaconexion;
    }

    public void setUltimaconexion(long ultimaconexion) {
        this.ultimaconexion = ultimaconexion;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }
}