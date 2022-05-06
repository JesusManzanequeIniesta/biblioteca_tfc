/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jmi.bibliotecatfc.dao;

import com.jmi.bibliotecatfc.dao.exceptions.NonexistentEntityException;
import com.jmi.bibliotecatfc.dao.exceptions.PreexistingEntityException;
import com.jmi.bibliotecatfc.entities.Autor;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.jmi.bibliotecatfc.entities.Libro;
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
public class DaoAutor implements Serializable {

    @PersistenceContext(unitName = "bibliotecatfc")
    private EntityManager em;

    @Transactional
    public void create(Autor autor) throws PreexistingEntityException, Exception {
        if (autor.getLibroList() == null) {
            autor.setLibroList(new ArrayList<Libro>());
        }
        try {
            List<Libro> attachedLibroList = new ArrayList<Libro>();
            for (Libro libroListLibroToAttach : autor.getLibroList()) {
                libroListLibroToAttach = em.getReference(libroListLibroToAttach.getClass(), libroListLibroToAttach.getId());
                attachedLibroList.add(libroListLibroToAttach);
            }
            autor.setLibroList(attachedLibroList);
            em.persist(autor);
            for (Libro libroListLibro : autor.getLibroList()) {
                libroListLibro.getAutorList().add(autor);
                libroListLibro = em.merge(libroListLibro);
            }
        } catch (Exception ex) {
            if (findAutor(autor.getId()) != null) {
                throw new PreexistingEntityException("Autor " + autor + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    @Transactional
    public void edit(Autor autor) throws NonexistentEntityException, Exception {
        try {
            Autor persistentAutor = em.find(Autor.class, autor.getId());
            List<Libro> libroListOld = persistentAutor.getLibroList();
            List<Libro> libroListNew = autor.getLibroList();
            List<Libro> attachedLibroListNew = new ArrayList<Libro>();
            for (Libro libroListNewLibroToAttach : libroListNew) {
                libroListNewLibroToAttach = em.getReference(libroListNewLibroToAttach.getClass(), libroListNewLibroToAttach.getId());
                attachedLibroListNew.add(libroListNewLibroToAttach);
            }
            libroListNew = attachedLibroListNew;
            autor.setLibroList(libroListNew);
            autor = em.merge(autor);
            for (Libro libroListOldLibro : libroListOld) {
                if (!libroListNew.contains(libroListOldLibro)) {
                    libroListOldLibro.getAutorList().remove(autor);
                    libroListOldLibro = em.merge(libroListOldLibro);
                }
            }
            for (Libro libroListNewLibro : libroListNew) {
                if (!libroListOld.contains(libroListNewLibro)) {
                    libroListNewLibro.getAutorList().add(autor);
                    libroListNewLibro = em.merge(libroListNewLibro);
                }
            }
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = autor.getId();
                if (findAutor(id) == null) {
                    throw new NonexistentEntityException("The autor with id " + id + " no longer exists.");
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
            Autor autor;
            try {
                autor = em.getReference(Autor.class, id);
                autor.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The autor with id " + id + " no longer exists.", enfe);
            }
            List<Libro> libroList = autor.getLibroList();
            for (Libro libroListLibro : libroList) {
                libroListLibro.getAutorList().remove(autor);
                libroListLibro = em.merge(libroListLibro);
            }
            em.remove(autor);
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

//    public List<Autor> findAutorEntities() {
//        return findAutorEntities(true, -1, -1);
//    }
//
//    public List<Autor> findAutorEntities(int maxResults, int firstResult) {
//        return findAutorEntities(false, maxResults, firstResult);
//    }
//
//    private List<Autor> findAutorEntities(boolean all, int maxResults, int firstResult) {
//        try {
//            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
//            cq.select(cq.from(Autor.class));
//            Query q = em.createQuery(cq);
//            if (!all) {
//                q.setMaxResults(maxResults);
//                q.setFirstResult(firstResult);
//            }
//            return q.getResultList();
//        } finally {
//            em.close();
//        }
//    }
    public List<Autor> listAllAuthors(){
        return em.createNamedQuery("Autor.findAll", Autor.class).getResultList();
    }

    public Autor findAutor(Integer id) {
        try {
            return em.find(Autor.class, id);
        } finally {
            em.close();
        }
    }

    public int getAutorCount() {
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Autor> rt = cq.from(Autor.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

    public Autor findByNombre(String nombre) {
        return em.createNamedQuery("Autor.findByNombre", Autor.class).getSingleResult();
    }
    
}
