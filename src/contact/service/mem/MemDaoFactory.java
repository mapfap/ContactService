package contact.service.mem;

import contact.service.DaoFactory;

/**
 * Manage instances of Memory-Based DAO used in the app.
 * Easier to change the implementation of ContactDao.
 * 
 * @author mapfap - Sarun Wongtanakarn
 */
public class MemDaoFactory extends DaoFactory {
	
	private static MemDaoFactory factory;
	private MemContactDao daoInstance;
	
	private MemDaoFactory() {
		daoInstance = new MemContactDao();
	}
	
	/**
	 * Get the instance of DaoFactory.
	 * @return instance of DaoFactory.
	 */
	public static MemDaoFactory getInstance() {
		if ( factory == null ) {
			factory = new MemDaoFactory();
		}
		return factory;
	}
	
	/**
	 * Get the instance of ContactDao.
	 * @return the instance of ContactDao.
	 */
	public MemContactDao getContactDao() {
		return daoInstance;
	}

	@Override
	public void shutdown() {
		// TODO Auto-generated method stub
		
	}
}