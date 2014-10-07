package contact.resource;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
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
import contact.entity.User;
import contact.service.ContactDao;
import contact.service.ContactDaoFactory;
import contact.service.UserDao;
import contact.service.UserDaoFactory;

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

	private static final Response NOT_FOUND_RESPONSE = Response.status( Response.Status.NOT_FOUND ).build();
	private static final Response BAD_REQUEST_RESPONSE = Response.status( Response.Status.BAD_REQUEST ).build();
	private static final Response CONFLICT_RESPONSE = Response.status( Response.Status.CONFLICT ).build();
	private static final Response NOT_ACCEPTABLE_RESPONSE = Response.status( Response.Status.NOT_ACCEPTABLE ).build();

	@Context
	UriInfo uriInfo;

	private ContactDao contactDao;
	private CacheControl cacheControl;
	private UserDao userDao;

	public ContactResource() {
		contactDao = ContactDaoFactory.getInstance().getContactDao();
		userDao = UserDaoFactory.getInstance().getUserDao();
		cacheControl = new CacheControl();
		cacheControl.setMaxAge( 86400 );
	}

	/**
	 * Check if there's an attempt to access non-existing entity.
	 * @param id of entity to be checked.
	 * @return appropriate response.
	 */
	private Response checkIdNotFound( long id ) {
		if ( isIdNotExisted( id ) ) {
			return NOT_FOUND_RESPONSE;
		}
		return null;
	}

	/**
	 * Check whether ID does not existed in DAO.
	 * @param id of contact to be checked.
	 * @return true if ID does not existed; false otherwise.
	 */
	private boolean isIdNotExisted( long id ) {
		return contactDao.find( id ) == null;
	}

	/**
	 * List all the contacts.
	 * Request: GET /contacts or GET /contacts?q=querystr
	 * @param queryString string for searching.
	 * @return HTTP response 200 if success.
	 */
	@GET 
	@Path("/{username}")
	@Produces({ MediaType.APPLICATION_XML })
	public Response listAllContact( @HeaderParam("token") String token, @PathParam("username") String username, @QueryParam("title") String title, @Context Request request ) {
		List<Contact> tempContactList = null;
		
		User user = userDao.findByUsername( username );
		// TODO: check token
		
		if ( title == null) {
			tempContactList = contactDao.findAll( user.getId() );
		} else {
			tempContactList = contactDao.findByTitle( user.getId(), title );
		}

		Contacts contacts = new Contacts();
		contacts.setContacts( tempContactList );
		
		return Response.ok( contacts ).build();
	}

//	/**
//	 * Get the contact with specified ID.
//	 * Request: GET /contacts/{id}
//	 * @param id ID of contact to be acquired.
//	 * @return HTTP response 200 if success.
//	 */
//	@GET 
//	@Path("{id}")
//	@Produces({ MediaType.APPLICATION_XML })
//	public Response getContact( @PathParam("id") long id, @Context Request request ) {
//		Response response = null;
//
//		response = checkIdNotFound( id );
//		if ( response != null ) {
//			return response;
//		}
//
//		Contact contact = contactDao.find( id );
//
//		EntityTag etag = new EntityTag( contact.getMD5() );
//		Response.ResponseBuilder builder = request.evaluatePreconditions( etag );
//		if ( builder != null ) {
//			return builder.cacheControl( cacheControl ).tag( etag ).build();
//		}
//		
//		return Response.ok( contact ).cacheControl( cacheControl ).tag( etag ).build();
//	}
//
//	/**
//	 * Create new contact with given XML data.
//	 * Request: POST /contacts (XML)
//	 * @param element XML data.
//	 * @param uriInfo info of current URI.
//	 * @return HTTP response 200 if success.
//	 */
//	@POST 
//	@Path("/")
//	@Consumes({ MediaType.APPLICATION_XML })
//	@Produces({ MediaType.APPLICATION_XML })
//	public Response createContact( JAXBElement<Contact> element, @Context UriInfo uriInfo, @Context Request request ) {
//		Contact contact = element.getValue();
//
//		if ( ! isIdNotExisted( contact.getId() ) ) {
//			return CONFLICT_RESPONSE;
//		}
//
//		// After this, contact's ID will be assigned.
//		contactDao.save( contact );
//		
//		EntityTag etag = new EntityTag( contact.getMD5() );
//		URI location = null;
//		try {
//			location = new URI( uriInfo.getAbsolutePath() + "/" + contact.getId() );
//		} catch ( URISyntaxException e ) {
//			e.printStackTrace();
//		}
//		
//		return Response.created( location ).cacheControl( cacheControl ).tag( etag ).build();
//	}
//
//	/**
//	 * Update the contact with given XML data.
//	 * Request: PUT /contacts/{id} (XML)
//	 * @param element XML data.
//	 * @param uriInfo info of current URI.
//	 * @return HTTP response 200 if success.
//	 */
//	@PUT 
//	@Path("{id}")
//	@Consumes({ MediaType.APPLICATION_XML })
//	@Produces({ MediaType.APPLICATION_XML })
//	public Response updateContact( @PathParam("id") long id, JAXBElement<Contact> element, @Context UriInfo uriInfo, @Context Request request ) {
//		Response response = null;
//
//		response = checkIdNotFound( id );
//		if ( response != null ) {
//			return response;
//		}
//		Contact contact = element.getValue();
//
//		// It should response with BAD_REQUEST if there's also ID in the xml data
//		// and it's not the same with the path parameter.
//		if ( contact.getId() != 0 && contact.getId() != id ) {
//			return BAD_REQUEST_RESPONSE;
//		}
//
//		contact.setId( id );
//		
//		contactDao.update( contact );
//
//		EntityTag etag = new EntityTag( contact.getMD5() );
//
//		Response.ResponseBuilder builder = request.evaluatePreconditions( etag );
//		if ( builder != null ) {
//			return builder.cacheControl( cacheControl ).tag( etag ).build();
//		}
//		
//		return Response.ok().cacheControl( cacheControl ).tag( etag ).build();
//	}
//
//	/**
//	 * Delete the contact with given ID.
//	 * Request: DELETE /contacts/{id}
//	 * 
//	 * NOTE: DELETE is idempotent, no need to check ID existed. 
//	 * 
//	 * @param id specifies ID of contact to be deleted.
//	 * @return HTTP response 200 if success.
//	 */
//	@DELETE
//	@Path("{id}")
//	public Response deleteContact( @PathParam("id") long id, @Context Request request ) {
//
//		Response response = null;
//		
//		// Idempotent is about the effect of the request, not about the response code that you get
//		response = checkIdNotFound( id );
//		if ( response != null ) {
//			return response;
//		}
//		
//		Contact contact = contactDao.find( id );
//		
//		EntityTag etag = new EntityTag( contact.getMD5() );
//
//		Response.ResponseBuilder builder = request.evaluatePreconditions( etag );
//		if ( builder != null ) {
//			return builder.cacheControl( cacheControl ).tag( etag ).build();
//		}
//		
//		if ( contactDao.delete( id ) ) {			
//			return Response.ok().build();
//		} else {
//			return NOT_ACCEPTABLE_RESPONSE;
//		}
//	}
	
}
