/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jmi.bibliotecatfc.dao;

import com.jmi.bibliotecatfc.dao.exceptions.IllegalOrphanException;
import com.jmi.bibliotecatfc.dao.exceptions.NonexistentEntityException;
import com.jmi.bibliotecatfc.dao.exceptions.PreexistingEntityException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.jmi.bibliotecatfc.entities.Ejemplar;
import com.jmi.bibliotecatfc.entities.Estado;
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
public class DaoEstado implements Serializable {

    @PersistenceContext(unitName = "bibliotecatfc")
    private EntityManager em;

    @Transactional
    public void create(Estado estado) throws PreexistingEntityException, Exception {
        if (estado.getEjemplarList() == null) {
            estado.setEjemplarList(new ArrayList<Ejemplar>());
        }
        try {
            List<Ejemplar> attachedEjemplarList = new ArrayList<Ejemplar>();
            for (Ejemplar ejemplarListEjemplarToAttach : estado.getEjemplarList()) {
                ejemplarListEjemplarToAttach = em.getReference(ejemplarListEjemplarToAttach.getClass(), ejemplarListEjemplarToAttach.getId());
                attachedEjemplarList.add(ejemplarListEjemplarToAttach);
            }
            estado.setEjemplarList(attachedEjemplarList);
            em.persist(estado);
            for (Ejemplar ejemplarListEjemplar : estado.getEjemplarList()) {
                Estado oldEstadoIdOfEjemplarListEjemplar = ejemplarListEjemplar.getEstadoId();
                ejemplarListEjemplar.setEstadoId(estado);
                ejemplarListEjemplar = em.merge(ejemplarListEjemplar);
                if (oldEstadoIdOfEjemplarListEjemplar != null) {
                    oldEstadoIdOfEjemplarListEjemplar.getEjemplarList().remove(ejemplarListEjemplar);
                    oldEstadoIdOfEjemplarListEjemplar = em.merge(oldEstadoIdOfEjemplarListEjemplar);
                }
            }
        } catch (Exception ex) {
            if (findEstado(estado.getId()) != null) {
                throw new PreexistingEntityException("Estado " + estado + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    @Transactional
    public void edit(Estado estado) throws IllegalOrphanException, NonexistentEntityException, Exception {
        try {
            Estado persistentEstado = em.find(Estado.class, estado.getId());
            List<Ejemplar> ejemplarListOld = persistentEstado.getEjemplarList();
            List<Ejemplar> ejemplarListNew = estado.getEjemplarList();
            List<String> illegalOrphanMessages = null;
            for (Ejemplar ejemplarListOldEjemplar : ejemplarListOld) {
                if (!ejemplarListNew.contains(ejemplarListOldEjemplar)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Ejemplar " + ejemplarListOldEjemplar + " since its estadoId field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<Ejemplar> attachedEjemplarListNew = new ArrayList<Ejemplar>();
            for (Ejemplar ejemplarListNewEjemplarToAttach : ejemplarListNew) {
                ejemplarListNewEjemplarToAttach = em.getReference(ejemplarListNewEjemplarToAttach.getClass(), ejemplarListNewEjemplarToAttach.getId());
                attachedEjemplarListNew.add(ejemplarListNewEjemplarToAttach);
            }
            ejemplarListNew = attachedEjemplarListNew;
            estado.setEjemplarList(ejemplarListNew);
            estado = em.merge(estado);
            for (Ejemplar ejemplarListNewEjemplar : ejemplarListNew) {
                if (!ejemplarListOld.contains(ejemplarListNewEjemplar)) {
                    Estado oldEstadoIdOfEjemplarListNewEjemplar = ejemplarListNewEjemplar.getEstadoId();
                    ejemplarListNewEjemplar.setEstadoId(estado);
                    ejemplarListNewEjemplar = em.merge(ejemplarListNewEjemplar);
                    if (oldEstadoIdOfEjemplarListNewEjemplar != null && !oldEstadoIdOfEjemplarListNewEjemplar.equals(estado)) {
                        oldEstadoIdOfEjemplarListNewEjemplar.getEjemplarList().remove(ejemplarListNewEjemplar);
                        oldEstadoIdOfEjemplarListNewEjemplar = em.merge(oldEstadoIdOfEjemplarListNewEjemplar);
                    }
                }
            }
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Short id = estado.getId();
                if (findEstado(id) == null) {
                    throw new NonexistentEntityException("The estado with id " + id + " no longer exists.");
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
    public void destroy(Short id) throws IllegalOrphanException, NonexistentEntityException {
        try {
            Estado estado;
            try {
                estado = em.getReference(Estado.class, id);
                estado.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The estado with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Ejemplar> ejemplarListOrphanCheck = estado.getEjemplarList();
            for (Ejemplar ejemplarListOrphanCheckEjemplar : ejemplarListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Estado (" + estado + ") cannot be destroyed since the Ejemplar " + ejemplarListOrphanCheckEjemplar + " in its ejemplarList field has a non-nullable estadoId field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(estado);
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Estado> findEstadoEntities() {
        return findEstadoEntities(true, -1, -1);
    }

    public List<Estado> findEstadoEntities(int maxResults, int firstResult) {
        return findEstadoEntities(false, maxResults, firstResult);
    }

    private List<Estado> findEstadoEntities(boolean all, int maxResults, int firstResult) {
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Estado.class));
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

    public Estado findEstado(Short id) {
        try {
            return em.find(Estado.class, id);
        } finally {
            em.close();
        }
    }

    public int getEstadoCount() {
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Estado> rt = cq.from(Estado.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
