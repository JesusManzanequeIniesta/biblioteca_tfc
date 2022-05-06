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
import com.jmi.bibliotecatfc.entities.Peticion;
import java.util.ArrayList;
import java.util.List;
import com.jmi.bibliotecatfc.entities.Prestamo;
import com.jmi.bibliotecatfc.entities.Socio;
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
public class DaoSocio implements Serializable {

    @PersistenceContext(unitName = "bibliotecatfc")
    private EntityManager em;

    @Transactional
    public void create(Socio socio) throws PreexistingEntityException, Exception {
        if (socio.getPeticionList() == null) {
            socio.setPeticionList(new ArrayList<Peticion>());
        }
        if (socio.getPrestamoList() == null) {
            socio.setPrestamoList(new ArrayList<Prestamo>());
        }
        try {
            List<Peticion> attachedPeticionList = new ArrayList<Peticion>();
            for (Peticion peticionListPeticionToAttach : socio.getPeticionList()) {
                peticionListPeticionToAttach = em.getReference(peticionListPeticionToAttach.getClass(), peticionListPeticionToAttach.getId());
                attachedPeticionList.add(peticionListPeticionToAttach);
            }
            socio.setPeticionList(attachedPeticionList);
            List<Prestamo> attachedPrestamoList = new ArrayList<Prestamo>();
            for (Prestamo prestamoListPrestamoToAttach : socio.getPrestamoList()) {
                prestamoListPrestamoToAttach = em.getReference(prestamoListPrestamoToAttach.getClass(), prestamoListPrestamoToAttach.getId());
                attachedPrestamoList.add(prestamoListPrestamoToAttach);
            }
            socio.setPrestamoList(attachedPrestamoList);
            em.persist(socio);
            for (Peticion peticionListPeticion : socio.getPeticionList()) {
                Socio oldSocioIdOfPeticionListPeticion = peticionListPeticion.getSocioId();
                peticionListPeticion.setSocioId(socio);
                peticionListPeticion = em.merge(peticionListPeticion);
                if (oldSocioIdOfPeticionListPeticion != null) {
                    oldSocioIdOfPeticionListPeticion.getPeticionList().remove(peticionListPeticion);
                    oldSocioIdOfPeticionListPeticion = em.merge(oldSocioIdOfPeticionListPeticion);
                }
            }
            for (Prestamo prestamoListPrestamo : socio.getPrestamoList()) {
                Socio oldSocioIdOfPrestamoListPrestamo = prestamoListPrestamo.getSocioId();
                prestamoListPrestamo.setSocioId(socio);
                prestamoListPrestamo = em.merge(prestamoListPrestamo);
                if (oldSocioIdOfPrestamoListPrestamo != null) {
                    oldSocioIdOfPrestamoListPrestamo.getPrestamoList().remove(prestamoListPrestamo);
                    oldSocioIdOfPrestamoListPrestamo = em.merge(oldSocioIdOfPrestamoListPrestamo);
                }
            }
        } catch (Exception ex) {
            if (findSocio(socio.getId()) != null) {
                throw new PreexistingEntityException("Socio " + socio + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    @Transactional
    public void edit(Socio socio) throws IllegalOrphanException, NonexistentEntityException, Exception {
        try {
            Socio persistentSocio = em.find(Socio.class, socio.getId());
            List<Peticion> peticionListOld = persistentSocio.getPeticionList();
            List<Peticion> peticionListNew = socio.getPeticionList();
            List<Prestamo> prestamoListOld = persistentSocio.getPrestamoList();
            List<Prestamo> prestamoListNew = socio.getPrestamoList();
            List<String> illegalOrphanMessages = null;
            for (Peticion peticionListOldPeticion : peticionListOld) {
                if (!peticionListNew.contains(peticionListOldPeticion)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Peticion " + peticionListOldPeticion + " since its socioId field is not nullable.");
                }
            }
            for (Prestamo prestamoListOldPrestamo : prestamoListOld) {
                if (!prestamoListNew.contains(prestamoListOldPrestamo)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Prestamo " + prestamoListOldPrestamo + " since its socioId field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<Peticion> attachedPeticionListNew = new ArrayList<Peticion>();
            for (Peticion peticionListNewPeticionToAttach : peticionListNew) {
                peticionListNewPeticionToAttach = em.getReference(peticionListNewPeticionToAttach.getClass(), peticionListNewPeticionToAttach.getId());
                attachedPeticionListNew.add(peticionListNewPeticionToAttach);
            }
            peticionListNew = attachedPeticionListNew;
            socio.setPeticionList(peticionListNew);
            List<Prestamo> attachedPrestamoListNew = new ArrayList<Prestamo>();
            for (Prestamo prestamoListNewPrestamoToAttach : prestamoListNew) {
                prestamoListNewPrestamoToAttach = em.getReference(prestamoListNewPrestamoToAttach.getClass(), prestamoListNewPrestamoToAttach.getId());
                attachedPrestamoListNew.add(prestamoListNewPrestamoToAttach);
            }
            prestamoListNew = attachedPrestamoListNew;
            socio.setPrestamoList(prestamoListNew);
            socio = em.merge(socio);
            for (Peticion peticionListNewPeticion : peticionListNew) {
                if (!peticionListOld.contains(peticionListNewPeticion)) {
                    Socio oldSocioIdOfPeticionListNewPeticion = peticionListNewPeticion.getSocioId();
                    peticionListNewPeticion.setSocioId(socio);
                    peticionListNewPeticion = em.merge(peticionListNewPeticion);
                    if (oldSocioIdOfPeticionListNewPeticion != null && !oldSocioIdOfPeticionListNewPeticion.equals(socio)) {
                        oldSocioIdOfPeticionListNewPeticion.getPeticionList().remove(peticionListNewPeticion);
                        oldSocioIdOfPeticionListNewPeticion = em.merge(oldSocioIdOfPeticionListNewPeticion);
                    }
                }
            }
            for (Prestamo prestamoListNewPrestamo : prestamoListNew) {
                if (!prestamoListOld.contains(prestamoListNewPrestamo)) {
                    Socio oldSocioIdOfPrestamoListNewPrestamo = prestamoListNewPrestamo.getSocioId();
                    prestamoListNewPrestamo.setSocioId(socio);
                    prestamoListNewPrestamo = em.merge(prestamoListNewPrestamo);
                    if (oldSocioIdOfPrestamoListNewPrestamo != null && !oldSocioIdOfPrestamoListNewPrestamo.equals(socio)) {
                        oldSocioIdOfPrestamoListNewPrestamo.getPrestamoList().remove(prestamoListNewPrestamo);
                        oldSocioIdOfPrestamoListNewPrestamo = em.merge(oldSocioIdOfPrestamoListNewPrestamo);
                    }
                }
            }
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = socio.getId();
                if (findSocio(id) == null) {
                    throw new NonexistentEntityException("The socio with id " + id + " no longer exists.");
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
            Socio socio;
            try {
                socio = em.getReference(Socio.class, id);
                socio.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The socio with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Peticion> peticionListOrphanCheck = socio.getPeticionList();
            for (Peticion peticionListOrphanCheckPeticion : peticionListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Socio (" + socio + ") cannot be destroyed since the Peticion " + peticionListOrphanCheckPeticion + " in its peticionList field has a non-nullable socioId field.");
            }
            List<Prestamo> prestamoListOrphanCheck = socio.getPrestamoList();
            for (Prestamo prestamoListOrphanCheckPrestamo : prestamoListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Socio (" + socio + ") cannot be destroyed since the Prestamo " + prestamoListOrphanCheckPrestamo + " in its prestamoList field has a non-nullable socioId field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(socio);
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Socio> findSocioEntities() {
        return findSocioEntities(true, -1, -1);
    }

    public List<Socio> findSocioEntities(int maxResults, int firstResult) {
        return findSocioEntities(false, maxResults, firstResult);
    }

    private List<Socio> findSocioEntities(boolean all, int maxResults, int firstResult) {
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Socio.class));
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

    public Socio findSocio(Integer id) {
        try {
            return em.find(Socio.class, id);
        } finally {
            em.close();
        }
    }

    public int getSocioCount() {
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Socio> rt = cq.from(Socio.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

    public Socio findByEmail(String email) {
        try{
            return em.createNamedQuery("Socio.findByEmail", Socio.class).setParameter("email", email).getSingleResult();
        } catch(Exception ex){
            throw ex;
        }
    }
    
}
