/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jmi.bibliotecatfc.entities;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author yisus
 */
@Entity
@Table(name = "EJEMPLAR")
@NamedQueries({
    @NamedQuery(name = "Ejemplar.findAll", query = "SELECT e FROM Ejemplar e"),
    @NamedQuery(name = "Ejemplar.findById", query = "SELECT e FROM Ejemplar e WHERE e.id = :id"),
    @NamedQuery(name = "Ejemplar.findByCodbarras", query = "SELECT e FROM Ejemplar e WHERE e.codbarras = :codbarras"),
    @NamedQuery(name = "Ejemplar.findByFechaalta", query = "SELECT e FROM Ejemplar e WHERE e.fechaalta = :fechaalta"),
    @NamedQuery(name = "Ejemplar.findByCalculadoPrestado", query = "SELECT e FROM Ejemplar e WHERE e.calculadoPrestado = :calculadoPrestado"),
    @NamedQuery(name = "Ejemplar.findByCalculadoPrestable", query = "SELECT e FROM Ejemplar e WHERE e.calculadoPrestable = :calculadoPrestable")})
public class Ejemplar implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "ID")
    private Integer id;
    @Size(max = 7)
    @Column(name = "CODBARRAS")
    private String codbarras;
    @Basic(optional = false)
    @NotNull
    @Column(name = "FECHAALTA")
    private LocalDate fechaalta;
    @Column(name = "CALCULADO_PRESTADO")
    private Boolean calculadoPrestado;
    @Column(name = "CALCULADO_PRESTABLE")
    private Boolean calculadoPrestable;
    @JoinColumn(name = "ESTADO_ID", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private Estado estadoId;
    @JoinColumn(name = "LIBRO_ID", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private Libro libroId;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "ejemplarId")
    private List<Prestamo> prestamoList;

    public Ejemplar() {
    }

    public Ejemplar(Integer id) {
        this.id = id;
    }

    public Ejemplar(Integer id, LocalDate fechaalta) {
        this.id = id;
        this.fechaalta = fechaalta;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCodbarras() {
        return codbarras;
    }

    public void setCodbarras(String codbarras) {
        this.codbarras = codbarras;
    }

    public LocalDate getFechaalta() {
        return fechaalta;
    }

    public void setFechaalta(LocalDate fechaalta) {
        this.fechaalta = fechaalta;
    }

    public Boolean getCalculadoPrestado() {
        return calculadoPrestado;
    }

    public void setCalculadoPrestado(Boolean calculadoPrestado) {
        this.calculadoPrestado = calculadoPrestado;
    }

    public Boolean getCalculadoPrestable() {
        return calculadoPrestable;
    }

    public void setCalculadoPrestable(Boolean calculadoPrestable) {
        this.calculadoPrestable = calculadoPrestable;
    }

    public Estado getEstadoId() {
        return estadoId;
    }

    public void setEstadoId(Estado estadoId) {
        this.estadoId = estadoId;
    }

    public Libro getLibroId() {
        return libroId;
    }

    public void setLibroId(Libro libroId) {
        this.libroId = libroId;
    }

    public List<Prestamo> getPrestamoList() {
        return prestamoList;
    }

    public void setPrestamoList(List<Prestamo> prestamoList) {
        this.prestamoList = prestamoList;
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
        if (!(object instanceof Ejemplar)) {
            return false;
        }
        Ejemplar other = (Ejemplar) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.jmi.bibliotecatfc.entities.Ejemplar[ id=" + id + " ]";
    }
    
}
