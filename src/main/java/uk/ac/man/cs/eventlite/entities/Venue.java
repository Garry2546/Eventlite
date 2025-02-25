package uk.ac.man.cs.eventlite.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.persistence.Id;

@Entity
@Table (name = "venues")
public class Venue {

	@Id
	@GeneratedValue
	private long id;

	@NotBlank(message="The venue name cannot be blank")
	@Size(max = 256, message = "The venue name cannot be longer than 256 characters.")
	private String name;

	@Min(value = 1, message = "The capacity must be a positive number.")
	private int capacity;
	
	@NotBlank(message="The venue name cannot be blank")
	@Size(max = 300, message = "The address cannot be longer than 300 characters.")
	private String address;
	
	@NotBlank(message="The postcode cannot be blank")
	private String postCode;
	
	public Venue() {
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getCapacity() {
		return capacity;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPostCode() {
		return postCode;
	}

	public void setPostCode(String postCode) {
		this.postCode = postCode;
	}
}
