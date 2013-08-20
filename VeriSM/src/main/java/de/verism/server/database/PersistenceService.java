package de.verism.server.database;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

/**
 * The service providing DB access.
 * @author Daniel Kotyk
 *
 */
public class PersistenceService {
	private EntityManager em = EntityManagerUtil.get();
	
	/**
	 * Saves an object to DB.
	 * @param obj
	 */
	public void create(Object obj) {
		em.getTransaction().begin();
		em.persist(obj);
		em.getTransaction().commit();
		em.close();
	}
	
	/**
	 * Updates an object in the DB.
	 * @param obj
	 */
	public void update(Object obj) {
		em.getTransaction().begin();
		em.merge(obj);
		em.getTransaction().commit();
		em.close();
	}
	
	/**
	 * Removes an object from DB.
	 * @param obj
	 */
	public void delete(Object obj) {
		em.getTransaction().begin();
		em.remove(obj);
		em.getTransaction().commit();
		em.close();
	}
	
	/**
	 * Retrieves an account from DB.
	 * @param username
	 * @return
	 */
	public Account getAccount(String username) {
		Query q = em.createQuery("SELECT a FROM Account a WHERE a.username = :username");
		q.setParameter("username", username);
		try {
			return (Account) q.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}
}
