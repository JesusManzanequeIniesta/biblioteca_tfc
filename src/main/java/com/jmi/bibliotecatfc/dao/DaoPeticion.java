/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jmi.bibliotecatfc.dao;

import com.jmi.bibliotecatfc.dao.exceptions.NonexistentEntityException;
import com.jmi.bibliotecatfc.dao.exceptions.PreexistingEntityException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.jmi.bibliotecatfc.entities.Libro;
import com.jmi.bibliotecatfc.entities.Peticion;
import com.jmi.bibliotecatfc.entities.Socio;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

/**
 *
 * @author yisus
 */
@ApplicationScoped
public class DaoPeticion implements Serializable {

    @PersistenceContext(unitName = "bibliotecatfc")
    private EntityManager em;

    @Transactional
    public void create(Peticion peticion) throws PreexistingEntityException, Exception {
        try {
            em.getTransaction().begin();
            Libro libroId = peticion.getLibroId();
            if (libroId != null) {
                libroId = em.getReference(libroId.getClass(), libroId.getId());
                peticion.setLibroId(libroId);
            }
            Socio socioId = peticion.getSocioId();
            if (socioId != null) {
                socioId = em.getReference(socioId.getClass(), socioId.getId());
                peticion.setSocioId(socioId);
            }
            em.persist(peticion);
            if (libroId != null) {
                libroId.getPeticionList().add(peticion);
                libroId = em.merge(libroId);
            }
            if (socioId != null) {
                socioId.getPeticionList().add(peticion);
                socioId = em.merge(socioId);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findPeticion(peticion.getId()) != null) {
                throw new PreexistingEntityException("Peticion " + peticion + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    @Transactional
    public void edit(Peticion peticion) throws NonexistentEntityException, Exception {
        try {
            Peticion persistentPeticion = em.find(Peticion.class, peticion.getId());
            Libro libroIdOld = persistentPeticion.getLibroId();
            Libro libroIdNew = peticion.getLibroId();
            Socio socioIdOld = persistentPeticion.getSocioId();
            Socio socioIdNew = peticion.getSocioId();
            if (libroIdNew != null) {
                libroIdNew = em.getReference(libroIdNew.getClass(), libroIdNew.getId());
                peticion.setLibroId(libroIdNew);
            }
            if (socioIdNew != null) {
                socioIdNew = em.getReference(socioIdNew.getClass(), socioIdNew.getId());
                peticion.setSocioId(socioIdNew);
            }
            peticion = em.merge(peticion);
            if (libroIdOld != null && !libroIdOld.equals(libroIdNew)) {
                libroIdOld.getPeticionList().remove(peticion);
                libroIdOld = em.merge(libroIdOld);
            }
            if (libroIdNew != null && !libroIdNew.equals(libroIdOld)) {
                libroIdNew.getPeticionList().add(peticion);
                libroIdNew = em.merge(libroIdNew);
            }
            if (socioIdOld != null && !socioIdOld.equals(socioIdNew)) {
                socioIdOld.getPeticionList().remove(peticion);
                socioIdOld = em.merge(socioIdOld);
            }
            if (socioIdNew != null && !socioIdNew.equals(socioIdOld)) {
                socioIdNew.getPeticionList().add(peticion);
                socioIdNew = em.merge(socioIdNew);
            }
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = peticion.getId();
                if (findPeticion(id) == null) {
                    throw new NonexistentEntityException("The peticion with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    @Transactional
    public void destroy(Integer id) throws NonexistentEntityException {
        try {
            Peticion peticion;
            try {
                peticion = em.getReference(Peticion.class, id);
                peticion.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The peticion with id " + id + " no longer exists.", enfe);
            }
            Libro libroId = peticion.getLibroId();
            if (libroId != null) {
                libroId.getPeticionList().remove(peticion);
                libroId = em.merge(libroId);
            }
            Socio socioId = peticion.getSocioId();
            if (socioId != null) {
                socioId.getPeticionList().remove(peticion);
                socioId = em.merge(socioId);
            }
            em.remove(peticion);
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Peticion> findPeticionEntities() {
        return findPeticionEntities(true, -1, -1);
    }

    public List<Peticion> findPeticionEntities(int maxResults, int firstResult) {
        return findPeticionEntities(false, maxResults, firstResult);
    }

    private List<Peticion> findPeticionEntities(boolean all, int maxResults, int firstResult) {
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Peticion.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Peticion findPeticion(Integer id) {
        try {
            return em.find(Peticion.class, id);
        } finally {
            em.close();
        }
    }

    public int getPeticionCount() {
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Peticion> rt = cq.from(Peticion.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
