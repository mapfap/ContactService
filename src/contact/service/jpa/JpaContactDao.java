package contact.service.jpa;

import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

import contact.entity.Contact;
import contact.service.ContactDao;

/**
 * 
 * JPA Data Access Object of Contact entity.
 * Provide CRUD operations.
 * 
 * @author mapfap - Sarun Wongtanakarn
 *
 */
public class JpaContactDao implements ContactDao {

	private final EntityManager em;
	
	@Context
	UriInfo uriInfo;

	/**
	 * Construct a new JpaContactDao with injected EntityManager for using.
	 * @param em EntityManager for accessing JPA services.
	 */
	public JpaContactDao(EntityManager em) {
		this.em = em;
	}
	
	/**
	 * @see ContactDao#findByTitle(String)
	 */
	@SuppressWarnings("unchecked")
	public List<Contact> findByTitle( String title ) {
		Query query = em.createQuery("SELECT c FROM Contact c WHERE LOWER(c.title) LIKE :title");
		query.setParameter( "title", "%" + title.toLowerCase() + "%" );
		List<Contact> contacts = query.getResultList();
		return Collections.unmodifiableList( contacts );
	}

	/**
	 * @see ContactDao#find(long)
	 */
	public Contact find( long id ) {
		return em.find(Contact.class, id);
	}

	/**
	 * @see ContactDao#findAll()
	 */
	@SuppressWarnings("unchecked")
	public List<Contact> findAll() {
		Query query = em.createQuery("SELECT c FROM Contact c");
		List<Contact> contacts = query.getResultList();
		return Collections.unmodifiableList( contacts );
	}

	/**
	 * @see ContactDao#delete(long)
	 */
	public boolean delete( long id ) {
		Contact contact = find( id );
		em.getTransaction().begin();
		em.remove( contact );
		em.getTransaction().commit();
		return true;
	}

	/**
	 * @see ContactDao#save(Contact)
	 */
	public boolean save( Contact contact ) {
		if ( contact == null ) {
			throw new IllegalArgumentException("Can't save a null contact");
		}
		EntityTransaction tx = em.getTransaction();
		try {
			tx.begin();
			em.persist( contact );
			tx.commit();
			return true;
		} catch ( EntityExistsException ex ) {
			Logger.getLogger( this.getClass().getName() ).warning( ex.getMessage() );
			if ( tx.isActive() ) {
				try { 
					tx.rollback();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return false;
		}
	}

	/**
	 * @see ContactDao#update(Contact)
	 */
	public boolean update( Contact contact ) {
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		em.find( Contact.class, contact.getId() );
		em.merge( contact );
		tx.commit();
		return true;
	}

	/**
	 * @see contact.service.ContactDao#removeAll()
	 */
	@Override
	public void removeAll() {
		for ( Contact contact : findAll() ) {
			delete( contact.getId() );
		}
	}

}
