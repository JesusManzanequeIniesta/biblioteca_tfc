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
import com.jmi.bibliotecatfc.entities.Grupos;
import com.jmi.bibliotecatfc.entities.Usuarios;
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
public class DaoUsuarios implements Serializable {

    @PersistenceContext(unitName = "bibliotecatfc")
    private EntityManager em;
    
    @Transactional
    public void create(Usuarios usuarios) throws PreexistingEntityException, Exception {
        if (usuarios.getGruposList() == null) {
            usuarios.setGruposList(new ArrayList<Grupos>());
        }
        try {
            List<Grupos> attachedGruposList = new ArrayList<Grupos>();
            for (Grupos gruposListGruposToAttach : usuarios.getGruposList()) {
                gruposListGruposToAttach = em.getReference(gruposListGruposToAttach.getClass(), gruposListGruposToAttach.getGruposPK());
                attachedGruposList.add(gruposListGruposToAttach);
            }
            usuarios.setGruposList(attachedGruposList);
            em.persist(usuarios);
            for (Grupos gruposListGrupos : usuarios.getGruposList()) {
                Usuarios oldUsuariosOfGruposListGrupos = gruposListGrupos.getUsuarios();
                gruposListGrupos.setUsuarios(usuarios);
                gruposListGrupos = em.merge(gruposListGrupos);
                if (oldUsuariosOfGruposListGrupos != null) {
                    oldUsuariosOfGruposListGrupos.getGruposList().remove(gruposListGrupos);
                    oldUsuariosOfGruposListGrupos = em.merge(oldUsuariosOfGruposListGrupos);
                }
            }
        } catch (Exception ex) {
            if (findUsuarios(usuarios.getUsuario()) != null) {
                throw new PreexistingEntityException("Usuarios " + usuarios + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    @Transactional
    public void edit(Usuarios usuarios) throws IllegalOrphanException, NonexistentEntityException, Exception {
        try {
            Usuarios persistentUsuarios = em.find(Usuarios.class, usuarios.getUsuario());
            List<Grupos> gruposListOld = persistentUsuarios.getGruposList();
            List<Grupos> gruposListNew = usuarios.getGruposList();
            List<String> illegalOrphanMessages = null;
            for (Grupos gruposListOldGrupos : gruposListOld) {
                if (!gruposListNew.contains(gruposListOldGrupos)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Grupos " + gruposListOldGrupos + " since its usuarios field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<Grupos> attachedGruposListNew = new ArrayList<Grupos>();
            for (Grupos gruposListNewGruposToAttach : gruposListNew) {
                gruposListNewGruposToAttach = em.getReference(gruposListNewGruposToAttach.getClass(), gruposListNewGruposToAttach.getGruposPK());
                attachedGruposListNew.add(gruposListNewGruposToAttach);
            }
            gruposListNew = attachedGruposListNew;
            usuarios.setGruposList(gruposListNew);
            usuarios = em.merge(usuarios);
            for (Grupos gruposListNewGrupos : gruposListNew) {
                if (!gruposListOld.contains(gruposListNewGrupos)) {
                    Usuarios oldUsuariosOfGruposListNewGrupos = gruposListNewGrupos.getUsuarios();
                    gruposListNewGrupos.setUsuarios(usuarios);
                    gruposListNewGrupos = em.merge(gruposListNewGrupos);
                    if (oldUsuariosOfGruposListNewGrupos != null && !oldUsuariosOfGruposListNewGrupos.equals(usuarios)) {
                        oldUsuariosOfGruposListNewGrupos.getGruposList().remove(gruposListNewGrupos);
                        oldUsuariosOfGruposListNewGrupos = em.merge(oldUsuariosOfGruposListNewGrupos);
                    }
                }
            }
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                String id = usuarios.getUsuario();
                if (findUsuarios(id) == null) {
                    throw new NonexistentEntityException("The usuarios with id " + id + " no longer exists.");
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
    public void destroy(String id) throws IllegalOrphanException, NonexistentEntityException {
        try {
            Usuarios usuarios;
            try {
                usuarios = em.getReference(Usuarios.class, id);
                usuarios.getUsuario();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The usuarios with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Grupos> gruposListOrphanCheck = usuarios.getGruposList();
            for (Grupos gruposListOrphanCheckGrupos : gruposListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Usuarios (" + usuarios + ") cannot be destroyed since the Grupos " + gruposListOrphanCheckGrupos + " in its gruposList field has a non-nullable usuarios field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(usuarios);
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Usuarios> findUsuariosEntities() {
        return findUsuariosEntities(true, -1, -1);
    }

    public List<Usuarios> findUsuariosEntities(int maxResults, int firstResult) {
        return findUsuariosEntities(false, maxResults, firstResult);
    }

    private List<Usuarios> findUsuariosEntities(boolean all, int maxResults, int firstResult) {
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Usuarios.class));
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

    public Usuarios findUsuarios(String id) {
        try {
            return em.find(Usuarios.class, id);
        } finally {
            em.close();
        }
    }

    public int getUsuariosCount() {
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Usuarios> rt = cq.from(Usuarios.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
