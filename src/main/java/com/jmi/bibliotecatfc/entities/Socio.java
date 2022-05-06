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
import javax.persistence.Lob;
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
@Table(name = "SOCIO")
@NamedQueries({
    @NamedQuery(name = "Socio.findAll", query = "SELECT s FROM Socio s"),
    @NamedQuery(name = "Socio.findById", query = "SELECT s FROM Socio s WHERE s.id = :id"),
    @NamedQuery(name = "Socio.findByCodbarras", query = "SELECT s FROM Socio s WHERE s.codbarras = :codbarras"),
    @NamedQuery(name = "Socio.findByNombre", query = "SELECT s FROM Socio s WHERE s.nombre = :nombre"),
    @NamedQuery(name = "Socio.findByTelefono", query = "SELECT s FROM Socio s WHERE s.telefono = :telefono"),
    @NamedQuery(name = "Socio.findByEmail", query = "SELECT s FROM Socio s WHERE s.email = :email"),
    @NamedQuery(name = "Socio.findByFechaalta", query = "SELECT s FROM Socio s WHERE s.fechaalta = :fechaalta"),
    @NamedQuery(name = "Socio.findByFechabaja", query = "SELECT s FROM Socio s WHERE s.fechabaja = :fechabaja"),
    @NamedQuery(name = "Socio.findByCalculadoSancionado", query = "SELECT s FROM Socio s WHERE s.calculadoSancionado = :calculadoSancionado"),
    @NamedQuery(name = "Socio.findByCalculadoFechafinsancion", query = "SELECT s FROM Socio s WHERE s.calculadoFechafinsancion = :calculadoFechafinsancion")})
public class Socio implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "ID")
    private Integer id;
    @Size(max = 7)
    @Column(name = "CODBARRAS")
    private String codbarras;
    @Lob
    @Column(name = "FOTO")
    private Serializable foto;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "NOMBRE")
    private String nombre;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 13)
    @Column(name = "TELEFONO")
    private String telefono;
    // @Pattern(regexp="[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?", message="Invalid email")//if the field contains email address consider using this annotation to enforce field validation
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "EMAIL")
    private String email;
    @Basic(optional = false)
    @NotNull
    @Column(name = "FECHAALTA")
    private LocalDate fechaalta;
    @Column(name = "FECHABAJA")
    private LocalDate fechabaja;
    @Column(name = "CALCULADO_SANCIONADO")
    private Boolean calculadoSancionado;
    @Column(name = "CALCULADO_FECHAFINSANCION")
    private LocalDate calculadoFechafinsancion;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "socioId")
    private List<Peticion> peticionList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "socioId")
    private List<Prestamo> prestamoList;

    public Socio() {
    }

    public Socio(Integer id) {
        this.id = id;
    }

    public Socio(Integer id, String nombre, String telefono, String email, LocalDate fechaalta) {
        this.id = id;
        this.nombre = nombre;
        this.telefono = telefono;
        this.email = email;
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

    public Serializable getFoto() {
        return foto;
    }

    public void setFoto(Serializable foto) {
        this.foto = foto;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getFechaalta() {
        return fechaalta;
    }

    public void setFechaalta(LocalDate fechaalta) {
        this.fechaalta = fechaalta;
    }

    public LocalDate getFechabaja() {
        return fechabaja;
    }

    public void setFechabaja(LocalDate fechabaja) {
        this.fechabaja = fechabaja;
    }

    public Boolean getCalculadoSancionado() {
        return calculadoSancionado;
    }

    public void setCalculadoSancionado(Boolean calculadoSancionado) {
        this.calculadoSancionado = calculadoSancionado;
    }

    public LocalDate getCalculadoFechafinsancion() {
        return calculadoFechafinsancion;
    }

    public void setCalculadoFechafinsancion(LocalDate calculadoFechafinsancion) {
        this.calculadoFechafinsancion = calculadoFechafinsancion;
    }

    public List<Peticion> getPeticionList() {
        return peticionList;
    }

    public void setPeticionList(List<Peticion> peticionList) {
        this.peticionList = peticionList;
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
        if (!(object instanceof Socio)) {
            return false;
        }
        Socio other = (Socio) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.jmi.bibliotecatfc.entities.Socio[ id=" + id + " ]";
    }
    
}
