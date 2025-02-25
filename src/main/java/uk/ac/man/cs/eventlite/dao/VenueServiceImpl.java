package uk.ac.man.cs.eventlite.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.ac.man.cs.eventlite.entities.Venue;
import uk.ac.man.cs.eventlite.exceptions.VenueNotFoundException;

@Service
public class VenueServiceImpl implements VenueService {

	
	@Autowired
	private VenueRepository venueRepository;

	@Override
	public long count() {
		return venueRepository.count();
	}

	@Override
	public Iterable<Venue> findAll() {
		return venueRepository.findAll();
	}

	@Override
	public Venue save(Venue venue) {
		return venueRepository.save(venue);
	}
	
	@Override
	public List<Venue> searchVenues(String query) {
		return venueRepository.findByNameContainingIgnoreCaseOrderByNameAsc(query);
	}
	
	@Override
	public Venue findById(long id) {
		return venueRepository.findById(id)
	            .orElseThrow(() -> new VenueNotFoundException(id));
	}
	
	@Override
	public void update(Venue venue, Venue changedVenue) {
		venue.setId(changedVenue.getId());
		venue.setName(changedVenue.getName());
		venue.setCapacity(changedVenue.getCapacity());
		venue.setAddress(changedVenue.getAddress());
		venue.setPostCode(changedVenue.getPostCode());
		
		
		venueRepository.save(venue);
	}
	
}

