package contact.service.jpa;

import contact.service.DaoFactory;

/**
 * Manage instances of JPA DAO used in the app.
 * Easier to change the implementation of ContactDao.
 * 
 * @author mapfap - Sarun Wongtanakarn
 */
public class JpaDaoFactory extends DaoFactory {
	
	private static JpaDaoFactory factory;
	private JpaContactDao daoInstance;
	
	private JpaDaoFactory() {
		daoInstance = new JpaContactDao();
	}
	
	/**
	 * Get the instance of DaoFactory.
	 * @return instance of DaoFactory.
	 */
	public static JpaDaoFactory getInstance() {
		if ( factory == null ) {
			factory = new JpaDaoFactory();
		}
		return factory;
	}
	
	/**
	 * Get the instance of ContactDao.
	 * @return the instance of ContactDao.
	 */
	public JpaContactDao getContactDao() {
		return daoInstance;
	}

	@Override
	public void shutdown() {
		
	}
}