package contact.service;

/**
 * Manage instances of DAO used in the app.
 * Easier to change the implementation of ContactDao.
 * 
 * @author mapfap - Sarun Wongtanakarn
 */
public class DaoFactory {
	
	private static DaoFactory factory;
	private ContactDao daoInstance;
	
	private DaoFactory() {
		daoInstance = new ContactDao();
	}
	
	/**
	 * Get the instance of DaoFactory.
	 * @return instance of DaoFactory.
	 */
	public static DaoFactory getInstance() {
		if ( factory == null ) {
			factory = new DaoFactory();
		}
		return factory;
	}
	
	/**
	 * Get the instance of ContactDao.
	 * @return the instance of ContactDao.
	 */
	public ContactDao getContactDao() {
		return daoInstance;
	}
}