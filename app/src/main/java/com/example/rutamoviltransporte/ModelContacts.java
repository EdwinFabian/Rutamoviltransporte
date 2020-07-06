package com.example.rutamoviltransporte;

public class ModelContacts {

    private String Email;
    private String Nombre;
    private String Photo;

    public ModelContacts() {
    }

    public ModelContacts(String email, String nombre, String photo) {
        Email = email;
        Nombre = nombre;
        Photo = photo;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String nombre) {
        Nombre = nombre;
    }

    public String getPhoto() {
        return Photo;
    }

    public void setPhoto(String photo) {
        Photo = photo;
    }
}
