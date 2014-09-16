package contact.resource;
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
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.JAXBElement;

import jersey.repackaged.com.google.common.collect.Lists;
import contact.entity.Contact;
import contact.service.ContactDao;
import contact.service.ContactFactory;
import contact.service.DaoFactory;

@Singleton
@Path("/contacts")
public class ContactResource {


	@Context
	UriInfo uriInfo;

	private ContactFactory contactFactory;
	private ContactDao contactDao;

	public ContactResource() {
		contactFactory = ContactFactory.getInstance();
		contactDao = DaoFactory.getInstance().getContactDao();
	}

	// GET /contacts
	// GET /contacts?q=querystr
	@GET 
	@Path("/")
	@Produces({ MediaType.APPLICATION_XML })
	public Response listAllContact( @QueryParam("q") String queryString ) {

		List<Contact> tempContactList = null;

		if ( queryString == null) {
			tempContactList = contactDao.findAll();
		} else {
			tempContactList = contactDao.search(queryString);
		}

		GenericEntity<List<Contact>> contacts = new GenericEntity<List<Contact>>(Lists.newArrayList(tempContactList)) {};

		return Response.ok(contacts).build();
	}

	// GET /contacts/{id}
	@GET 
	@Path("{id}")
	@Produces({ MediaType.APPLICATION_XML })
	public Response getContact( @PathParam("id") long id ) {
		if ( contactDao.find(id) == null ) {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
		return Response.ok( contactDao.find(id) ).build();
	}

	// POST /contacts (XML)
	@POST 
	@Path("/")
	@Consumes({ MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_XML })
	public Response createContact( JAXBElement<Contact> element, @Context UriInfo uriInfo ) {
		Contact contact = contactFactory.createContact( element.getValue() );
		contactDao.save( contact );
		return Response.ok( contact ).header("Location", uriInfo.getAbsolutePath() + "/" + contact.getId() ).build();
	}

	// POST /contacts (FORM)
	@POST 
	@Path("/")
	@Consumes({ "application/x-www-form-urlencoded" })
	@Produces({ MediaType.APPLICATION_XML })
	public Response createContactWithForm( @FormParam("title") String title, @FormParam("name") String name, @FormParam("name") String email, @FormParam("name") String phoneNumber ) {
		Contact contact = contactFactory.createContact(title, name, email, phoneNumber);
		contactDao.save( contact );
		return Response.ok( contact ).header("Location", uriInfo.getAbsolutePath() + "/" + contact.getId() ).build();
	}

	// PUT /contacts/{id}
	@PUT 
	@Path("{id}")
	@Produces({ MediaType.APPLICATION_XML })
	public Response updateContact( @FormParam("title") String title, @FormParam("name") String name, @FormParam("name") String email, @FormParam("name") String phoneNumber ) {
		return Response.ok( contactDao.update(contactFactory.createContact( title, name, email, phoneNumber )) ).build();
	}

	// DELETE /contacts/{id}
	@DELETE
	@Path("{id}")
	public Response deleteContact( @PathParam("id") long id ) {
		contactDao.delete( id );
		return Response.ok().build();
	}
}
