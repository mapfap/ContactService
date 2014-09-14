package contact.entity;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType (XmlAccessType.FIELD)
public class Contact implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@XmlAttribute
	private long id;
	private String title;
	private String name;
	private String email;
	private String phoneNumber;
	
	public Contact() {
		
	}
	
	public Contact(long id, String title, String name, String email, String phoneNumber) {
		this.id = id;
		this.title = title;
		this.name = name;
		this.email = email;
		this.phoneNumber = phoneNumber;
	}
	
	public long getId() {
		return id;
	}
	
	public void setId( long id ) {
		this.id = id;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle( String title ) {
		this.title = title;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName( String name ) {
		this.name = name;
	}
	
	public String getEmail() {
		return email;
	}

	public void setEmail( String email ) {
		this.email = email;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}
	
	public void setPhoneNumber( String phoneNumber ) {
		this.phoneNumber = phoneNumber;
	}
	
	@Override
	public String toString() {
		return String.format("[%ld] %s.", id, title);
	}
	
	/** Two contacts are equal if they have the same id,
	 * even if other attributes differ.
	 * @param other another contact to compare to this one.
	 */
	public boolean equals( Object other ) {
		if ( other == null || other.getClass() != this.getClass() ) {
			return false;
		}
		Contact contact = (Contact) other;
		return contact.getId() == this.getId();
	}

	
}
