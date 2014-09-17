package contact.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

import contact.entity.Contact;

/**
 * 
 * Data Access Object of Contact entity.
 * Provide CRUD operations.
 * 
 * @author mapfap - Sarun Wongtanakarn
 *
 */
public class ContactDao {

	private Map<Long, Contact> contacts;
	
	@Context
	UriInfo uriInfo;

	public ContactDao() {
		contacts = new ConcurrentHashMap<Long, Contact>();
		createTestContacts();
	}
	
	/**
	 * Create several sample contacts.
	 */
	private void createTestContacts() {
		ContactFactory contactFactory = ContactFactory.getInstance();
		Contact test1 = contactFactory.createContact( "Geeky", "John Doe", "john@mymail.com", "010010010100" );
		Contact test2 = contactFactory.createContact( "Map", "Sarun Wongtanakarn", "mail@mapfap.com", "0110000000" );
		save(test1);
		save(test2);
	}

	/**
	 * Get the contact with matching ID.
	 * @param id ID of contact for finding.
	 * @return the contact with exactly given ID.
	 */
	public List<Contact> search( String searchQuery ) {
		List<Contact> matchContacts = new ArrayList<Contact>();
		for ( Contact contact : findAll() ) {
			if ( contact.getTitle() == null ) {
				/* Do Nothing, There is no title.*/
			} else if ( contact.getTitle().contains( searchQuery ) ) {
				matchContacts.add( contact );
			}
		}
		return sortByContactId( matchContacts );
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
		List<Contact> list = new ArrayList<Contact>( contacts.values() );
		return sortByContactId( list );
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
	
	/**
	 * Sort the given list of contacts.
	 * @param contacts list of contacts to be sorted.
	 * @return Sorted list of contacts.
	 */
	private List<Contact> sortByContactId( List<Contact> contacts ) {
		Collections.sort( contacts );
		return contacts;
	}

}
