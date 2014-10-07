package contact.service.jpa;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import contact.service.ContactDao;
import contact.service.ContactDaoFactory;

/**
 * Manage instances of DAO that use the Java Persistence API (JPA) used in the app.
 * Easier to change the implementation of ContactDao.
 * 
 * @author mapfap - Sarun Wongtanakarn
 */
public class JpaContactDaoFactory extends ContactDaoFactory {
	
	private static final String PERSISTENCE_UNIT = "contacts";
	private ContactDao daoInstance;
	private final EntityManagerFactory emf;
	private EntityManager em;
	private static Logger logger;
	
	static {
		logger = Logger.getLogger(JpaContactDaoFactory.class.getName());
	}
	
	/**
	 * Suppose to call only once by its abstract factory.
	 */
	public JpaContactDaoFactory() {
		emf = Persistence.createEntityManagerFactory( PERSISTENCE_UNIT );
		em = emf.createEntityManager();
		daoInstance = new JpaContactDao( em );
	}
	
	/**
	 * Get the instance of ContactDao.
	 * @return the instance of ContactDao.
	 */
	public ContactDao getContactDao() {
		return daoInstance;
	}

	@Override
	public void shutdown() {
		try {
			if ( em != null && em.isOpen() ) {
				em.close();
			}
			if ( emf != null && emf.isOpen() ) {
				emf.close();
			}
		} catch ( IllegalStateException ex ) {
			logger.log( Level.SEVERE, ex.toString() );
		}
	}
}