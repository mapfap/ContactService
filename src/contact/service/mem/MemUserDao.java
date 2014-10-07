package contact.service.mem;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

import contact.entity.Contact;
import contact.entity.User;
import contact.service.ContactDao;
import contact.service.UserDao;

/**
 * 
 * Memory-Based Data Access Object of Contact entity.
 * Provide CRUD operations.
 * 
 * @author mapfap - Sarun Wongtanakarn
 *
 */
public class MemUserDao implements UserDao {

	private Map<Long, User> users;
	
	@Context
	UriInfo uriInfo;
	
	private AtomicLong nextId;

	public MemUserDao() {
		nextId = new AtomicLong( 1000L );
		users = new ConcurrentHashMap<Long, User>();
		createTestUsers();
	}
	
	/**
	 * Create several sample contacts.
	 */
	private void createTestUsers() {
		User user1 = new User( "mapfap", "thisispassword" );
		save( user1 );
	}

	/**
	 * @see UserDao#find(long)
	 */
	public User find( long id ) {
		return users.get( id );
	}

	/**
	 * @see ContactDao#delete(long)
	 */
	public boolean delete( long id ) {
		users.remove( id );
		return true;
	}

	/**
	 * @see ContactDao#save(Contact)
	 */
	public boolean save( User user ) {
		if ( user.getId() == 0 ) {
			user.setId( getUniqueId() );
		}
		users.put( user.getId(), user );
		return true;
	}

	/**
	 * @see ContactDao#update(Contact)
	 */
	public boolean update( User user ) {
		if ( ! users.containsKey( user.getId() ) ) {
			return false;
		}

		users.put( user.getId(), user );
		return true;
	}
	
	/**
	 * Get a unique contact ID.
	 * @return unique id not in persistent storage
	 */
	private synchronized long getUniqueId() {
		return nextId.getAndAdd( 1L );
	}

	@Override
	public User findByUsername( String username ) {
		List<User> list = new ArrayList<User>( users.values() );
		for ( User user : list ) {
			if ( user.getUsername() == null ) {
			} else if ( user.getUsername().equals( username ) ) {
				return user;
			}
		}
		return null;
	}

}
