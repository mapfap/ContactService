package contact.service.jpa;

import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;

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
	@Override
	public List<Contact> findByTitle( String title ) {
		Query query = em.createQuery("SELECT c FROM Contact c WHERE LOWER(c.title) LIKE :title");
		query.setParameter( "title", "%" + title.toLowerCase() + "%" );
		List<Contact> contacts = query.getResultList();
		return Collections.unmodifiableList( contacts );
	}

	/**
	 * @see ContactDao#find(long)
	 */
	@Override
	public Contact find( long id ) {
		return em.find(Contact.class, id);
	}

	/**
	 * @see ContactDao#findAll()
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<Contact> findAll() {
		Query query = em.createQuery("SELECT c FROM Contact c");
		List<Contact> contacts = query.getResultList();
		return Collections.unmodifiableList( contacts );
	}

	/**
	 * @see ContactDao#delete(long)
	 */
	@Override
	public boolean delete( long id ) {
		EntityTransaction tx = em.getTransaction();
		Contact contact = find( id );
		try {
			tx.begin();
			em.remove( contact );
			tx.commit();
			return true;
		} catch ( EntityExistsException ex ) {
			handleDatabaseError( tx, ex );
			return false;
		}
	}
	/**
	 * @see ContactDao#save(Contact)
	 */
	@Override
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
			handleDatabaseError( tx, ex );
			return false;
		}
	}

	/**
	 * @see ContactDao#update(Contact)
	 */
	@Override
	public boolean update( Contact contact ) {
		Contact oldContact = find( contact.getId() );
		if ( oldContact == null ) {
			return false;
		}
		
		EntityTransaction tx = em.getTransaction();
		try {
			tx.begin();
			em.find( Contact.class, contact.getId() );
			em.merge( contact );
			tx.commit();	
			return true;
		} catch ( EntityExistsException ex ) {
			handleDatabaseError( tx, ex );
			return false;
		}
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

	/**
	 * Handle error from database, try to rollback it if possible.
	 * @param tx current EntityTransaction.
	 * @param ex EntityExistsException that occurs.
	 */
	private void handleDatabaseError( EntityTransaction tx, EntityExistsException ex ){
		Logger.getLogger( this.getClass().getName() ).warning( ex.getMessage() );
		if ( tx.isActive() ) {
			try { 
				tx.rollback();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
