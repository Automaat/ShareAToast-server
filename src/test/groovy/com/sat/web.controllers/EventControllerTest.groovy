package com.sat.web.controllers

import com.sat.model.Event
import com.sat.model.User
import com.sat.repositories.EventRepository
import com.sat.repositories.NewsRepository
import com.sat.repositories.UserRepository

import com.sat.service.EventService
import com.sat.web.BadRequestException
import com.sat.web.ErrorInfo
import org.joda.time.LocalDateTime
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import spock.lang.Ignore
import spock.lang.Specification

import static org.springframework.http.MediaType.APPLICATION_JSON
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class EventControllerTest extends Specification {

    def service = Mock(EventService)

    def repository = Mock(EventRepository)

    def underTest = new EventController(service)

    def mockMvc = MockMvcBuilders.standaloneSetup(underTest).build()

    def "createEvent(EventCreateDTO dto) should add event"() {
        given:
        def content = '{"name": "SomeEvent", ' +
                '"ownerName": "SomeOwner", ' +
                '"participants": [], "date": 654654654, ' +
                '"place": "Cracow", ' +
                '"description": "fdkjdsf jsdgfjh", ' +
                '"publicEvent": true}'

        when:
        def response = mockMvc.perform(post('/api/events')
                .contentType(APPLICATION_JSON)
                .content(content)
        )

        then:
        response.andExpect(status().isCreated())
    }

    @Ignore
    def "createEvent(EventCreateDTO dto) should return BadRequest when there is not such owner in database"() {
        given:
        service.addEvent(_) >> { throw new BadRequestException(ErrorInfo.EVENT_OWNER_NOT_FOUND) }
        def content = '{"name": "SomeEvent", ' +
                '"ownerName": "SomeOwner", ' +
                '"participants": [], ' +
                '"date": 654654654, ' +
                '"place": "Cracow", ' +
                '"description": ' +
                '"fdkjdsf jsdgfjh", ' +
                '"publicEvent": true}'

        when:
        def response = mockMvc.perform(post('/event/create')
                .contentType(APPLICATION_JSON)
                .content(content)
        )

        then:
        response.andExpect(status().isBadRequest())
    }

    @Ignore
    def " deleteEvent(String name) should delete event and return OK"() {
        given:
        service.deleteEvent("EventName") >> true

        when:
        def response = mockMvc.perform(delete('/event/delete/EventName'))

        then:
        response.andExpect(status().isOk())

    }

    @Ignore
    def " deleteEvent(String name) should return BadRequest when there is not such event in database"() {
        given:
        service.deleteEvent("EventName") >> false

        when:
        def response = mockMvc.perform(delete('/event/delete/EventName'))

        then:
        response.andExpect(status().isBadRequest())

    }

    def "controller lists all events from database"() {
        given:
        def userRepository = Mock(UserRepository)
        def newsRepository = Mock(NewsRepository)
        def eventService = new EventService(userRepository, repository, newsRepository)
        def eventController = new EventController(eventService)
        def mockMvc = MockMvcBuilders.standaloneSetup(eventController).build()

        def user = new User()
        user.setName("TestOwner")

        def eventList = new ArrayList<Event>()
        def event = Event.builder()
                .id("1")
                .name("Test")
                .placeName("TestPlace")
                .owner(user)
                .startDateTime(new LocalDateTime(61381839600000))
                .description("Testy")
                .participants(Collections.emptySet())
                .build()
        eventList.add(event)

        repository.findAll() >> eventList

        when:
        def response = mockMvc.perform(get('/api/events'))

        then:
        response.andExpect(status().isOk())
                .andExpect(content().string('[{"id":"1",' +
                '"name":"Test",' +
                '"ownerName":"TestOwner",' +
                '"participants":[],' +
                '"date":61381839600000,' +
                '"place":"TestPlace",' +
                '"description":"Testy",' +
                '"publicEvent":false}]'))
    }
}
