package contact.resource;
import java.util.List;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;

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

import contact.entity.Contact;
import contact.service.ContactFactory;
import jersey.repackaged.com.google.common.collect.Lists;


@Path("/contacts")
public class ContactResource {

	@Context
	UriInfo uriInfo;

	public ContactResource() {

	}

	// GET /contacts
	// GET /contacts?q=querystr

	@GET 
	@Path("/")
	@Produces( MediaType.APPLICATION_XML )
	public Response listAllContact( @QueryParam("q") String queryString ) {

		if ( queryString == null) {
			queryString = "no query params";
		}

		Contact contact = ContactFactory.getInstance().createContact("mapfap", "Sarun Wongtanakarn", "admin@mapfap.com", "000000000");
		Contact contact2 = ContactFactory.getInstance().createContact(queryString, "Aatrox", "aatrox@mapfap.com", "11111111");

		List<Contact> x = new ArrayList<Contact>();
		x.add(contact);
		x.add(contact2);

		GenericEntity<List<Contact>> contacts = new GenericEntity<List<Contact>>(Lists.newArrayList(x)) {};

		return Response.ok(contacts).build();
	}

	// GET /contacts/{id}
	@GET 
	@Path("{id}")
	@Produces( MediaType.APPLICATION_XML )
	public Response getContact( @PathParam("id") long id ) {
		//		return "This is contact [id=" + id + "]\n";
		Contact contact = ContactFactory.getInstance().createContact("mapfap", "Sarun Wongtanakarn", "admin@mapfap.com", "000000000");

		return Response.ok(contact).build();
	}
	
	// POST /contacts
	@POST 
	@Path("/")
	@Produces( MediaType.APPLICATION_XML )
	public Response createContact( @FormParam("title") String title, @FormParam("name") String name, @FormParam("name") String email, @FormParam("name") String phoneNumber ) {
		
		
		
		return Response.ok( ContactFactory.getInstance().createContact(name, title, "aatrox@mapfap.com", "11111111") ).build();
	}

	// PUT /contacts/{id}
	@PUT 
	@Path("{id}")
	@Produces( MediaType.APPLICATION_XML )
	public Response updateContact( @FormParam("title") String title, @FormParam("name") String name, @FormParam("name") String email, @FormParam("name") String phoneNumber ) {
		
		return Response.ok( ContactFactory.getInstance().createContact(name, title, "aatrox@mapfap.com", "11111111") ).build();
		
	}

	// DELETE /contacts/{id}
	@DELETE
	@Path("{id}")
	@Produces( MediaType.APPLICATION_XML )
	public Response deleteContact( Reader reader ) {
		return Response.ok( ContactFactory.getInstance().createContact("del", "Aatrox", "aatrox@mapfap.com", "11111111") ).build();
		
	}
}
