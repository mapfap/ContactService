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
	
	private AtomicLong nextId;
	
	private ContactFactory() {
		// singleton
		nextId = new AtomicLong( 1000L );
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
	public Contact createContact( String title, String name, String email, String phoneNumber ) {
		return new Contact( getUniqueId(), title, name, email, phoneNumber );
	}
	/**
	 * Create a contact with existed contact.
	 * This will re-assign ID if its ID is 0 (not assigned yet). 
	 * @param contact
	 * @return
	 */
	public Contact createContact( Contact contact ) {
		if ( contact.getId() == 0 ) {			
			contact.setId( ContactFactory.getInstance().getUniqueId() );
		}
		return contact;
	}
	
	/**
	 * Get a unique contact ID.
	 * @return unique id not in persistent storage
	 */
	private synchronized long getUniqueId() {
		return nextId.getAndAdd( 1L );
	}

}
