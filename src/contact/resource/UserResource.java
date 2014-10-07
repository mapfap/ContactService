package contact.resource;
import java.net.URI;
import java.net.URISyntaxException;

import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.JAXBElement;

import contact.entity.User;
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
@Path("/users")
public class UserResource {

	private static final Response CONFLICT_RESPONSE = Response.status( Response.Status.CONFLICT ).build();

	@Context
	UriInfo uriInfo;

	private UserDao userDao;
	private CacheControl cacheControl;

	public UserResource() {
		userDao = UserDaoFactory.getInstance().getUserDao();
		cacheControl = new CacheControl();
		cacheControl.setMaxAge( 86400 );
	}

	/**
	 * Check whether ID does not existed in DAO.
	 * @param id of user to be checked.
	 * @return true if ID does not existed; false otherwise.
	 */
	private boolean isIdNotExisted( long id ) {
		return userDao.find( id ) == null;
	}


	/**
	 * Create new user with given XML data.
	 * Request: POST /contacts (XML)
	 * @param element XML data.
	 * @param uriInfo info of current URI.
	 * @return HTTP response 200 if success.
	 */
	@POST 
	@Path("/")
	@Consumes({ MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_XML })
	public Response createUser( JAXBElement<User> element, @Context UriInfo uriInfo ) {
		User user = element.getValue();

		if ( ! isIdNotExisted( user.getId() ) ) {
			return CONFLICT_RESPONSE;
		}

		userDao.save( user );
		
		URI location = null;
		try {
			location = new URI( uriInfo.getAbsolutePath() + "/" + user.getId() );
		} catch ( URISyntaxException e ) {
			e.printStackTrace();
		}
		
		return Response.created( location ).cacheControl( cacheControl ).build();
	}
	
}
