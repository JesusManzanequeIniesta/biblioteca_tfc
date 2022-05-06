/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jmi.bibliotecatfc.dao;

import com.jmi.bibliotecatfc.dao.exceptions.IllegalOrphanException;
import com.jmi.bibliotecatfc.dao.exceptions.NonexistentEntityException;
import com.jmi.bibliotecatfc.dao.exceptions.PreexistingEntityException;
import com.jmi.bibliotecatfc.entities.Ejemplar;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.jmi.bibliotecatfc.entities.Estado;
import com.jmi.bibliotecatfc.entities.Libro;
import com.jmi.bibliotecatfc.entities.Prestamo;
import java.util.ArrayList;
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
public class DaoEjemplar implements Serializable {

    @PersistenceContext(unitName = "bibliotecatfc")
    private EntityManager em;

    @Transactional
    public void create(Ejemplar ejemplar) throws PreexistingEntityException, Exception {
        if (ejemplar.getPrestamoList() == null) {
            ejemplar.setPrestamoList(new ArrayList<Prestamo>());
        }
        try {
            Estado estadoId = ejemplar.getEstadoId();
            if (estadoId != null) {
                estadoId = em.getReference(estadoId.getClass(), estadoId.getId());
                ejemplar.setEstadoId(estadoId);
            }
            Libro libroId = ejemplar.getLibroId();
            if (libroId != null) {
                libroId = em.getReference(libroId.getClass(), libroId.getId());
                ejemplar.setLibroId(libroId);
            }
            List<Prestamo> attachedPrestamoList = new ArrayList<Prestamo>();
            for (Prestamo prestamoListPrestamoToAttach : ejemplar.getPrestamoList()) {
                prestamoListPrestamoToAttach = em.getReference(prestamoListPrestamoToAttach.getClass(), prestamoListPrestamoToAttach.getId());
                attachedPrestamoList.add(prestamoListPrestamoToAttach);
            }
            ejemplar.setPrestamoList(attachedPrestamoList);
            em.persist(ejemplar);
            if (estadoId != null) {
                estadoId.getEjemplarList().add(ejemplar);
                estadoId = em.merge(estadoId);
            }
            if (libroId != null) {
                libroId.getEjemplarList().add(ejemplar);
                libroId = em.merge(libroId);
            }
            for (Prestamo prestamoListPrestamo : ejemplar.getPrestamoList()) {
                Ejemplar oldEjemplarIdOfPrestamoListPrestamo = prestamoListPrestamo.getEjemplarId();
                prestamoListPrestamo.setEjemplarId(ejemplar);
                prestamoListPrestamo = em.merge(prestamoListPrestamo);
                if (oldEjemplarIdOfPrestamoListPrestamo != null) {
                    oldEjemplarIdOfPrestamoListPrestamo.getPrestamoList().remove(prestamoListPrestamo);
                    oldEjemplarIdOfPrestamoListPrestamo = em.merge(oldEjemplarIdOfPrestamoListPrestamo);
                }
            }
        } catch (Exception ex) {
            if (findEjemplar(ejemplar.getId()) != null) {
                throw new PreexistingEntityException("Ejemplar " + ejemplar + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    @Transactional
    public void edit(Ejemplar ejemplar) throws IllegalOrphanException, NonexistentEntityException, Exception {
        try {
            Ejemplar persistentEjemplar = em.find(Ejemplar.class, ejemplar.getId());
            Estado estadoIdOld = persistentEjemplar.getEstadoId();
            Estado estadoIdNew = ejemplar.getEstadoId();
            Libro libroIdOld = persistentEjemplar.getLibroId();
            Libro libroIdNew = ejemplar.getLibroId();
            List<Prestamo> prestamoListOld = persistentEjemplar.getPrestamoList();
            List<Prestamo> prestamoListNew = ejemplar.getPrestamoList();
            List<String> illegalOrphanMessages = null;
            for (Prestamo prestamoListOldPrestamo : prestamoListOld) {
                if (!prestamoListNew.contains(prestamoListOldPrestamo)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Prestamo " + prestamoListOldPrestamo + " since its ejemplarId field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (estadoIdNew != null) {
                estadoIdNew = em.getReference(estadoIdNew.getClass(), estadoIdNew.getId());
                ejemplar.setEstadoId(estadoIdNew);
            }
            if (libroIdNew != null) {
                libroIdNew = em.getReference(libroIdNew.getClass(), libroIdNew.getId());
                ejemplar.setLibroId(libroIdNew);
            }
            List<Prestamo> attachedPrestamoListNew = new ArrayList<Prestamo>();
            for (Prestamo prestamoListNewPrestamoToAttach : prestamoListNew) {
                prestamoListNewPrestamoToAttach = em.getReference(prestamoListNewPrestamoToAttach.getClass(), prestamoListNewPrestamoToAttach.getId());
                attachedPrestamoListNew.add(prestamoListNewPrestamoToAttach);
            }
            prestamoListNew = attachedPrestamoListNew;
            ejemplar.setPrestamoList(prestamoListNew);
            ejemplar = em.merge(ejemplar);
            if (estadoIdOld != null && !estadoIdOld.equals(estadoIdNew)) {
                estadoIdOld.getEjemplarList().remove(ejemplar);
                estadoIdOld = em.merge(estadoIdOld);
            }
            if (estadoIdNew != null && !estadoIdNew.equals(estadoIdOld)) {
                estadoIdNew.getEjemplarList().add(ejemplar);
                estadoIdNew = em.merge(estadoIdNew);
            }
            if (libroIdOld != null && !libroIdOld.equals(libroIdNew)) {
                libroIdOld.getEjemplarList().remove(ejemplar);
                libroIdOld = em.merge(libroIdOld);
            }
            if (libroIdNew != null && !libroIdNew.equals(libroIdOld)) {
                libroIdNew.getEjemplarList().add(ejemplar);
                libroIdNew = em.merge(libroIdNew);
            }
            for (Prestamo prestamoListNewPrestamo : prestamoListNew) {
                if (!prestamoListOld.contains(prestamoListNewPrestamo)) {
                    Ejemplar oldEjemplarIdOfPrestamoListNewPrestamo = prestamoListNewPrestamo.getEjemplarId();
                    prestamoListNewPrestamo.setEjemplarId(ejemplar);
                    prestamoListNewPrestamo = em.merge(prestamoListNewPrestamo);
                    if (oldEjemplarIdOfPrestamoListNewPrestamo != null && !oldEjemplarIdOfPrestamoListNewPrestamo.equals(ejemplar)) {
                        oldEjemplarIdOfPrestamoListNewPrestamo.getPrestamoList().remove(prestamoListNewPrestamo);
                        oldEjemplarIdOfPrestamoListNewPrestamo = em.merge(oldEjemplarIdOfPrestamoListNewPrestamo);
                    }
                }
            }
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = ejemplar.getId();
                if (findEjemplar(id) == null) {
                    throw new NonexistentEntityException("The ejemplar with id " + id + " no longer exists.");
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
    public void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException {
        try {
            Ejemplar ejemplar;
            try {
                ejemplar = em.getReference(Ejemplar.class, id);
                ejemplar.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The ejemplar with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Prestamo> prestamoListOrphanCheck = ejemplar.getPrestamoList();
            for (Prestamo prestamoListOrphanCheckPrestamo : prestamoListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Ejemplar (" + ejemplar + ") cannot be destroyed since the Prestamo " + prestamoListOrphanCheckPrestamo + " in its prestamoList field has a non-nullable ejemplarId field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Estado estadoId = ejemplar.getEstadoId();
            if (estadoId != null) {
                estadoId.getEjemplarList().remove(ejemplar);
                estadoId = em.merge(estadoId);
            }
            Libro libroId = ejemplar.getLibroId();
            if (libroId != null) {
                libroId.getEjemplarList().remove(ejemplar);
                libroId = em.merge(libroId);
            }
            em.remove(ejemplar);
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Ejemplar> findEjemplarEntities() {
        return findEjemplarEntities(true, -1, -1);
    }

    public List<Ejemplar> findEjemplarEntities(int maxResults, int firstResult) {
        return findEjemplarEntities(false, maxResults, firstResult);
    }

    private List<Ejemplar> findEjemplarEntities(boolean all, int maxResults, int firstResult) {
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Ejemplar.class));
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

    public Ejemplar findEjemplar(Integer id) {
        try {
            return em.find(Ejemplar.class, id);
        } finally {
            em.close();
        }
    }

    public int getEjemplarCount() {
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Ejemplar> rt = cq.from(Ejemplar.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
