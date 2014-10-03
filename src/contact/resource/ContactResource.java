package contact.resource;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
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

import org.eclipse.jetty.http.HttpHeader;

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

	private static final Response NOT_FOUND_RESPOND = Response.status( Response.Status.NOT_FOUND ).build();
	private static final Response BAD_REQUEST_RESPOND = Response.status( Response.Status.BAD_REQUEST ).build();
	private static final Response NOT_MODIFIED_RESPOND = Response.status( Response.Status.NOT_MODIFIED ).build();
	private static final Response CONFLICT_RESPOND = Response.status( Response.Status.CONFLICT ).build();
	private static final Response PRECONDITION_FAILED_RESPONSE = Response.status( Response.Status.PRECONDITION_FAILED ).build();

	@Context
	UriInfo uriInfo;

	private ContactDao contactDao;

	public ContactResource() {
		contactDao = MemDaoFactory.getInstance().getContactDao();
	}

	/**
	 * Check if there's an attempt to access non-existing entity.
	 * @param id of entity to be checked.
	 * @return appropriate response.
	 */
	private Response checkIdNotFound( long id ) {
		if ( isIdNotExisted( id ) ) {
			return NOT_FOUND_RESPOND;
		}
		return null;
	}

	/**
	 * Check whether ID is not existed in DAO.
	 * @param id of contact to be checked.
	 * @return true if ID is not existed; false otherwise.
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
	@Path("/")
	@Produces({ MediaType.APPLICATION_XML })
	public Response listAllContact( @HeaderParam("If-Match") String ifMatch, @HeaderParam("If-None-Match") String ifNoneMatch, @QueryParam("title") String title, @Context Request request ) {

		Response response = null;

		List<Contact> tempContactList = null;

		if ( title == null) {
			tempContactList = contactDao.findAll();
		} else {
			tempContactList = contactDao.findByTitle( title );
		}

		Contacts contacts = new Contacts();
		contacts.setContacts( tempContactList );

		
		// NOTE:
		// SHOULD THE LIST USE ETAG?
		// IT'S GOOD IF LIST IS RARELY CHANGED.
//		response = checkPrecondition( ifMatch, ifNoneMatch, contacts.getMD5(), false );
//		if ( response != null ) {
//			return response;
//		}
//		CacheControl cc = new CacheControl();
//		cc.setMaxAge( -1 );
//		EntityTag etag = new EntityTag( contacts.getMD5() );
//		return Response.ok( contacts ).cacheControl( cc ).tag( etag ).build();
		
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
	public Response getContact( @HeaderParam("If-Match") String ifMatch, @HeaderParam("If-None-Match") String ifNoneMatch, @PathParam("id") long id, @Context Request request ) {
		Response response = null;

		response = checkIdNotFound( id );
		if ( response != null ) {
			return response;
		}

		Contact contact = contactDao.find( id );

		response = checkPrecondition( ifMatch, ifNoneMatch, contact.getMD5(), false );
		if ( response != null ) {
			return response;
		}

		CacheControl cc = new CacheControl();
		cc.setMaxAge( -1 );
		EntityTag etag = new EntityTag( contact.getMD5() );
		Response.ResponseBuilder rb = request.evaluatePreconditions( etag );
		if ( rb != null ) {
			return rb.cacheControl( cc ).tag( etag ).build();
		}
		return Response.ok( contact ).cacheControl( cc ).tag( etag ).build();
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
	public Response createContact( JAXBElement<Contact> element, @Context UriInfo uriInfo, @Context Request request ) {
		Contact contact = element.getValue();

		// ID already existed.
		if ( ! isIdNotExisted( contact.getId() ) ) {
			return CONFLICT_RESPOND;
		}

		// After this, contact's ID will be assigned.
		contactDao.save( contact );

		CacheControl cc = new CacheControl();
		cc.setMaxAge( -1 );
		EntityTag etag = new EntityTag( contact.getMD5() );

		URI location = null;
		try {
			location = new URI( uriInfo.getAbsolutePath() + "/" + contact.getId() );
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return Response.created( location ).cacheControl( cc ).tag( etag ).build();
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
	public Response updateContact( @HeaderParam("If-Match") String ifMatch, @HeaderParam("If-None-Match") String ifNoneMatch, @PathParam("id") long id, JAXBElement<Contact> element, @Context UriInfo uriInfo, @Context Request request ) {
		//		System.out.println("." + ifMatch + ".");
		//		System.out.println("." + ifNonMatch + ".");

		Response response = null;

		response = checkIdNotFound( id );
		if ( response != null ) {
			return response;
		}
		Contact contact = element.getValue();

		// If should respond with BAD_REQUEST if there's also ID in the xml data
		// and it's not the same with the path parameter.
		if ( contact.getId() != 0 && contact.getId() != id ) {
			return BAD_REQUEST_RESPOND;
		}

		contact.setId( id );
		
		response = checkPrecondition( ifMatch, ifNoneMatch, contact.getMD5(), false );
		if ( response != null ) {
			return response;
		}
		
		contactDao.update( contact );

		CacheControl cc = new CacheControl();
		cc.setMaxAge( -1 );
		EntityTag etag = new EntityTag( contact.getMD5() );
		
		return Response.ok().cacheControl( cc ).tag( etag ).build();
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
	public Response deleteContact( @HeaderParam("If-Match") String ifMatch, @HeaderParam("If-None-Match") String ifNoneMatch, @PathParam("id") long id, @Context Request request ) {

		Response response = null;
		
		// Idempotent is about the effect of the request, not about the response code that you get
		response = checkIdNotFound( id );
		if ( response != null ) {
			return response;
		}
		
		Contact contact = contactDao.find( id );
		response = checkPrecondition( ifMatch, ifNoneMatch, contact.getMD5(), false );
		if ( response != null ) {
			return response;
		}

		contactDao.delete( id );
		return Response.ok().build();
	}
	
	/**
	 * Return appropriate response, if it's not finished yet then return null.
	 * @param ifMatch If-Match HTTP header.
	 * @param ifNoneMatch If-None-Match HTTP header.
	 * @param etag ETag for checking.
	 * @param isGetMethod if this method called from GET so, request will change to NOT MODIFIED instead of PRECONDITION FAILED. 
	 * @return appropriate response.
	 */
	private Response checkPrecondition( String ifMatch, String ifNoneMatch, String etag, boolean isGetMethod ) {
		if ( ifMatch != null && ifNoneMatch != null ) {
			return BAD_REQUEST_RESPOND;
		}

		if ( ifMatch != null ) {
			if ( ifMatch.equals( etag ) ) {				
				return null;
			} else {
				return PRECONDITION_FAILED_RESPONSE;
			}
		}

		if ( ifNoneMatch != null ) {
			if ( ifNoneMatch.equals( etag ) ) {
				if ( isGetMethod ) {
					return NOT_MODIFIED_RESPOND;
				} else {					
					return PRECONDITION_FAILED_RESPONSE;
				}
			} else {
				return null;
			}
		}

		// Both headers are null.
		return null;
	}

}
