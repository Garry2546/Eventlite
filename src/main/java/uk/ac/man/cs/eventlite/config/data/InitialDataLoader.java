package uk.ac.man.cs.eventlite.config.data;

import java.time.LocalTime;
import java.time.LocalDate;
import java.util.ArrayList;

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
@Profile("default")
public class InitialDataLoader {

	private final static Logger log = LoggerFactory.getLogger(InitialDataLoader.class);

	@Autowired
	private EventService eventService;

	@Autowired
	private VenueService venueService;

	@Bean
	CommandLineRunner initDatabase() {
		return args -> {
			if (venueService.count() > 0) {
				log.info("Database already populated with venues. Skipping venue initialization.");
			} else {
				Venue venue = new Venue();
				venue.setCapacity(100);
				venue.setName("Arbitrary Venue");
				venue.setAddress("Arbitrary Street");
				venue.setPostCode("POSTCODE");
				venueService.save(venue);
				
				Venue venue2 = new Venue();
				venue2.setCapacity(100);
				venue2.setName("Arbitrary Venue 2");
				venue2.setAddress("Arbitrary Street 2");
				venue2.setPostCode("POSTCODE 2");
				venueService.save(venue2);
			}

			if (eventService.count() > 0) {
				log.info("Database already populated with events. Skipping event initialization.");
			} else {
				
				ArrayList<Venue> venueList = new ArrayList<>();
						
				venueService.findAll().forEach(venueList::add);
				
				Event sport = new Event();
				sport.setName("Badminton");
				sport.setVenue(venueList.get(0));				
				sport.setTime(LocalTime.now());
				//Tomorrows Date
				sport.setDate(LocalDate.now().plusDays(1));
				sport.setDescription("A game of badminton");
				eventService.save(sport);
				
				Event sport2 = new Event();
				sport2.setName("Cricket");
				sport2.setVenue(venueList.get(1));
				sport2.setTime(LocalTime.now());
				sport2.setDate(LocalDate.now());
				sport2.setDescription("A game of cricket");
				eventService.save(sport2);
				
			}
		};
	}
}
