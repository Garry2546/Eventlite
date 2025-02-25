package uk.ac.man.cs.eventlite.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;


import uk.ac.man.cs.eventlite.entities.Event;
import uk.ac.man.cs.eventlite.exceptions.EventNotFoundException;

@Service
public class EventServiceImpl implements EventService {
	
	@Autowired
	private EventRepository eventRepository;
	
	@Autowired
	private VenueRepository venueRepository;

	@Override
	public long count() {
		return eventRepository.count();
	}

	@Override
	public Iterable<Event> findAll() {
		return eventRepository.findAllByOrderByDateAscTimeAsc();
	}
	
	@Override
	public void save(Event event) {
		eventRepository.save(event);
	}

	@Override
	public Event findById(long id) {
		return eventRepository.findById(id)
	            .orElseThrow(() -> new EventNotFoundException(id));
	}
	
	@Override
	public List<Event> searchEvents(String query) {
		return eventRepository.findByNameContainingIgnoreCaseOrderByDateAscTimeAscNameAsc(query);
	}

	@Override
	public void update(Event event, Event changedEvent) {
		event.setDate(changedEvent.getDate());
		event.setId(changedEvent.getId());
		event.setName(changedEvent.getName());
		event.setDescription(changedEvent.getDescription());
		event.setTime(changedEvent.getTime());
		
		if (event.getVenue() != null) {
	        venueRepository.save(changedEvent.getVenue());
	        event.setVenue(changedEvent.getVenue());
	    }
		eventRepository.save(event);
	}
	
	@Override
	public void deleteById(long id) {
		eventRepository.deleteById(id);
	}

}
