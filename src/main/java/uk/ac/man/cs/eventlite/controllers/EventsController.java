package uk.ac.man.cs.eventlite.controllers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import uk.ac.man.cs.eventlite.dao.EventService;
import uk.ac.man.cs.eventlite.dao.VenueService;
import uk.ac.man.cs.eventlite.entities.Event;
import uk.ac.man.cs.eventlite.entities.Venue;
import uk.ac.man.cs.eventlite.exceptions.EventNotFoundException;

@Controller
@RequestMapping(value = "/events", produces = { MediaType.TEXT_HTML_VALUE })
public class EventsController {
	
	private static final Logger log = LoggerFactory.getLogger(EventsController.class);

	@Autowired
	private EventService eventService;
	@Autowired
	private VenueService venueService;

	@ExceptionHandler(EventNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public String eventNotFoundHandler(EventNotFoundException ex, Model model) {
		ex.printStackTrace();
		model.addAttribute("not_found_id", ex.getId());

		return "events/not_found";
	}

	@GetMapping("/{id}")
	public String getEvent(@PathVariable("id") long id, Model model) {
		Event event = eventService.findById(id);
		if (event == null) {
			throw new EventNotFoundException(id);
		}
	    model.addAttribute("event", event);
	    return "events/event-details";
	}

	@GetMapping
	public String getAllEvents(Model model) {
		model.addAttribute("events", eventService.findAll());
		return "events/index";
	}

	@GetMapping("/new")
	public String newEvent(Model model) {
		if (!model.containsAttribute("event")) {
			model.addAttribute("event", new Event());
		}

		return "events/new";
	}
	
	@DeleteMapping("/{id}")
	public String deleteEvent(@PathVariable("id") long id, RedirectAttributes redirectAttrs) {
		
		eventService.deleteById(id);
		redirectAttrs.addFlashAttribute("ok_message", "Event deleted.");
		
		return "redirect:/events";
	}
	
	@PostMapping(consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	public String createEvent(@RequestBody @Valid @ModelAttribute Event event, BindingResult errors,
			Model model, RedirectAttributes redirectAttrs) {

		if (errors.hasErrors()) {
			model.addAttribute("event", event);
			return "events/new";
		}
		Venue venue = event.getVenue();		
		venueService.save(venue);
		eventService.save(event);
		redirectAttrs.addFlashAttribute("ok_message", "New event added.");

		return "redirect:/events";
	}

	@GetMapping("/search")
	public String searchEvents(@RequestParam(name = "query", required = false) String query, Model model) {
		log.info("Search query: " + query);

		List<Event> searchResults;
		List<Event> upcomingEvents = new ArrayList<>();
		List<Event> pastEvents = new ArrayList<>();
		if (query != null && !query.isEmpty()) {
			searchResults = eventService.searchEvents(query);
			log.info("Search results: " + searchResults);
		} else {
			// If no query is provided, show all events
			searchResults = (List<Event>) eventService.findAll();
			log.info("No search query provided. Returning all events.");
		}

		// Split the search results into upcoming and past events
		LocalDateTime now = LocalDateTime.now();
		for (Event event : searchResults) {
			LocalDateTime eventDateTime = LocalDateTime.of(event.getDate(), event.getTime());
			if (eventDateTime.isAfter(now) || eventDateTime.isEqual(now)) {
				upcomingEvents.add(event);
			} else {
				pastEvents.add(event);
			}
		}

		// Sort pastEvents in descending order by date
		Collections.sort(pastEvents, Comparator.comparing(Event::getDate).reversed());

		model.addAttribute("upcomingEvents", upcomingEvents);
		model.addAttribute("pastEvents", pastEvents);
		model.addAttribute("searchQuery", query); // Retain search query in input field
		return "events/index";
	}
	
	@GetMapping("/edit/{id}")
	public String showEditPage(@PathVariable("id") long id,Model model) {
		
		Event event = eventService.findById(id);
		if (event == null) {
			throw new EventNotFoundException(id);
		}
		model.addAttribute("event", event);
		return "events/edit";
	}
	
	@PostMapping("/edit/{id}")
	public String updateEvent(@PathVariable("id") long id, @Valid Event event, 
	                          BindingResult result, Model model) {
		Event currEvent = eventService.findById(id);
		if (currEvent == null) {
		    throw new EventNotFoundException(id);
		}
		eventService.update(currEvent, event);
	    return "redirect:/events/search?query=";
	}

	@GetMapping("/delete/{id}")
	public String deleteEvent(@PathVariable("id") String id, Model model) {
		eventService.deleteById(Long.parseLong(id));
		model.addAttribute("events", eventService.findAll());
		return "events/index";
	}
}
