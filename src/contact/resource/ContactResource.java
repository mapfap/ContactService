package contact.resource;
import java.util.List;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
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
	@GET 
	@Path("/")
	@Produces( MediaType.APPLICATION_XML )
	public Response listAllContact() {
//		return "This is contact [id=" + id + "]\n";
		Contact contact = ContactFactory.getInstance().createContact("mapfap", "Sarun Wongtanakarn", "admin@mapfap.com", "000000000");

		Contact contact2 = ContactFactory.getInstance().createContact("aatrox", "Aatrox", "aatrox@mapfap.com", "11111111");

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
	
//	// GET /contacts/?q=
//	@GET 
//	@Path("{q}")
//	@Produces(MediaType.TEXT_PLAIN)
//	public String searchContact( @PathParam String q ) {
//		return "This is contact [id=" + id + "]\n";
//	}

	
	
	/**
	 * 
	 * @param id id derived from path parameter
	 * @param reader body of the request
	 */
	@PUT
	@Path("{id}")
	public void updateContact( @PathParam("id") String id, Reader reader ) {
		BufferedReader buff = new BufferedReader(reader);
		try {
			String body = buff.readLine().trim();
			
			System.out.printf( "Got %s is %s\n", id, body );
		} catch (IOException | NullPointerException ioe) {
			//return Response.serverError().build();
		} finally {
			try { buff.close(); } catch ( IOException e ) { /* ignore */ }
		}
	}
}
