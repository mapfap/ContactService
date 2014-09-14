package contact.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import contact.entity.Contact;

/**
 * 
 * Data Access Object of Contact entity.
 * Provide crud operations.
 * 
 * @author mapfap - Sarun Wongtanakarn
 *
 */
public class ContactDao {
	
	private Map<Long, Contact> contacts;
	
	public ContactDao() {
		contacts = new ConcurrentHashMap<Long, Contact>();
	}
	
	/**
	 * Get the contact with matching ID.
	 * @param id ID of contact for finding.
	 * @return the contact with exactly given ID.
	 */
	public Contact find( long id ) {
		return contacts.get( id );
	}
	
	/**
	 * Get the list of all contacts.
	 * @return the list of all contacts.
	 */
	public List<Contact> findAll() {
		return new ArrayList<Contact>( contacts.values() );
	}
	
	/**
	 * Delete the contact with matching ID.
	 * @param id of contact to be deleted.
	 */
	public void delete( long id ) {
		contacts.remove( id );
	}
	
	/**
	 * Save a new contact.
	 * If ID is existed, Contact will be replaced.
	 * @param contact Contact to be saved.
	 */
	public boolean save( Contact contact ) {
		contacts.put( contact.getId(), contact );
		return true;
	}
	
	/**
	 * Update a contact.
	 * @param contact Contact to be updated.
	 */
	public boolean update( Contact contact ) {
		if ( ! contacts.containsKey( contact.getId() ) ) {
			return false;
		}
		
		contacts.put( contact.getId(), contact );
		return true;
	}
	
}
