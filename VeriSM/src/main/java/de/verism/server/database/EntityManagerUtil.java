package de.verism.server.database;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * The JPA entity manager is a wrapper around the HibernateSession.
 * @author Daniel Kotyk
 *
 */
public class EntityManagerUtil {
	
	// the string value refers to the persistence.xml
	private static final String JPA_CONFIG = "verism";
	
	// the database manager
    private static EntityManagerFactory emf = Persistence.createEntityManagerFactory(JPA_CONFIG);
    
    /**
     * Prevent instantiation.
     */
    private EntityManagerUtil() {}

    /**
     * Returns the instance of the database manager.
     * @return
     */
    public static EntityManager get() {
    	return emf.createEntityManager();
    }
}
