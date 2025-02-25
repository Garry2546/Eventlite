package uk.ac.man.cs.eventlite.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.handler;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.hasItem;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.Optional;
import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import uk.ac.man.cs.eventlite.config.Security;
import uk.ac.man.cs.eventlite.dao.EventService;
import uk.ac.man.cs.eventlite.dao.VenueService;
import uk.ac.man.cs.eventlite.entities.Event;
import uk.ac.man.cs.eventlite.entities.Venue;

@ExtendWith(SpringExtension.class)
@WebMvcTest(EventsController.class)
@Import(Security.class)
public class EventsControllerTest {

	@Autowired
	private MockMvc mvc;

	@Mock
	private Event event;

	@Mock
	private Venue venue;

	@MockBean
	private EventService eventService;

	@MockBean
	private VenueService venueService;

	@Test
	public void getIndexWhenNoEvents() throws Exception {
		when(eventService.findAll()).thenReturn(Collections.<Event>emptyList());

		mvc.perform(get("/events").accept(MediaType.TEXT_HTML)).andExpect(status().isOk())
				.andExpect(view().name("events/index")).andExpect(handler().methodName("getAllEvents"));

		verify(eventService).findAll();
		verifyNoInteractions(event);
		verifyNoInteractions(venue);
	}

	@Test
	public void getIndexWithEvents() throws Exception {
		when(venue.getName()).thenReturn("Kilburn Building");
		
		when(event.getVenue()).thenReturn(venue);
		when(eventService.findAll()).thenReturn(Collections.<Event>singletonList(event));
		
		mvc.perform(get("/events").accept(MediaType.TEXT_HTML)).andExpect(status().isOk())
				.andExpect(view().name("events/index")).andExpect(handler().methodName("getAllEvents"));

		verify(eventService).findAll();
	}

	@Test
	public void getEventNotFound() throws Exception {
		mvc.perform(get("/events/99").accept(MediaType.TEXT_HTML)).andExpect(status().isNotFound())
				.andExpect(view().name("events/not_found")).andExpect(handler().methodName("getEvent"));
	}
	
	@Test
	public void getEditEvent() throws Exception {
		Event customEvent = new Event();
		Venue customVenue = new Venue();
		
	    customEvent.setId(2L);
	    customEvent.setName("Custom Event");
	    customEvent.setVenue(customVenue);
	    customEvent.setTime(LocalTime.now());
	    customEvent.setDate(LocalDate.now());
	    
		when(eventService.findById(2L)).thenReturn(customEvent);
		
		mvc.perform(get("/events/edit/2").with(user("Rob").roles(Security.ADMIN_ROLE)).accept(MediaType.TEXT_HTML).with(csrf())).andExpect(status().isOk())
		.andExpect(view().name("events/edit")).andExpect(handler().methodName("showEditPage"));
	}
	
	@Test
	public void searchEventsWithEmptyQuery() throws Exception {

		Event event1 = new Event();
		event1.setDate(LocalDate.now().plusDays(1));
		event1.setTime(LocalTime.now());

		Event event2 = new Event();
		event2.setDate(LocalDate.now().plusDays(2));
		event2.setTime(LocalTime.now());
		
		Venue venue = new Venue();
		venue.setCapacity(100);
		venue.setName("Arbitrary Venue 2");
		venueService.save(venue);
		
		event1.setVenue(venue);
		event2.setVenue(venue);


		when(eventService.findAll()).thenReturn(Arrays.asList(event1, event2));

		mvc.perform(get("/events/search").accept(MediaType.TEXT_HTML))
		        .andExpect(status().isOk())
		        .andExpect(view().name("events/index"))
		        .andExpect(handler().methodName("searchEvents"))
		        .andExpect(model().attribute("upcomingEvents", hasSize(2)));

	}

	@Test
	public void searchEventsWithSpecificQuery() throws Exception {
	    LocalDate futureDate = LocalDate.now().plusDays(1);
	    LocalTime time = LocalTime.of(12, 0);

	    Event event1 = new Event();
	    event1.setName("EventA");
	    event1.setDate(futureDate);
	    event1.setTime(time);
	    
		Venue venue = new Venue();
		venue.setCapacity(100);
		venue.setName("Arbitrary Venue 2");
		venueService.save(venue);
		
		event1.setVenue(venue);

	    when(eventService.searchEvents("EventA")).thenReturn(Collections.singletonList(event1));

	    mvc.perform(get("/events/search").param("query", "EventA").accept(MediaType.TEXT_HTML))
	            .andExpect(status().isOk()).andExpect(view().name("events/index"))
	            .andExpect(handler().methodName("searchEvents"))
	            .andExpect(model().attribute("upcomingEvents", hasSize(1)))
	            .andExpect(model().attribute("upcomingEvents", hasItem(event1)))
	            .andExpect(model().attribute("pastEvents", hasSize(0)));
	}

