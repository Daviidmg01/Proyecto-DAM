/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.proyectofinal.model;

import java.util.Date;

/**
 *
 * @author dav85
 */
public class Coche {
    
    private int idCoche;
    private String matricula;
    private String marca;
    private String modelo;
    private int idCliente;
    private String imagen;

    public Coche() {
    }

    public Coche(int idCoche, String matricula, String marca, String modelo, int idCliente, String imagen) {
        this.idCoche = idCoche;
        this.matricula = matricula;
        this.marca = marca;
        this.modelo = modelo;
        this.idCliente = idCliente;
        this.imagen = imagen;
    }

    public int getIdCoche() {
        return idCoche;
    }

    public void setIdCoche(int idCoche) {
        this.idCoche = idCoche;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }
    
    
}
