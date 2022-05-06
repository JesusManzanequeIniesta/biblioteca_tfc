/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jmi.bibliotecatfc.dao;

import com.jmi.bibliotecatfc.dao.exceptions.NonexistentEntityException;
import com.jmi.bibliotecatfc.dao.exceptions.PreexistingEntityException;
import com.jmi.bibliotecatfc.entities.Grupos;
import com.jmi.bibliotecatfc.entities.GruposPK;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.jmi.bibliotecatfc.entities.Usuarios;
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
public class DaoGrupos implements Serializable {

    @PersistenceContext(unitName = "bibliotecatfc")
    private EntityManager em;

    @Transactional
    public void create(Grupos grupos) throws PreexistingEntityException, Exception {
        if (grupos.getGruposPK() == null) {
            grupos.setGruposPK(new GruposPK());
        }
        grupos.getGruposPK().setIdusuario(grupos.getUsuarios().getUsuario());
        try {
            Usuarios usuarios = grupos.getUsuarios();
            if (usuarios != null) {
                usuarios = em.getReference(usuarios.getClass(), usuarios.getUsuario());
                grupos.setUsuarios(usuarios);
            }
            em.persist(grupos);
            if (usuarios != null) {
                usuarios.getGruposList().add(grupos);
                usuarios = em.merge(usuarios);
            }
        } catch (Exception ex) {
            if (findGrupos(grupos.getGruposPK()) != null) {
                throw new PreexistingEntityException("Grupos " + grupos + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    @Transactional
    public void edit(Grupos grupos) throws NonexistentEntityException, Exception {
        grupos.getGruposPK().setIdusuario(grupos.getUsuarios().getUsuario());
        try {
            Grupos persistentGrupos = em.find(Grupos.class, grupos.getGruposPK());
            Usuarios usuariosOld = persistentGrupos.getUsuarios();
            Usuarios usuariosNew = grupos.getUsuarios();
            if (usuariosNew != null) {
                usuariosNew = em.getReference(usuariosNew.getClass(), usuariosNew.getUsuario());
                grupos.setUsuarios(usuariosNew);
            }
            grupos = em.merge(grupos);
            if (usuariosOld != null && !usuariosOld.equals(usuariosNew)) {
                usuariosOld.getGruposList().remove(grupos);
                usuariosOld = em.merge(usuariosOld);
            }
            if (usuariosNew != null && !usuariosNew.equals(usuariosOld)) {
                usuariosNew.getGruposList().add(grupos);
                usuariosNew = em.merge(usuariosNew);
            }
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                GruposPK id = grupos.getGruposPK();
                if (findGrupos(id) == null) {
                    throw new NonexistentEntityException("The grupos with id " + id + " no longer exists.");
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
    public void destroy(GruposPK id) throws NonexistentEntityException {
        try {
            Grupos grupos;
            try {
                grupos = em.getReference(Grupos.class, id);
                grupos.getGruposPK();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The grupos with id " + id + " no longer exists.", enfe);
            }
            Usuarios usuarios = grupos.getUsuarios();
            if (usuarios != null) {
                usuarios.getGruposList().remove(grupos);
                usuarios = em.merge(usuarios);
            }
            em.remove(grupos);
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Grupos> findGruposEntities() {
        return findGruposEntities(true, -1, -1);
    }

    public List<Grupos> findGruposEntities(int maxResults, int firstResult) {
        return findGruposEntities(false, maxResults, firstResult);
    }

    private List<Grupos> findGruposEntities(boolean all, int maxResults, int firstResult) {
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Grupos.class));
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

    public Grupos findGrupos(GruposPK id) {
        try {
            return em.find(Grupos.class, id);
        } finally {
            em.close();
        }
    }

    public int getGruposCount() {
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Grupos> rt = cq.from(Grupos.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
