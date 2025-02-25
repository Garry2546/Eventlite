package uk.ac.man.cs.eventlite.config.data;

import java.time.LocalDate;
import java.time.LocalTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import uk.ac.man.cs.eventlite.dao.EventService;
import uk.ac.man.cs.eventlite.dao.VenueService;
import uk.ac.man.cs.eventlite.entities.Event;
import uk.ac.man.cs.eventlite.entities.Venue;

@Configuration
@Profile("test")
public class TestDataLoader {

	private final static Logger log = LoggerFactory.getLogger(TestDataLoader.class);

	@Autowired
	private EventService eventService;

	@Autowired
	private VenueService venueService;

	@Bean
	CommandLineRunner initDatabase() {
		return args -> {
			// Build and save test events and venues here.
			// The test database is configured to reside in memory, so must be initialized
			// every time.
			Venue venue2 = new Venue();
			venue2.setCapacity(100);
			venue2.setName("Arbitrary Venue 2");
			venueService.save(venue2);
			
			
			Event sport = new Event();
			sport.setName("Badminton");
			sport.setVenue(venue2);
			sport.setId(1L);
			
	        LocalDate currentDate = LocalDate.now();

	        // Add one day to the current date
	        LocalDate tomorrowDate = currentDate.plusDays(1);
			
			sport.setTime(LocalTime.now());
			sport.setDate(tomorrowDate);
			eventService.save(sport);
			
			Event sport2 = new Event();
			sport2.setName("Badminton");
			sport2.setVenue(venue2);
			sport2.setTime(LocalTime.now());
			sport2.setDate(LocalDate.now());
			sport2.setId(2L);
			eventService.save(sport2);
			
			Event sport3 = new Event();
			sport3.setName("Badminton");
			sport3.setVenue(venue2);
			sport3.setTime(LocalTime.now());
			sport3.setDate(LocalDate.now());
			sport3.setId(3L);
			eventService.save(sport3);
		};
	}
}
