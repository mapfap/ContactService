package contact.resource;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.JAXBElement;
import contact.entity.Contact;
import contact.entity.Contacts;
import contact.service.ContactDao;
import contact.service.mem.MemDaoFactory;

/**
 * 
 * ContactResource provides RESTful web resources using JAX-RS
 * provide an access to Contact entity.
 * 
 * @author mapfap - Sarun Wongtanakarn
 *
 */
@Singleton
@Path("/contacts")
public class ContactResource {

	private static final Response NOT_FOUND_RESPOND = Response.status( Response.Status.NOT_FOUND ).build();;

	@Context
	UriInfo uriInfo;

	private ContactDao contactDao;

	public ContactResource() {
		contactDao = MemDaoFactory.getInstance().getContactDao();
	}
	
	/**
	 * Check whether ID is not existed in DAO.
	 * @param id of contact to be checked.
	 * @return true if ID is not existed; false otherwise.
	 */
	private boolean idNotExisted( long id ) {
		return contactDao.find( id ) == null;
	}

	/**
	 * List all the contacts.
	 * Request: GET /contacts or GET /contacts?q=querystr
	 * @param queryString string for searching.
	 * @return HTTP response 200 if success.
	 */
	@GET 
	@Path("/")
	@Produces({ MediaType.APPLICATION_XML })
	public Response listAllContact( @QueryParam("title") String title ) {

		List<Contact> tempContactList = null;

		if ( title == null) {
			tempContactList = contactDao.findAll();
		} else {
			tempContactList = contactDao.findByTitle( title );
		}
		
		Contacts contacts = new Contacts();
		contacts.setContacts( tempContactList );
		
		return Response.ok( contacts ).build();
	}

	/**
	 * Get the contact with specified ID.
	 * Request: GET /contacts/{id}
	 * @param id ID of contact to be acquired.
	 * @return HTTP response 200 if success.
	 */
	@GET 
	@Path("{id}")
	@Produces({ MediaType.APPLICATION_XML })
	public Response getContact( @PathParam("id") long id, @Context Request request ) {
		if ( idNotExisted( id ) ) {
			return NOT_FOUND_RESPOND;
		}
		Contact contact = contactDao.find( id );
		CacheControl cc = new CacheControl();
		cc.setMaxAge( 86400 );
		Response.ResponseBuilder rb = null;
		EntityTag etag = new EntityTag( contact.getMD5() );
		rb = request.evaluatePreconditions( etag );
		if ( rb != null ) {
		  return rb.cacheControl( cc ).tag( etag ).build();
		}
		rb = Response.ok( contact ).cacheControl( cc ).tag( etag );
		return rb.build();
	}
	
	/**
	 * Create new contact with given XML data.
	 * Request: POST /contacts (XML)
	 * @param element XML data.
	 * @param uriInfo info of current URI.
	 * @return HTTP response 200 if success.
	 */
	@POST 
	@Path("/")
	@Consumes({ MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_XML })
	public Response createContact( JAXBElement<Contact> element, @Context UriInfo uriInfo ) {
		Contact contact = element.getValue();
		
		// ID already existed.
		if ( ! idNotExisted( contact.getId() ) ) {
			return Response.status( Response.Status.CONFLICT ).build();
		}
			
		contactDao.save( contact );
		URI location = null;
		try {
			location = new URI( uriInfo.getAbsolutePath() + "/" + contact.getId() );
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return Response.created( location ).build();
	}

	/**
	 * Create new contact with given form data.
	 * Request: POST /contacts (FORM)
	 * @param title contact's nickname or title.
	 * @param name contact's full name.
	 * @param email contact's email address.
	 * @param phoneNumber contact's telephone number.
	 * @return HTTP response 200 if success.
	 */
	@POST 
	@Path("/")
	@Consumes({ "application/x-www-form-urlencoded" })
	@Produces({ MediaType.APPLICATION_XML })
	public Response createContactWithForm( @FormParam("title") String title, @FormParam("name") String name, @FormParam("name") String email, @FormParam("name") String phoneNumber ) {
		Contact contact = new Contact( title, name, email, phoneNumber );
		contactDao.save( contact );
		URI location = null;
		try {
			location = new URI( uriInfo.getAbsolutePath() + "/" + contact.getId() );
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return Response.created( location ).build();	
	}

	/**
	 * Update the contact with given XML data.
	 * Request: PUT /contacts/{id} (XML)
	 * @param element XML data.
	 * @param uriInfo info of current URI.
	 * @return HTTP response 200 if success.
	 */
	@PUT 
	@Path("{id}")
	@Consumes({ MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_XML })
	public Response updateContact( @PathParam("id") long id, JAXBElement<Contact> element, @Context UriInfo uriInfo ) {
		if ( idNotExisted( id ) ) {
			return NOT_FOUND_RESPOND;
		}
		Contact contact = element.getValue();
		
		// If should respond with BAD_REQUEST if there's also ID in the xml data
		// and it's not the same with the path parameter.
		if ( contact.getId() != 0 && contact.getId() != id ) {
			return Response.status( Response.Status.BAD_REQUEST ).build();
		}
		
		contact.setId( id );
		contactDao.update( contact );
		return Response.ok().build();
	}

	/**
	 * Update the contact with given form data.
	 * Request: PUT /contacts/{id} (FORM)
	 * @param title title contact's nickname or title.
	 * @param name contact's full name.
	 * @param email contact's email address.
	 * @param phoneNumber phoneNumber contact's telephone number.
	 * @return HTTP response 200 if success.
	 */
	@PUT 
	@Path("{id}")
	@Consumes({ "application/x-www-form-urlencoded" })
	@Produces({ MediaType.APPLICATION_XML })
	public Response updateContact( @PathParam("id") long id, @FormParam("title") String title, @FormParam("name") String name, @FormParam("name") String email, @FormParam("name") String phoneNumber ) {
		if ( idNotExisted( id ) ) {
			return NOT_FOUND_RESPOND;
		}
		Contact contact = new Contact( id, title, name, email, phoneNumber );
		contactDao.update( contact );
		return Response.ok().build();
	}

	/**
	 * Delete the contact with given ID.
	 * Request: DELETE /contacts/{id}
	 * 
	 * NOTE: DELETE is idempotent, no need to check ID existed. 
	 * 
	 * @param id specifies ID of contact to be deleted.
	 * @return HTTP response 200 if success.
	 */
	@DELETE
	@Path("{id}")
	public Response deleteContact( @PathParam("id") long id ) {
		
		// Idempotent is about the effect of the request, not about the response code that you get
		if ( idNotExisted( id ) ) {
			return NOT_FOUND_RESPOND;
		}
		
		contactDao.delete( id );
		return Response.ok().build();
	}
	
}
