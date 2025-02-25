package uk.ac.man.cs.eventlite.dao;

import java.util.List;

import uk.ac.man.cs.eventlite.entities.Event;

public interface EventService {

	public long count();

	public Iterable<Event> findAll();
	
	public void save(Event event);

	public Event findById(long id);
	
    public List<Event> searchEvents(String query);
    
    public void update(Event eventTo, Event eventFrom);
	
	public void deleteById(long id);
}
