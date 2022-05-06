/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package com.jmi.bibliotecatfc.controller;

import com.jmi.bibliotecatfc.dao.DaoAutor;
import com.jmi.bibliotecatfc.entities.Autor;
import java.util.List;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

@Named
@RequestScoped
public class BeanAutor {

    
    private String nombre;
    private List<Autor> autores;
    private String confirmaroperacion;
    private String error;

   
    @Inject
    DaoAutor daoautor;
    
    public BeanAutor() {
    }

    public List<Autor> getAutores() {
        return daoautor.listAllAuthors();
    }

    public void setAutores(List<Autor> autores) {
        this.autores = autores;
    }
    
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getConfirmaroperacion() {
        return confirmaroperacion;
    }

    public void setConfirmaroperacion(String confirmaroperacion) {
        this.confirmaroperacion = confirmaroperacion;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
    
    public void inserta(){
        try{
            daoautor = new DaoAutor();
            Autor autor = new Autor();
            
            autor.setNombre(nombre);
            daoautor.create(autor);
            confirmaroperacion = "Autor creado "+autor.getNombre()+". Con id "+autor.getId();
            
            
        } catch (Exception ex){
            error = ex.getMessage();
        }
    }


}
