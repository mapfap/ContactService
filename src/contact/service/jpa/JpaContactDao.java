package contact.service.jpa;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

import contact.entity.Contact;
import contact.service.ContactDao;
import contact.service.ContactFactory;

/**
 * 
 * JPA Data Access Object of Contact entity.
 * Provide CRUD operations.
 * 
 * @author mapfap - Sarun Wongtanakarn
 *
 */
public class JpaContactDao implements ContactDao {

	private Map<Long, Contact> contacts;
	
	@Context
	UriInfo uriInfo;

	public JpaContactDao() {
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
	 * @see ContactDao#findByTitle(String)
	 */
	public List<Contact> findByTitle( String title ) {
		title = title.toUpperCase();
		List<Contact> matchContacts = new ArrayList<Contact>();
		for ( Contact contact : findAll() ) {
			if ( contact.getTitle() == null ) {
				/* Do Nothing, There is no title.*/
			} else if ( contact.getTitle().toUpperCase().contains( title ) ) {
				matchContacts.add( contact );
			}
		}
		return sortByContactId( matchContacts );
	}

	/**
	 * @see ContactDao#find(long)
	 */
	public Contact find( long id ) {
		return contacts.get( id );
	}

	/**
	 * @see ContactDao#findAll()
	 */
	public List<Contact> findAll() {
		List<Contact> list = new ArrayList<Contact>( contacts.values() );
		return sortByContactId( list );
	}

	/**
	 * @see ContactDao#delete(long)
	 */
	public boolean delete( long id ) {
		contacts.remove( id );
		return true;
	}

	/**
	 * @see ContactDao#save(Contact)
	 */
	public boolean save( Contact contact ) {
		contacts.put( contact.getId(), contact );
		return true;
	}

	/**
	 * @see ContactDao#update(Contact)
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
