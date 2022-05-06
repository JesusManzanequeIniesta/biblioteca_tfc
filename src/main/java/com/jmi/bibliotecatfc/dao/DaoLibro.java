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
import com.jmi.bibliotecatfc.entities.Autor;
import java.util.ArrayList;
import java.util.List;
import com.jmi.bibliotecatfc.entities.Ejemplar;
import com.jmi.bibliotecatfc.entities.Libro;
import com.jmi.bibliotecatfc.entities.Peticion;
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
public class DaoLibro implements Serializable {

    @PersistenceContext(unitName = "bibliotecatfc")
    private EntityManager em;

    @Transactional
    public void create(Libro libro) throws PreexistingEntityException, Exception {
        if (libro.getAutorList() == null) {
            libro.setAutorList(new ArrayList<Autor>());
        }
        if (libro.getEjemplarList() == null) {
            libro.setEjemplarList(new ArrayList<Ejemplar>());
        }
        if (libro.getPeticionList() == null) {
            libro.setPeticionList(new ArrayList<Peticion>());
        }
        try {
            List<Autor> attachedAutorList = new ArrayList<Autor>();
            for (Autor autorListAutorToAttach : libro.getAutorList()) {
                autorListAutorToAttach = em.getReference(autorListAutorToAttach.getClass(), autorListAutorToAttach.getId());
                attachedAutorList.add(autorListAutorToAttach);
            }
            libro.setAutorList(attachedAutorList);
            List<Ejemplar> attachedEjemplarList = new ArrayList<Ejemplar>();
            for (Ejemplar ejemplarListEjemplarToAttach : libro.getEjemplarList()) {
                ejemplarListEjemplarToAttach = em.getReference(ejemplarListEjemplarToAttach.getClass(), ejemplarListEjemplarToAttach.getId());
                attachedEjemplarList.add(ejemplarListEjemplarToAttach);
            }
            libro.setEjemplarList(attachedEjemplarList);
            List<Peticion> attachedPeticionList = new ArrayList<Peticion>();
            for (Peticion peticionListPeticionToAttach : libro.getPeticionList()) {
                peticionListPeticionToAttach = em.getReference(peticionListPeticionToAttach.getClass(), peticionListPeticionToAttach.getId());
                attachedPeticionList.add(peticionListPeticionToAttach);
            }
            libro.setPeticionList(attachedPeticionList);
            em.persist(libro);
            for (Autor autorListAutor : libro.getAutorList()) {
                autorListAutor.getLibroList().add(libro);
                autorListAutor = em.merge(autorListAutor);
            }
            for (Ejemplar ejemplarListEjemplar : libro.getEjemplarList()) {
                Libro oldLibroIdOfEjemplarListEjemplar = ejemplarListEjemplar.getLibroId();
                ejemplarListEjemplar.setLibroId(libro);
                ejemplarListEjemplar = em.merge(ejemplarListEjemplar);
                if (oldLibroIdOfEjemplarListEjemplar != null) {
                    oldLibroIdOfEjemplarListEjemplar.getEjemplarList().remove(ejemplarListEjemplar);
                    oldLibroIdOfEjemplarListEjemplar = em.merge(oldLibroIdOfEjemplarListEjemplar);
                }
            }
            for (Peticion peticionListPeticion : libro.getPeticionList()) {
                Libro oldLibroIdOfPeticionListPeticion = peticionListPeticion.getLibroId();
                peticionListPeticion.setLibroId(libro);
                peticionListPeticion = em.merge(peticionListPeticion);
                if (oldLibroIdOfPeticionListPeticion != null) {
                    oldLibroIdOfPeticionListPeticion.getPeticionList().remove(peticionListPeticion);
                    oldLibroIdOfPeticionListPeticion = em.merge(oldLibroIdOfPeticionListPeticion);
                }
            }
        } catch (Exception ex) {
            if (findLibro(libro.getId()) != null) {
                throw new PreexistingEntityException("Libro " + libro + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    @Transactional
    public void edit(Libro libro) throws IllegalOrphanException, NonexistentEntityException, Exception {
        try {
            Libro persistentLibro = em.find(Libro.class, libro.getId());
            List<Autor> autorListOld = persistentLibro.getAutorList();
            List<Autor> autorListNew = libro.getAutorList();
            List<Ejemplar> ejemplarListOld = persistentLibro.getEjemplarList();
            List<Ejemplar> ejemplarListNew = libro.getEjemplarList();
            List<Peticion> peticionListOld = persistentLibro.getPeticionList();
            List<Peticion> peticionListNew = libro.getPeticionList();
            List<String> illegalOrphanMessages = null;
            for (Ejemplar ejemplarListOldEjemplar : ejemplarListOld) {
                if (!ejemplarListNew.contains(ejemplarListOldEjemplar)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Ejemplar " + ejemplarListOldEjemplar + " since its libroId field is not nullable.");
                }
            }
            for (Peticion peticionListOldPeticion : peticionListOld) {
                if (!peticionListNew.contains(peticionListOldPeticion)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Peticion " + peticionListOldPeticion + " since its libroId field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<Autor> attachedAutorListNew = new ArrayList<Autor>();
            for (Autor autorListNewAutorToAttach : autorListNew) {
                autorListNewAutorToAttach = em.getReference(autorListNewAutorToAttach.getClass(), autorListNewAutorToAttach.getId());
                attachedAutorListNew.add(autorListNewAutorToAttach);
            }
            autorListNew = attachedAutorListNew;
            libro.setAutorList(autorListNew);
            List<Ejemplar> attachedEjemplarListNew = new ArrayList<Ejemplar>();
            for (Ejemplar ejemplarListNewEjemplarToAttach : ejemplarListNew) {
                ejemplarListNewEjemplarToAttach = em.getReference(ejemplarListNewEjemplarToAttach.getClass(), ejemplarListNewEjemplarToAttach.getId());
                attachedEjemplarListNew.add(ejemplarListNewEjemplarToAttach);
            }
            ejemplarListNew = attachedEjemplarListNew;
            libro.setEjemplarList(ejemplarListNew);
            List<Peticion> attachedPeticionListNew = new ArrayList<Peticion>();
            for (Peticion peticionListNewPeticionToAttach : peticionListNew) {
                peticionListNewPeticionToAttach = em.getReference(peticionListNewPeticionToAttach.getClass(), peticionListNewPeticionToAttach.getId());
                attachedPeticionListNew.add(peticionListNewPeticionToAttach);
            }
            peticionListNew = attachedPeticionListNew;
            libro.setPeticionList(peticionListNew);
            libro = em.merge(libro);
            for (Autor autorListOldAutor : autorListOld) {
                if (!autorListNew.contains(autorListOldAutor)) {
                    autorListOldAutor.getLibroList().remove(libro);
                    autorListOldAutor = em.merge(autorListOldAutor);
                }
            }
            for (Autor autorListNewAutor : autorListNew) {
                if (!autorListOld.contains(autorListNewAutor)) {
                    autorListNewAutor.getLibroList().add(libro);
                    autorListNewAutor = em.merge(autorListNewAutor);
                }
            }
            for (Ejemplar ejemplarListNewEjemplar : ejemplarListNew) {
                if (!ejemplarListOld.contains(ejemplarListNewEjemplar)) {
                    Libro oldLibroIdOfEjemplarListNewEjemplar = ejemplarListNewEjemplar.getLibroId();
                    ejemplarListNewEjemplar.setLibroId(libro);
                    ejemplarListNewEjemplar = em.merge(ejemplarListNewEjemplar);
                    if (oldLibroIdOfEjemplarListNewEjemplar != null && !oldLibroIdOfEjemplarListNewEjemplar.equals(libro)) {
                        oldLibroIdOfEjemplarListNewEjemplar.getEjemplarList().remove(ejemplarListNewEjemplar);
                        oldLibroIdOfEjemplarListNewEjemplar = em.merge(oldLibroIdOfEjemplarListNewEjemplar);
                    }
                }
            }
            for (Peticion peticionListNewPeticion : peticionListNew) {
                if (!peticionListOld.contains(peticionListNewPeticion)) {
                    Libro oldLibroIdOfPeticionListNewPeticion = peticionListNewPeticion.getLibroId();
                    peticionListNewPeticion.setLibroId(libro);
                    peticionListNewPeticion = em.merge(peticionListNewPeticion);
                    if (oldLibroIdOfPeticionListNewPeticion != null && !oldLibroIdOfPeticionListNewPeticion.equals(libro)) {
                        oldLibroIdOfPeticionListNewPeticion.getPeticionList().remove(peticionListNewPeticion);
                        oldLibroIdOfPeticionListNewPeticion = em.merge(oldLibroIdOfPeticionListNewPeticion);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = libro.getId();
                if (findLibro(id) == null) {
                    throw new NonexistentEntityException("The libro with id " + id + " no longer exists.");
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
            Libro libro;
            try {
                libro = em.getReference(Libro.class, id);
                libro.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The libro with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Ejemplar> ejemplarListOrphanCheck = libro.getEjemplarList();
            for (Ejemplar ejemplarListOrphanCheckEjemplar : ejemplarListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Libro (" + libro + ") cannot be destroyed since the Ejemplar " + ejemplarListOrphanCheckEjemplar + " in its ejemplarList field has a non-nullable libroId field.");
            }
            List<Peticion> peticionListOrphanCheck = libro.getPeticionList();
            for (Peticion peticionListOrphanCheckPeticion : peticionListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Libro (" + libro + ") cannot be destroyed since the Peticion " + peticionListOrphanCheckPeticion + " in its peticionList field has a non-nullable libroId field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<Autor> autorList = libro.getAutorList();
            for (Autor autorListAutor : autorList) {
                autorListAutor.getLibroList().remove(libro);
                autorListAutor = em.merge(autorListAutor);
            }
            em.remove(libro);
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Libro> findLibroEntities() {
        return findLibroEntities(true, -1, -1);
    }

    public List<Libro> findLibroEntities(int maxResults, int firstResult) {
        return findLibroEntities(false, maxResults, firstResult);
    }

    private List<Libro> findLibroEntities(boolean all, int maxResults, int firstResult) {
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Libro.class));
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

    public Libro findLibro(Integer id) {
        try {
            return em.find(Libro.class, id);
        } finally {
            em.close();
        }
    }

    public int getLibroCount() {
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Libro> rt = cq.from(Libro.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

    public Libro findByISBN(String bn) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
}
