package com.tudu.tu_du.models;

public class Post {
    private String id;
    private String titulo;
    private String descripcion;
    private String img1;
    private String img2;
    private String categoria;
    private String idUser;
    private String precio;
    private  long timestamp;

    public Post(){

    }

    public Post(String id, String titulo, String descripcion, String img1, String img2, String categoria, String idUser, String precio, long timestamp) {
        this.id = id;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.img1 = img1;
        this.img2 = img2;
        this.categoria = categoria;
        this.idUser = idUser;
        this.precio = precio;
        this.timestamp = timestamp;

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getImg1() {
        return img1;
    }

    public void setImg1(String img1) {
        this.img1 = img1;
    }

    public String getImg2() {
        return img2;
    }

    public void setImg2(String img2) {
        this.img2 = img2;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getPrecio() {
        return precio;
    }

    public void setPrecio(String precio) {
        this.precio = precio;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
