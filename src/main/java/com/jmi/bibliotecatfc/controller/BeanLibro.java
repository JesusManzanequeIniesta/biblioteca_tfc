/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package com.jmi.bibliotecatfc.controller;

import com.jmi.bibliotecatfc.dao.DaoAutor;
import com.jmi.bibliotecatfc.dao.DaoEjemplar;
import com.jmi.bibliotecatfc.dao.DaoLibro;
import com.jmi.bibliotecatfc.entities.Autor;
import com.jmi.bibliotecatfc.entities.Ejemplar;
import com.jmi.bibliotecatfc.entities.Libro;
import com.jmi.bibliotecatfc.tools.GoogleBooks;
import com.jmi.bibliotecatfc.tools.Tools;
import java.sql.SQLException;
import java.util.List;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import org.json.*;

@Named
@RequestScoped
public class BeanLibro {

    private Libro libro;
    private String error;
    private Ejemplar ejemplar;
    private Autor autor;

    public Libro getLibro() {
        return libro;
    }

    public void setLibro(Libro libro) {
        this.libro = libro;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public Ejemplar getEjemplar() {
        return ejemplar;
    }

    public void setEjemplar(Ejemplar ejemplar) {
        this.ejemplar = ejemplar;
    }

    public Autor getAutor() {
        return autor;
    }

    public void setAutor(Autor autor) {
        this.autor = autor;
    }
    

    @Inject
    DaoLibro daolibro;
    @Inject
    DaoAutor daoautor;
    @Inject
    DaoEjemplar daoejemplar;

    public BeanLibro() {
    }

    public void registroAutomáticoLibro(String isbn) throws Exception {
        try{
        libro = daolibro.findByISBN(isbn);
        if (libro == null) {
            // No ha sido dado de alta anteriormente, lo busco en Google
            JSONObject jsLibro = GoogleBooks.buscaISBN(isbn);
            if (jsLibro == null) {
                error = "Libro no localizado en BD Google";
                
            } else {
                // Leo datos JSON para proceder al alta
                libro = new Libro();
                libro.setIsbn(isbn);
                libro.setTitulo(jsLibro.getString("titulo"));
                // Ahora mismo paso del tejuelo para no hacer ésto mas largo
                //libro.Tejuelo();
                daolibro.create(libro);
                daoautor = new DaoAutor();
                JSONArray jsAutores = jsLibro.getJSONArray("autores");
                for (int i = 0; i < jsAutores.length(); i++) {
                    String nombre = jsAutores.getString(i);
                    // Nombre de autor vacio
                    // >>> Quizás mejor que no aparezca en json
                    if (!nombre.equals("")) {
                        autor = daoautor.findByNombre(nombre);
                        // No he encontrado el autor asi que lo doy de alta
                        if (autor == null) {
                            autor = new Autor();
                            autor.setNombre(nombre);
                            daoautor.create(autor);
                        }
                        // Tengo que decir que ha escrito ese libro
                        libro.getAutorList().add(autor);
                        autor.getLibroList().add(libro);
                    }
                } // fin for de autores
            } // fin jsLibro
        } // fin Libro
        ejemplar = new Ejemplar();
        daoejemplar = new DaoEjemplar();
        daoejemplar.create(ejemplar);
        libro.getEjemplarList().add(ejemplar);
//        // Paso como atributos el libro y el ejemplar
//        request.setAttribute("libro", libro);
//        request.setAttribute("ejemplar", ejemplar);
//        String ruta = getServletConfig().getServletContext().getRealPath("/generados");
        // Genero el pdf en generados
//        String file = Tools.creaFicheroPDF(ruta + "\\", "Ejemplar", ejemplar.getCodbarras());
        //request.setAttribute("error",file);
//        request.getRequestDispatcher("/registroautomaticolibro.jsp").forward(request, response);
    }
        catch (SQLException e) {throw e;}
        catch (Exception e) {throw e;}
    }
}
