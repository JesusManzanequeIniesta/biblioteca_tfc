/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package com.jmi.bibliotecatfc.controller;

import com.jmi.bibliotecatfc.dao.DaoSocio;
import com.jmi.bibliotecatfc.entities.Autor;
import com.jmi.bibliotecatfc.entities.Socio;
import com.jmi.bibliotecatfc.exceptions.BibliotecaException;
import java.util.List;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import com.jmi.bibliotecatfc.tools.Tools;

@Named
@RequestScoped
public class BeanSocio {

    private String nombre;
    private String email;
    private String telefono;

    @Inject
    DaoSocio daosocio;

    public BeanSocio() {
    }

    public void registrarse() throws BibliotecaException, Exception {
        email = email.toLowerCase();
        Socio socio = daosocio.findByEmail(email);
        // Si el socio ya existe lanzamos un error
        // no podemos tener dos socios con mismo email
        try{
            if (socio != null) {
                throw new BibliotecaException("Email de socio usado", 3);
            }
            socio = new Socio();
            socio.setNombre(nombre);
            socio.setEmail(email);
            socio.setTelefono(telefono);
            daosocio.create(socio);
        } catch (Exception ex){
            throw ex;
        }
//        // Creo un token de validación de email y telefono
//        Token token = new Token();
//        token.setEmail(email);
//        token.setTelefono(telefono);
//        // Genero un token aleatorio
//        token.setValue(Tools.generaToken());
//        daotoken = new DaoToken();
//        daotoken.inserta(token);
//        // Envío correo de validación
//        String asunto = "Validación en correo registro aplicación biblioteca";
//        String cuerpo = Tools.creaCuerpoCorreo(token.getValue());
//        Tools.enviarConGMail(email, asunto, cuerpo);
//        dao.close();
    }

}
