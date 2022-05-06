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
import com.jmi.bibliotecatfc.entities.Prestamo;
import com.jmi.bibliotecatfc.entities.Tiposancion;
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
public class DaoTipoSancion implements Serializable {

    @PersistenceContext(unitName = "bibliotecatfc")
    private EntityManager em;
    
    @Transactional
    public void create(Tiposancion tiposancion) throws PreexistingEntityException, Exception {
        if (tiposancion.getPrestamoList() == null) {
            tiposancion.setPrestamoList(new ArrayList<Prestamo>());
        }
        try {
            List<Prestamo> attachedPrestamoList = new ArrayList<Prestamo>();
            for (Prestamo prestamoListPrestamoToAttach : tiposancion.getPrestamoList()) {
                prestamoListPrestamoToAttach = em.getReference(prestamoListPrestamoToAttach.getClass(), prestamoListPrestamoToAttach.getId());
                attachedPrestamoList.add(prestamoListPrestamoToAttach);
            }
            tiposancion.setPrestamoList(attachedPrestamoList);
            em.persist(tiposancion);
            for (Prestamo prestamoListPrestamo : tiposancion.getPrestamoList()) {
                Tiposancion oldTiposancionIdOfPrestamoListPrestamo = prestamoListPrestamo.getTiposancionId();
                prestamoListPrestamo.setTiposancionId(tiposancion);
                prestamoListPrestamo = em.merge(prestamoListPrestamo);
                if (oldTiposancionIdOfPrestamoListPrestamo != null) {
                    oldTiposancionIdOfPrestamoListPrestamo.getPrestamoList().remove(prestamoListPrestamo);
                    oldTiposancionIdOfPrestamoListPrestamo = em.merge(oldTiposancionIdOfPrestamoListPrestamo);
                }
            }
        } catch (Exception ex) {
            if (findTiposancion(tiposancion.getId()) != null) {
                throw new PreexistingEntityException("Tiposancion " + tiposancion + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    @Transactional
    public void edit(Tiposancion tiposancion) throws NonexistentEntityException, Exception {
        try {
            Tiposancion persistentTiposancion = em.find(Tiposancion.class, tiposancion.getId());
            List<Prestamo> prestamoListOld = persistentTiposancion.getPrestamoList();
            List<Prestamo> prestamoListNew = tiposancion.getPrestamoList();
            List<Prestamo> attachedPrestamoListNew = new ArrayList<Prestamo>();
            for (Prestamo prestamoListNewPrestamoToAttach : prestamoListNew) {
                prestamoListNewPrestamoToAttach = em.getReference(prestamoListNewPrestamoToAttach.getClass(), prestamoListNewPrestamoToAttach.getId());
                attachedPrestamoListNew.add(prestamoListNewPrestamoToAttach);
            }
            prestamoListNew = attachedPrestamoListNew;
            tiposancion.setPrestamoList(prestamoListNew);
            tiposancion = em.merge(tiposancion);
            for (Prestamo prestamoListOldPrestamo : prestamoListOld) {
                if (!prestamoListNew.contains(prestamoListOldPrestamo)) {
                    prestamoListOldPrestamo.setTiposancionId(null);
                    prestamoListOldPrestamo = em.merge(prestamoListOldPrestamo);
                }
            }
            for (Prestamo prestamoListNewPrestamo : prestamoListNew) {
                if (!prestamoListOld.contains(prestamoListNewPrestamo)) {
                    Tiposancion oldTiposancionIdOfPrestamoListNewPrestamo = prestamoListNewPrestamo.getTiposancionId();
                    prestamoListNewPrestamo.setTiposancionId(tiposancion);
                    prestamoListNewPrestamo = em.merge(prestamoListNewPrestamo);
                    if (oldTiposancionIdOfPrestamoListNewPrestamo != null && !oldTiposancionIdOfPrestamoListNewPrestamo.equals(tiposancion)) {
                        oldTiposancionIdOfPrestamoListNewPrestamo.getPrestamoList().remove(prestamoListNewPrestamo);
                        oldTiposancionIdOfPrestamoListNewPrestamo = em.merge(oldTiposancionIdOfPrestamoListNewPrestamo);
                    }
                }
            }
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Short id = tiposancion.getId();
                if (findTiposancion(id) == null) {
                    throw new NonexistentEntityException("The tiposancion with id " + id + " no longer exists.");
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
    public void destroy(Short id) throws NonexistentEntityException {
        try {
            Tiposancion tiposancion;
            try {
                tiposancion = em.getReference(Tiposancion.class, id);
                tiposancion.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The tiposancion with id " + id + " no longer exists.", enfe);
            }
            List<Prestamo> prestamoList = tiposancion.getPrestamoList();
            for (Prestamo prestamoListPrestamo : prestamoList) {
                prestamoListPrestamo.setTiposancionId(null);
                prestamoListPrestamo = em.merge(prestamoListPrestamo);
            }
            em.remove(tiposancion);
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Tiposancion> findTiposancionEntities() {
        return findTiposancionEntities(true, -1, -1);
    }

    public List<Tiposancion> findTiposancionEntities(int maxResults, int firstResult) {
        return findTiposancionEntities(false, maxResults, firstResult);
    }

    private List<Tiposancion> findTiposancionEntities(boolean all, int maxResults, int firstResult) {
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Tiposancion.class));
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

    public Tiposancion findTiposancion(Short id) {
        try {
            return em.find(Tiposancion.class, id);
        } finally {
            em.close();
        }
    }

    public int getTiposancionCount() {
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Tiposancion> rt = cq.from(Tiposancion.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
