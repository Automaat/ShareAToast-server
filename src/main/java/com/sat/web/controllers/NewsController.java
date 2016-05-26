package com.sat.web.controllers;

import com.sat.service.EventService;
import com.sat.web.dto.NewsDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
public class NewsController {
    private final EventService eventService;

    @Autowired
    public NewsController(EventService eventService) {
        this.eventService = eventService;
    }

    @RequestMapping(value = "/event/{id}/news", method = RequestMethod.POST,
            consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createNews(@PathVariable(value = "id") String id, @RequestBody NewsDTO newsDTO) {

        eventService.addNews(newsDTO, id);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
