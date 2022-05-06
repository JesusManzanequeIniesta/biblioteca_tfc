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
import com.jmi.bibliotecatfc.entities.Ejemplar;
import com.jmi.bibliotecatfc.entities.Prestamo;
import com.jmi.bibliotecatfc.entities.Socio;
import com.jmi.bibliotecatfc.entities.Tiposancion;
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
public class DaoPrestamo implements Serializable {

    @PersistenceContext(unitName = "bibliotecatfc")
    private EntityManager em;

    @Transactional
    public void create(Prestamo prestamo) throws PreexistingEntityException, Exception {
        try {
            Ejemplar ejemplarId = prestamo.getEjemplarId();
            if (ejemplarId != null) {
                ejemplarId = em.getReference(ejemplarId.getClass(), ejemplarId.getId());
                prestamo.setEjemplarId(ejemplarId);
            }
            Socio socioId = prestamo.getSocioId();
            if (socioId != null) {
                socioId = em.getReference(socioId.getClass(), socioId.getId());
                prestamo.setSocioId(socioId);
            }
            Tiposancion tiposancionId = prestamo.getTiposancionId();
            if (tiposancionId != null) {
                tiposancionId = em.getReference(tiposancionId.getClass(), tiposancionId.getId());
                prestamo.setTiposancionId(tiposancionId);
            }
            em.persist(prestamo);
            if (ejemplarId != null) {
                ejemplarId.getPrestamoList().add(prestamo);
                ejemplarId = em.merge(ejemplarId);
            }
            if (socioId != null) {
                socioId.getPrestamoList().add(prestamo);
                socioId = em.merge(socioId);
            }
            if (tiposancionId != null) {
                tiposancionId.getPrestamoList().add(prestamo);
                tiposancionId = em.merge(tiposancionId);
            }
        } catch (Exception ex) {
            if (findPrestamo(prestamo.getId()) != null) {
                throw new PreexistingEntityException("Prestamo " + prestamo + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    @Transactional
    public void edit(Prestamo prestamo) throws NonexistentEntityException, Exception {
        try {
            Prestamo persistentPrestamo = em.find(Prestamo.class, prestamo.getId());
            Ejemplar ejemplarIdOld = persistentPrestamo.getEjemplarId();
            Ejemplar ejemplarIdNew = prestamo.getEjemplarId();
            Socio socioIdOld = persistentPrestamo.getSocioId();
            Socio socioIdNew = prestamo.getSocioId();
            Tiposancion tiposancionIdOld = persistentPrestamo.getTiposancionId();
            Tiposancion tiposancionIdNew = prestamo.getTiposancionId();
            if (ejemplarIdNew != null) {
                ejemplarIdNew = em.getReference(ejemplarIdNew.getClass(), ejemplarIdNew.getId());
                prestamo.setEjemplarId(ejemplarIdNew);
            }
            if (socioIdNew != null) {
                socioIdNew = em.getReference(socioIdNew.getClass(), socioIdNew.getId());
                prestamo.setSocioId(socioIdNew);
            }
            if (tiposancionIdNew != null) {
                tiposancionIdNew = em.getReference(tiposancionIdNew.getClass(), tiposancionIdNew.getId());
                prestamo.setTiposancionId(tiposancionIdNew);
            }
            prestamo = em.merge(prestamo);
            if (ejemplarIdOld != null && !ejemplarIdOld.equals(ejemplarIdNew)) {
                ejemplarIdOld.getPrestamoList().remove(prestamo);
                ejemplarIdOld = em.merge(ejemplarIdOld);
            }
            if (ejemplarIdNew != null && !ejemplarIdNew.equals(ejemplarIdOld)) {
                ejemplarIdNew.getPrestamoList().add(prestamo);
                ejemplarIdNew = em.merge(ejemplarIdNew);
            }
            if (socioIdOld != null && !socioIdOld.equals(socioIdNew)) {
                socioIdOld.getPrestamoList().remove(prestamo);
                socioIdOld = em.merge(socioIdOld);
            }
            if (socioIdNew != null && !socioIdNew.equals(socioIdOld)) {
                socioIdNew.getPrestamoList().add(prestamo);
                socioIdNew = em.merge(socioIdNew);
            }
            if (tiposancionIdOld != null && !tiposancionIdOld.equals(tiposancionIdNew)) {
                tiposancionIdOld.getPrestamoList().remove(prestamo);
                tiposancionIdOld = em.merge(tiposancionIdOld);
            }
            if (tiposancionIdNew != null && !tiposancionIdNew.equals(tiposancionIdOld)) {
                tiposancionIdNew.getPrestamoList().add(prestamo);
                tiposancionIdNew = em.merge(tiposancionIdNew);
            }
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = prestamo.getId();
                if (findPrestamo(id) == null) {
                    throw new NonexistentEntityException("The prestamo with id " + id + " no longer exists.");
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
            Prestamo prestamo;
            try {
                prestamo = em.getReference(Prestamo.class, id);
                prestamo.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The prestamo with id " + id + " no longer exists.", enfe);
            }
            Ejemplar ejemplarId = prestamo.getEjemplarId();
            if (ejemplarId != null) {
                ejemplarId.getPrestamoList().remove(prestamo);
                ejemplarId = em.merge(ejemplarId);
            }
            Socio socioId = prestamo.getSocioId();
            if (socioId != null) {
                socioId.getPrestamoList().remove(prestamo);
                socioId = em.merge(socioId);
            }
            Tiposancion tiposancionId = prestamo.getTiposancionId();
            if (tiposancionId != null) {
                tiposancionId.getPrestamoList().remove(prestamo);
                tiposancionId = em.merge(tiposancionId);
            }
            em.remove(prestamo);
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Prestamo> findPrestamoEntities() {
        return findPrestamoEntities(true, -1, -1);
    }

    public List<Prestamo> findPrestamoEntities(int maxResults, int firstResult) {
        return findPrestamoEntities(false, maxResults, firstResult);
    }

    private List<Prestamo> findPrestamoEntities(boolean all, int maxResults, int firstResult) {
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Prestamo.class));
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

    public Prestamo findPrestamo(Integer id) {
        try {
            return em.find(Prestamo.class, id);
        } finally {
            em.close();
        }
    }

    public int getPrestamoCount() {
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Prestamo> rt = cq.from(Prestamo.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
