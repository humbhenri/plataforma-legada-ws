/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.unibrasil.sca.cobol;

import com.unibrasil.sca.cobol.exceptions.NonexistentEntityException;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 *
 * @author humbhenri
 */
public class ProfessorJpaController implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(ProfessorJpaController.class.getSimpleName());

    private final EntityManager em;

    public ProfessorJpaController(EntityManager em) {
        this.em = em;
    }

    public EntityManager getEntityManager() {
        return this.em;
    }

    public void create(Professor professor) {
        try {
            em.getTransaction().begin();
            User username = professor.getUsername();
            if (username != null) {
                username = em.getReference(username.getClass(), username.getUsername());
                professor.setUsername(username);
            }
            em.persist(professor);
            if (username != null) {
                username.getProfessorCollection().add(professor);
                username = em.merge(username);
            }
            em.getTransaction().commit();
            LOGGER.log(Level.INFO, "Professor criado com sucesso: {0}", professor);
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Professor professor) throws NonexistentEntityException, Exception {
        try {
            em.getTransaction().begin();
            Professor persistentProfessor = em.find(Professor.class, professor.getId());
            User usernameOld = persistentProfessor.getUsername();
            User usernameNew = professor.getUsername();
            if (usernameNew != null) {
                usernameNew = em.getReference(usernameNew.getClass(), usernameNew.getUsername());
                professor.setUsername(usernameNew);
            }
            professor = em.merge(professor);
            if (usernameOld != null && !usernameOld.equals(usernameNew)) {
                usernameOld.getProfessorCollection().remove(professor);
                usernameOld = em.merge(usernameOld);
            }
            if (usernameNew != null && !usernameNew.equals(usernameOld)) {
                usernameNew.getProfessorCollection().add(professor);
                usernameNew = em.merge(usernameNew);
            }
            em.getTransaction().commit();
            LOGGER.log(Level.INFO, "Professor editado com sucesso: {0}", professor);
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = professor.getId();
                if (findProfessor(id) == null) {
                    throw new NonexistentEntityException("The professor with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException {
        try {
            em.getTransaction().begin();
            Professor professor;
            try {
                professor = em.getReference(Professor.class, id);
                professor.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The professor with id " + id + " no longer exists.", enfe);
            }
            User username = professor.getUsername();
            if (username != null) {
                username.getProfessorCollection().remove(professor);
                username = em.merge(username);
            }
            em.remove(professor);
            em.getTransaction().commit();
            LOGGER.info("Professor destruído");
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Professor> findProfessorEntities() {
        return findProfessorEntities(true, -1, -1);
    }

    public List<Professor> findProfessorEntities(int maxResults, int firstResult) {
        return findProfessorEntities(false, maxResults, firstResult);
    }

    private List<Professor> findProfessorEntities(boolean all, int maxResults, int firstResult) {
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Professor.class));
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

    public Professor findProfessor(Integer id) {
        try {
            return em.find(Professor.class, id);
        } finally {
            em.close();
        }
    }

    public int getProfessorCount() {
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Professor> rt = cq.from(Professor.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

}
