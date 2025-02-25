package uk.ac.man.cs.eventlite.dao;

import java.util.List;

import uk.ac.man.cs.eventlite.entities.Venue;

public interface VenueService {

	public long count();

	public Iterable<Venue> findAll();
	
	public Venue save(Venue venue);
	
	public Venue findById(long id);
	
    public List<Venue> searchVenues(String query);
    
    public void update(Venue venueTo, Venue venueFrom);

}
