package uk.ac.man.cs.eventlite.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import uk.ac.man.cs.eventlite.entities.Event;

public interface EventRepository extends CrudRepository<Event, Long>{
	
	@Query
	List<Event> findAllByOrderByDateAscTimeAsc();

	@Query
	List<Event> findByNameContainingIgnoreCaseOrderByDateAscTimeAscNameAsc(String query);
	
}
