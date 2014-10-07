package contact.service;

import contact.entity.User;
/**
 * Interface for Data Access Object of user.
 * Define the CRUD operation.
 * 
 * @author mapfap - Sarun Wongtanakarn
 */
public interface UserDao {

	/**
	 * Get the user with matching ID.
	 * @param id ID of user for finding.
	 * @return the user with exactly given ID.
	 */
	public User find( long id );

	/**
	 * Delete the user with matching ID.
	 * @param id of user to be deleted.
	 * @return true if success.
	 */
	public boolean delete( long id );

	/**
	 * Save a new user.
	 * If ID is existed, User will be replaced.
	 * @param user User to be saved.
	 * @return true if success.
	 */
	public boolean save( User contact );

	/**
	 * Update a user.
	 * @param user User to be updated.
	 * @return true if success.
	 */
	public boolean update( User update );

	public User findByUsername(String username);
	
}