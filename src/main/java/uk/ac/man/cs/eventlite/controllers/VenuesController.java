package uk.ac.man.cs.eventlite.controllers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
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
import uk.ac.man.cs.eventlite.exceptions.VenueNotFoundException;

@Controller
@RequestMapping(value = "/venues", produces = { MediaType.TEXT_HTML_VALUE })
public class VenuesController {
		
	private static final Logger log = LoggerFactory.getLogger(EventsController.class);
	
	@Autowired
	private EventService eventService;

	@Autowired
	private VenueService venueService;
	
	
	@ExceptionHandler(VenueNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public String venueNotFoundHandler(VenueNotFoundException ex, Model model) {
		ex.printStackTrace();
		model.addAttribute("not_found_id", ex.getId());

		return "venues/not_found";
	}	
	
	@GetMapping
	public String getAllVenues(Model model) {
		model.addAttribute("venues", venueService.findAll());
		return "venues/index";
	}
	
	@GetMapping("/{id}")
	public String getVenue(@PathVariable("id") long id, Model model) {
		Venue venue = venueService.findById(id);
		if (venue == null) {
			throw new VenueNotFoundException(id);
		}
	    model.addAttribute("venue", venue);
	    
	    Iterable<Event> events = eventService.findAll();
	    List<Event> upcomingEventsAtVenue = new ArrayList<>();
	    
	    LocalDateTime now = LocalDateTime.now();
		for (Event event : events) {
			LocalDateTime eventDateTime = LocalDateTime.of(event.getDate(), event.getTime());
			if (eventDateTime.isAfter(now) && event.getVenue().getName() == venue.getName()) {
				upcomingEventsAtVenue.add(event);
			}
		}
	    
	    model.addAttribute("upcomingEventsAtVenue", upcomingEventsAtVenue);
	    
	    return "venues/venue-details";
	}
	
	@GetMapping("/new")
	public String newVenue(Model model) {
	    if (!model.containsAttribute("venue")) {
	        Venue venue = new Venue();
	        venue.setCapacity(1); // set the default capacity to 1
	        model.addAttribute("venue", venue);
	    } else {
	        Venue venue = (Venue) model.getAttribute("venue");
	        if (venue.getCapacity() == 0) {
	            venue.setCapacity(1); // set the default capacity to 1 if it's 0
	        }
	    }

	    return "venues/new";
	}
	
	@PostMapping(consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	public String createVenue(@RequestBody @Valid @ModelAttribute Venue venue, BindingResult errors,
			Model model, RedirectAttributes redirectAttrs) {

		if (errors.hasErrors()) {
			model.addAttribute("venue", new Venue());
			return "venues/new";
		}
		venueService.save(venue);
		redirectAttrs.addFlashAttribute("ok_message", "New venue added.");

		return "redirect:/venues/search";
	}
	
	
	@GetMapping("/search")
	public String searchVenues(@RequestParam(name = "query", required = false) String query, Model model) {
		log.info("Search query: " + query);

		List<Venue> searchResults;
		if (query != null && !query.isEmpty()) {
			searchResults = venueService.searchVenues(query);
			log.info("Search results: " + searchResults);
		} else {
			// If no query is provided, show all venues
			searchResults = (List<Venue>) venueService.findAll();
			log.info("No search query provided. Returning all venues.");
		}

		model.addAttribute("searchQuery", query); // Retain search query in input field
		model.addAttribute("searchResults", searchResults);
		return "venues/index";
	}
	
	@GetMapping("/edit/{id}")
	public String showEditPage(@PathVariable("id") long id, Model model) {
		
		Venue venue = venueService.findById(id);
		if (venue == null) {
			throw new VenueNotFoundException(id);
		}
		model.addAttribute("venue", venue);
		return "venues/edit";
	}
	
	@PostMapping("/edit/{id}")
	public String updateVenue(@PathVariable("id") long id, @Valid Venue venue, 
	                          BindingResult result, Model model) {
		Venue currVenue = venueService.findById(id);
		if (currVenue == null) {
		    throw new VenueNotFoundException(id);
		}
		venueService.update(currVenue, venue);
	    return "redirect:/venues/search?query=";
	}
	
	
}