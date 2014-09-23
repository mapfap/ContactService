package contact.service.mem;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import contact.entity.Contact;
import contact.service.ContactDao;

/**
 * 
 * Memory-Based Data Access Object of Contact entity.
 * Provide CRUD operations.
 * 
 * @author mapfap - Sarun Wongtanakarn
 *
 */
public class MemContactDao implements ContactDao {

	private Map<Long, Contact> contacts;
	
	@Context
	UriInfo uriInfo;
	
	private AtomicLong nextId;

	public MemContactDao() {
		nextId = new AtomicLong( 1000L );
		contacts = new ConcurrentHashMap<Long, Contact>();
		loadContacts();
//		createTestContacts();
	}
	
	private void loadContacts() {
		try {
			Contacts importContacts = new Contacts();
			JAXBContext context = JAXBContext.newInstance( Contacts.class ) ;
			File inputFile = new File( MemDaoFactory.EXTERNAL_FILE_PATH );
			Unmarshaller unmarshaller = context.createUnmarshaller();	
			importContacts = (Contacts) unmarshaller.unmarshal( inputFile );
			for ( Contact contact : importContacts.getContacts() ) {
				contacts.put( contact.getId(), contact );
			}
		} catch ( JAXBException e ) {
			e.printStackTrace();
		}
	}

	/**
	 * Create several sample contacts.
	 */
	private void createTestContacts() {
		Contact test1 = new Contact( "Geeky", "John Doe", "john@mymail.com", "010010010100" );
		Contact test2 = new Contact( "Map", "Sarun Wongtanakarn", "mail@mapfap.com", "0110000000" );
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
		if ( contact.getId() == 0 ) {
			contact.setId( getUniqueId() );
		}
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
	
	/**
	 * Get a unique contact ID.
	 * @return unique id not in persistent storage
	 */
	private synchronized long getUniqueId() {
		return nextId.getAndAdd( 1L );
	}

}
