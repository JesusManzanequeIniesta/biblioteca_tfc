/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jmi.bibliotecatfc.entities;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author yisus
 */
@Entity
@Table(name = "GENERATOR_TABLE")
@NamedQueries({
    @NamedQuery(name = "GeneratorTable.findAll", query = "SELECT g FROM GeneratorTable g"),
    @NamedQuery(name = "GeneratorTable.findByKey", query = "SELECT g FROM GeneratorTable g WHERE g.key = :key"),
    @NamedQuery(name = "GeneratorTable.findByNext", query = "SELECT g FROM GeneratorTable g WHERE g.next = :next")})
public class GeneratorTable implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "KEY")
    private String key;
    @Column(name = "NEXT")
    private Integer next;

    public GeneratorTable() {
    }

    public GeneratorTable(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Integer getNext() {
        return next;
    }

    public void setNext(Integer next) {
        this.next = next;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (key != null ? key.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof GeneratorTable)) {
            return false;
        }
        GeneratorTable other = (GeneratorTable) object;
        if ((this.key == null && other.key != null) || (this.key != null && !this.key.equals(other.key))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.jmi.bibliotecatfc.entities.GeneratorTable[ key=" + key + " ]";
    }
    
}
