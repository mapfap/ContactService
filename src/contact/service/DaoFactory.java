package contact.service;

/**
 * 
 * 
 * @author mapfap - Sarun Wongtanakarn
 */
public class DaoFactory {
	
	private static DaoFactory factory;
	private ContactDao daoInstance;
	
	private DaoFactory() {
		daoInstance = new ContactDao();
	}
	
	public static DaoFactory getInstance() {
		if ( factory == null ) {
			factory = new DaoFactory();
		}
		return factory;
	}
	
	public ContactDao getContactDao() {
		return daoInstance;
	}
}