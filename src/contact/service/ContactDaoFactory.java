package contact.service;



/**
 * DaoFactory defines methods for obtaining instance of data access objects.
 * To create DAO you first get an instance of a concrete factory by invoking
 * <p>
 * <tt>DaoFactory factory = DaoFactory.getInstance(); </tt>
 * <p>
 * Then use the <tt>factory</tt> object to get instances of actual DAO.
 * This factory is an abstract class.  There are concrete subclasses for
 * each persistence mechanism.  You can add your own factory by subclassing
 * this factory.
 * 
 * @author jim, Sarun Wongtanakarn
 */
public abstract class ContactDaoFactory {
	// singleton instance of this factory
	private static ContactDaoFactory factory;
	
	/** 
	 * this class shouldn't be instantiated, but constructor must be visible to subclasses.
	 */
	protected ContactDaoFactory() {
		// nothing to do
	}
	
	/**
	 * Get a singleton instance of the DaoFactory.
	 * @return instance of a concrete DaoFactory
	 */
	public static ContactDaoFactory getInstance() {
		if ( factory == null ) {
			factory = new contact.service.mem.MemContactDaoFactory();
			// factory = new contact.service.jpa.JpaDaoFactory();
		}
		return factory;
	}
	
	/**
	 * Set DAO factory for.
	 * So it's able to inject the preferred DaoFactory.
	 * @param afactory a new factory to be used as concrete factory class.
	 */
	public static void setDaoFactory( ContactDaoFactory afactory ) {
		factory = afactory;
	}
	
	/**
	 * Get an instance of a data access object for Contact objects.
	 * Subclasses of the base DaoFactory class must provide a concrete
	 * instance of this method that returns a ContactDao suitable
	 * for their persistence framework.
	 * @return instance of Contact's DAO
	 */
	public abstract ContactDao getContactDao();
	
	/**
	 * Shutdown all persistence services.
	 * This method gives the persistence framework a chance to
	 * gracefully save data and close databases before the
	 * application terminates.
	 */
	public abstract void shutdown();

}