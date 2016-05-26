package com.sat.web.controllers;


import com.sat.model.Event;
import com.sat.service.EventService;
import com.sat.web.dto.EventDTO;
import org.joda.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.*;

@RestController
@RequestMapping("/api")
public class EventController {

    private final EventService eventService;

    @Autowired
    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @RequestMapping(value = "/events", method = POST,
            consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<EventDTO> createEvent(@RequestBody EventDTO eventDTO) {

        return new ResponseEntity<>(eventService.addEvent(eventDTO), HttpStatus.CREATED);
    }

    @RequestMapping(value = "/events/{id}", method = DELETE)
    public ResponseEntity<?> deleteEvent(@PathVariable(value = "id") String eventId) {

        eventService.deleteEventWithId(eventId);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/events/{id}", method = GET)
    public ResponseEntity<?> getEvent(@PathVariable(value = "id") String id) {

        return new ResponseEntity<>(eventService.getEvent(id), HttpStatus.OK);
    }

    @RequestMapping(value = "/events", method = GET, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<List<EventDTO>> showEvents() {
        List<EventDTO> eventDTOs = eventService.getAllEvents();
        return new ResponseEntity<>(eventDTOs, HttpStatus.OK);

    }
    @RequestMapping(value = "/events", params={"start_date", "end_date"}, method = GET, produces = APPLICATION_JSON_VALUE)
        public ResponseEntity<List<EventDTO>> listEventByDateBetween(@RequestParam("start_date") LocalDateTime start_date, @RequestParam("end_date") LocalDateTime end_date) {
        List<EventDTO> eventDTOs = eventService.getEventsByDateBetween(start_date, end_date);
                return new ResponseEntity<>(eventDTOs, HttpStatus.OK);
    }

    @RequestMapping(value = "/events", params = "owner", method = GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<EventDTO>> getUser(@RequestParam("owner") String owner) {

        List<EventDTO> eventDTOs = eventService.getEventsByOwner(owner);

        return new ResponseEntity<>(eventDTOs, HttpStatus.OK);
    }

    @RequestMapping(value = "/events", params = "name", method = GET, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<List<EventDTO>> showEventsByName(@RequestParam("name") String name) {

        List<EventDTO> eventDTOs = eventService.getEventsByName(name);

        return new ResponseEntity<>(eventDTOs, HttpStatus.OK);
    }

    @RequestMapping(value = "/events", params = "placeName", method = GET, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<List<EventDTO>> getEventsByPlace(@RequestParam("placeName") String placeName) {

        List<EventDTO> eventDTOs = eventService.getEventsByPlaceName(placeName);
        return new ResponseEntity<>(eventDTOs, HttpStatus.OK);

    }

    @RequestMapping(value = "/events/{id}/me", method = POST)
    public ResponseEntity<?> joinEvent(@PathVariable("id") String evenId){

        eventService.joinEvent(evenId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
