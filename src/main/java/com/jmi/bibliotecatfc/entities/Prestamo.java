/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jmi.bibliotecatfc.entities;

import java.io.Serializable;
import java.time.LocalDate;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

/**
 *
 * @author yisus
 */
@Entity
@Table(name = "PRESTAMO")
@NamedQueries({
    @NamedQuery(name = "Prestamo.findAll", query = "SELECT p FROM Prestamo p"),
    @NamedQuery(name = "Prestamo.findById", query = "SELECT p FROM Prestamo p WHERE p.id = :id"),
    @NamedQuery(name = "Prestamo.findByFechainicio", query = "SELECT p FROM Prestamo p WHERE p.fechainicio = :fechainicio"),
    @NamedQuery(name = "Prestamo.findByFechalimite", query = "SELECT p FROM Prestamo p WHERE p.fechalimite = :fechalimite"),
    @NamedQuery(name = "Prestamo.findByFechadevolucion", query = "SELECT p FROM Prestamo p WHERE p.fechadevolucion = :fechadevolucion"),
    @NamedQuery(name = "Prestamo.findByCalculadoActivo", query = "SELECT p FROM Prestamo p WHERE p.calculadoActivo = :calculadoActivo")})
public class Prestamo implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "ID")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Column(name = "FECHAINICIO")
    private LocalDate fechainicio;
    @Basic(optional = false)
    @NotNull
    @Column(name = "FECHALIMITE")
    private LocalDate fechalimite;
    @Column(name = "FECHADEVOLUCION")
    private LocalDate fechadevolucion;
    @Column(name = "CALCULADO_ACTIVO")
    private Boolean calculadoActivo;
    @JoinColumn(name = "EJEMPLAR_ID", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private Ejemplar ejemplarId;
    @JoinColumn(name = "SOCIO_ID", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private Socio socioId;
    @JoinColumn(name = "TIPOSANCION_ID", referencedColumnName = "ID")
    @ManyToOne
    private Tiposancion tiposancionId;

    public Prestamo() {
    }

    public Prestamo(Integer id) {
        this.id = id;
    }

    public Prestamo(Integer id, LocalDate fechainicio, LocalDate fechalimite) {
        this.id = id;
        this.fechainicio = fechainicio;
        this.fechalimite = fechalimite;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDate getFechainicio() {
        return fechainicio;
    }

    public void setFechainicio(LocalDate fechainicio) {
        this.fechainicio = fechainicio;
    }

    public LocalDate getFechalimite() {
        return fechalimite;
    }

    public void setFechalimite(LocalDate fechalimite) {
        this.fechalimite = fechalimite;
    }

    public LocalDate getFechadevolucion() {
        return fechadevolucion;
    }

    public void setFechadevolucion(LocalDate fechadevolucion) {
        this.fechadevolucion = fechadevolucion;
    }

    public Boolean getCalculadoActivo() {
        return calculadoActivo;
    }

    public void setCalculadoActivo(Boolean calculadoActivo) {
        this.calculadoActivo = calculadoActivo;
    }

    public Ejemplar getEjemplarId() {
        return ejemplarId;
    }

    public void setEjemplarId(Ejemplar ejemplarId) {
        this.ejemplarId = ejemplarId;
    }

    public Socio getSocioId() {
        return socioId;
    }

    public void setSocioId(Socio socioId) {
        this.socioId = socioId;
    }

    public Tiposancion getTiposancionId() {
        return tiposancionId;
    }

    public void setTiposancionId(Tiposancion tiposancionId) {
        this.tiposancionId = tiposancionId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Prestamo)) {
            return false;
        }
        Prestamo other = (Prestamo) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.jmi.bibliotecatfc.entities.Prestamo[ id=" + id + " ]";
    }
    
}
