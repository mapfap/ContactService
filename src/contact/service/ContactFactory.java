package contact.service;

import java.util.concurrent.atomic.AtomicLong;

import contact.entity.Contact;

/**
 * 
 * Creator of the Contact class, encapsulates the responsibility of
 * creating a contact which has to handle with the unique ID.
 * 
 * @author mapfap - Sarun Wongtanakarn
 *
 */
public class ContactFactory {
	
	private static ContactFactory instance = null;
	
	private static AtomicLong nextId = new AtomicLong(1000L);
	
	private ContactFactory() {
		// singleton
	}
	
	public static ContactFactory getInstance() {
		if ( instance == null ) {
			instance = new ContactFactory();
		}
		return instance;
	}
	
	/**
	 * Create a contact with specified information.
	 * @param title contact's nickname or title.
	 * @param name contact's full name.
	 * @param email contact's email address.
	 * @param phoneNumber contact's telephone number.
	 * @return the contact the created.
	 */
	public Contact createContact(String title, String name, String email, String phoneNumber ) {
		return new Contact( getUniqueId(), title, name, email, phoneNumber );
	}
	
	/**
	 * Get a unique contact ID.
	 * @return unique id not in persistent storage
	 */
	private synchronized long getUniqueId() {
		long id = nextId.getAndAdd( 1L );
		while( id < Long.MAX_VALUE ) {	
			
			// TODO: IT'S COUPLING !!!
			if ( DaoFactory.getInstance().getContactDao().find( id ) == null ) {
				return id;
			}
			id = nextId.getAndAdd(1L);
		}
		return id; // this should never happen
	}
}