	@Test
	public void searchEventsWithNonExistentQuery() throws Exception {
	    when(eventService.searchEvents("NonExistentEvent")).thenReturn(Collections.emptyList());

	    mvc.perform(get("/events/search").param("query", "NonExistentEvent").accept(MediaType.TEXT_HTML))
	            .andExpect(status().isOk()).andExpect(view().name("events/index"))
	            .andExpect(handler().methodName("searchEvents"))
	            .andExpect(model().attribute("upcomingEvents", hasSize(0)))
	            .andExpect(model().attribute("pastEvents", hasSize(0)));
	}
	
	@Test
	public void searchPastEvents() throws Exception {
		Event event1 = new Event();
		event1.setDate(LocalDate.now().minusDays(1));
		event1.setTime(LocalTime.now());
		
		Venue venue = new Venue();
		venue.setCapacity(100);
		venue.setName("Arbitrary Venue 2");
		venueService.save(venue);
		
		event1.setVenue(venue);

		when(eventService.findAll()).thenReturn(Collections.singletonList(event1));

		mvc.perform(get("/events/search").accept(MediaType.TEXT_HTML))
		        .andExpect(status().isOk())
		        .andExpect(view().name("events/index"))
		        .andExpect(handler().methodName("searchEvents"))
		        .andExpect(model().attribute("pastEvents", hasItem(event1)))
		        .andExpect(model().attribute("pastEvents", hasSize(1)));
	}
	
	
	@Test
	public void checkEventMoves() throws Exception {
		Event event1 = new Event();
		event1.setDate(LocalDate.now().minusDays(1));
		event1.setTime(LocalTime.now());
		
		Venue venue = new Venue();
		venue.setCapacity(100);
		venue.setName("Arbitrary Venue 2");
		venueService.save(venue);
		
		event1.setVenue(venue);

		when(eventService.findAll()).thenReturn(Collections.singletonList(event1));

		// Perform the test to check the event is in past events
		mvc.perform(get("/events/search").accept(MediaType.TEXT_HTML))
		        .andExpect(status().isOk())
		        .andExpect(view().name("events/index"))
		        .andExpect(handler().methodName("searchEvents"))
		        .andExpect(model().attribute("pastEvents", hasItem(event1)))
		        .andExpect(model().attribute("pastEvents", hasSize(1)))
		        .andExpect(model().attribute("upcomingEvents", hasSize(0)));
		
		// Change event date to the future
		event1.setDate(LocalDate.now().plusDays(1));
		
		// Check the event has moved
		mvc.perform(get("/events/search").accept(MediaType.TEXT_HTML))
        .andExpect(status().isOk())
        .andExpect(view().name("events/index"))
        .andExpect(handler().methodName("searchEvents"))
        .andExpect(model().attribute("upcomingEvents", hasItem(event1)))
        .andExpect(model().attribute("pastEvents", hasSize(0)))
        .andExpect(model().attribute("upcomingEvents", hasSize(1)));
	}
	
	

	@Test
	public void getEventDetails() throws Exception {
		Event customEvent = new Event();
		Venue customVenue = new Venue();
		
	    customEvent.setId(2L);
	    customEvent.setName("Custom Event");
	    customEvent.setVenue(customVenue);
	    customEvent.setTime(LocalTime.now());
	    customEvent.setDate(LocalDate.now());
	    
		when(eventService.findById(2L)).thenReturn(customEvent);
		
		mvc.perform(get("/events/2").with(user("Rob").roles(Security.ADMIN_ROLE)).accept(MediaType.TEXT_HTML).with(csrf())).andExpect(status().isOk())
		.andExpect(view().name("events/event-details"));
	}
//	
// Not working somehow needa make this work
//	@Test
//	public void postEditEvent() throws Exception {
//		 mvc.perform(post("/edit/2")
//			        .with(user("Rob").roles(Security.ADMIN_ROLE))
//			        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
//			        .param("name", "Updated Event")
//			        .param("venue.name", venue.getName())
//			        .param("time", "12:00:00")
//			        .param("date", "2022-12-31")
//			        .with(csrf()));
//
//		verify(eventService).update(any(Event.class), any(Event.class));
//	}
}
