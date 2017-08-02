/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.unibrasil.sca.cobol;

import com.unibrasil.sca.cobol.exceptions.IllegalOrphanException;
import com.unibrasil.sca.cobol.exceptions.NonexistentEntityException;
import com.unibrasil.sca.cobol.exceptions.PreexistingEntityException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author humbhenri
 */
public class UserJpaController implements Serializable {

    public UserJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(User user) throws PreexistingEntityException, Exception {
        if (user.getProfessorCollection() == null) {
            user.setProfessorCollection(new ArrayList<Professor>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Collection<Professor> attachedProfessorCollection = new ArrayList<Professor>();
            for (Professor professorCollectionProfessorToAttach : user.getProfessorCollection()) {
                professorCollectionProfessorToAttach = em.getReference(professorCollectionProfessorToAttach.getClass(), professorCollectionProfessorToAttach.getId());
                attachedProfessorCollection.add(professorCollectionProfessorToAttach);
            }
            user.setProfessorCollection(attachedProfessorCollection);
            em.persist(user);
            for (Professor professorCollectionProfessor : user.getProfessorCollection()) {
                User oldUsernameOfProfessorCollectionProfessor = professorCollectionProfessor.getUsername();
                professorCollectionProfessor.setUsername(user);
                professorCollectionProfessor = em.merge(professorCollectionProfessor);
                if (oldUsernameOfProfessorCollectionProfessor != null) {
                    oldUsernameOfProfessorCollectionProfessor.getProfessorCollection().remove(professorCollectionProfessor);
                    oldUsernameOfProfessorCollectionProfessor = em.merge(oldUsernameOfProfessorCollectionProfessor);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findUser(user.getUsername()) != null) {
                throw new PreexistingEntityException("User " + user + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(User user) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            User persistentUser = em.find(User.class, user.getUsername());
            Collection<Professor> professorCollectionOld = persistentUser.getProfessorCollection();
            Collection<Professor> professorCollectionNew = user.getProfessorCollection();
            List<String> illegalOrphanMessages = null;
            for (Professor professorCollectionOldProfessor : professorCollectionOld) {
                if (!professorCollectionNew.contains(professorCollectionOldProfessor)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Professor " + professorCollectionOldProfessor + " since its username field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Collection<Professor> attachedProfessorCollectionNew = new ArrayList<Professor>();
            for (Professor professorCollectionNewProfessorToAttach : professorCollectionNew) {
                professorCollectionNewProfessorToAttach = em.getReference(professorCollectionNewProfessorToAttach.getClass(), professorCollectionNewProfessorToAttach.getId());
                attachedProfessorCollectionNew.add(professorCollectionNewProfessorToAttach);
            }
            professorCollectionNew = attachedProfessorCollectionNew;
            user.setProfessorCollection(professorCollectionNew);
            user = em.merge(user);
            for (Professor professorCollectionNewProfessor : professorCollectionNew) {
                if (!professorCollectionOld.contains(professorCollectionNewProfessor)) {
                    User oldUsernameOfProfessorCollectionNewProfessor = professorCollectionNewProfessor.getUsername();
                    professorCollectionNewProfessor.setUsername(user);
                    professorCollectionNewProfessor = em.merge(professorCollectionNewProfessor);
                    if (oldUsernameOfProfessorCollectionNewProfessor != null && !oldUsernameOfProfessorCollectionNewProfessor.equals(user)) {
                        oldUsernameOfProfessorCollectionNewProfessor.getProfessorCollection().remove(professorCollectionNewProfessor);
                        oldUsernameOfProfessorCollectionNewProfessor = em.merge(oldUsernameOfProfessorCollectionNewProfessor);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                String id = user.getUsername();
                if (findUser(id) == null) {
                    throw new NonexistentEntityException("The user with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(String id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            User user;
            try {
                user = em.getReference(User.class, id);
                user.getUsername();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The user with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Professor> professorCollectionOrphanCheck = user.getProfessorCollection();
            for (Professor professorCollectionOrphanCheckProfessor : professorCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This User (" + user + ") cannot be destroyed since the Professor " + professorCollectionOrphanCheckProfessor + " in its professorCollection field has a non-nullable username field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(user);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<User> findUserEntities() {
        return findUserEntities(true, -1, -1);
    }

    public List<User> findUserEntities(int maxResults, int firstResult) {
        return findUserEntities(false, maxResults, firstResult);
    }

    private List<User> findUserEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(User.class));
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

    public User findUser(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(User.class, id);
        } finally {
            em.close();
        }
    }

    public int getUserCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<User> rt = cq.from(User.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
