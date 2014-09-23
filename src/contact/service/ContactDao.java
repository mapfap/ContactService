package contact.service;

import java.util.List;

import contact.entity.Contact;
/**
 * Interface for Data Access Object of contact.
 * Define the CRUD operation.
 * 
 * @author mapfap - Sarun Wongtanakarn
 */
public interface ContactDao {

	/**
	 * Get the contact with matching ID.
	 * @param id ID of contact for finding.
	 * @return the contact with exactly given ID.
	 */
	public Contact find( long id );

	/**
	 * Get the list of all contacts.
	 * @return the list of all contacts.
	 */
	public List<Contact> findAll();
	
	/**
	 * Find contact with matched substring of title.
	 * @param title part of title of contact for finding.
	 * @return list of contacts matched by title.
	 */
	public List<Contact> findByTitle( String title );

	/**
	 * Delete the contact with matching ID.
	 * @param id of contact to be deleted.
	 * @return true if success.
	 */
	public boolean delete( long id );

	/**
	 * Save a new contact.
	 * If ID is existed, Contact will be replaced.
	 * @param contact Contact to be saved.
	 * @return true if success.
	 */
	public boolean save( Contact contact );

	/**
	 * Update a contact.
	 * @param contact Contact to be updated.
	 * @return true if success.
	 */
	public boolean update( Contact update );
	
	/**
	 * Remove all contacts from the database.
	 */
	public void removeAll();

}