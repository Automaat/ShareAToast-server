package com.sat.service

import com.sat.model.Event
import com.sat.model.News
import com.sat.model.User
import com.sat.repositories.EventRepository
import com.sat.repositories.NewsRepository
import com.sat.repositories.UserRepository
import com.sat.web.BadRequestException
import com.sat.web.dto.DTOMappers
import com.sat.web.dto.NewsDTO
import com.sat.web.ErrorInfo
import org.joda.time.format.DateTimeFormat
import spock.lang.Specification

import static com.sat.web.dto.DTOMappers.*

class EventServiceTest extends Specification {

    def eventRepository = Mock(EventRepository)
    def userRepository = Mock(UserRepository)
    def newsRepository = Mock(NewsRepository)

    def eventService = new EventService(userRepository, eventRepository, newsRepository)

    def dtf = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")

    def someEvent = Event.builder()
            .name("SomeEvent")
            .startDateTime(dtf.parseLocalDateTime("2016-12-31 23:45:00"))
            .placeName("Krakow")
            .description("sylwester z jedynka")
            .publicEvent(true)
            .owner(new User())
            .news(new ArrayList<News>())
            .build()

    def firstEvent = Event.builder()
            .name("name")
            .startDateTime(dtf.parseLocalDateTime("2016-12-31 23:45:00"))
            .placeName("Krakow")
            .description("sylwester z jedynka")
            .publicEvent(true)
            .owner(new User())
            .build()

    def thirdEvent = Event.builder()
            .name("some")
            .startDateTime(dtf.parseLocalDateTime("2016-11-11 11:11:00"))
            .placeName("Krakow")
            .description("walka o pas polsatu")
            .publicEvent(true)
            .owner(new User())
            .build()


    def "method getEvent should return DTO when event exist"() {
        given:
        eventRepository.findOneById(_) >> Optional.of(someEvent)

        when:
        def eventDTO = eventService.getEvent(someEvent.getId())

        then:
        eventDTO.equals(eventToDTO(someEvent))
    }

    def "method getEvent should throw BadRequestException when event doesn't exist"() {
        given:
        eventRepository.findOneById(_) >> Optional.empty()

        when:
        eventService.getEvent("xxx")

        then:
        thrown(BadRequestException)
    }

    def "method getEventsByPlace should return list of events found by placeName"() {
        given:
        def placeName = "Krakow"
        def returnedList = [
                someEvent,
                firstEvent
        ] as List
        eventRepository.findByPlaceNameContaining(placeName) >> returnedList

        when:
        def listOfFoundItems = eventService.getEventsByPlaceName(placeName)

        then:
        listOfFoundItems == [
                eventToDTO(someEvent),
                eventToDTO(firstEvent)
        ] as List
    }

    def "method getEventsByPlace should return empty list of events"() {
        given:
        def placeName = "Radom"
        eventRepository.findByPlaceNameContaining(placeName) >> new ArrayList<Event>()

        when:
        def listOfFoundItems = eventService.getEventsByPlaceName(placeName)

        then:
        listOfFoundItems.isEmpty()
    }

    def "method getEventsByName should return list of events found by name"() {
        given:
        def returnedList = [
                firstEvent,
                secondEvent,
        ] as List
        eventRepository.findByNameContaining("name") >> returnedList

        when:
        def listOfFoundItems = eventService.getEventsByName("name")

        then:
        listOfFoundItems == [
                eventToDTO(firstEvent),
                eventToDTO(secondEvent)
        ] as List
    }

    def "method getEventsByName should return empty list"() {
        given:
        eventRepository.findByNameContaining("xyz") >> new ArrayList<Event>()

        when:
        def response = eventService.getEventsByName("xyz")

        then:
        response.isEmpty()
    }

    def firstUser = User.builder()
            .name("Jan1")
            .email("xyzc@vp.com")
            .password("asdfg")
            .build()

    def secondUser = User.builder()
            .name("Jan2")
            .email("xyzq@vp.com")
            .password("asdfg")
            .build()

    def secondEvent = Event.builder()
            .name("SecondEvent")
            .startDateTime(dtf.parseLocalDateTime("2016-12-31 23:45:00"))
            .placeName("Krakow")
            .description("sylwester z jedynka")
            .publicEvent(true)
            .owner(firstUser)
            .build()

    def "method getEventsByOwner should return list with matching events"() {
        given:
        def ownEventsSet = [
                firstEvent,
                secondEvent
        ] as Set

        userRepository.findOneById(secondEvent.getOwner().getId()) >> Optional.of(firstUser)
        firstUser.userOwnedEvents = ownEventsSet

        when:
        def foundList = eventService.getEventsByOwner(firstUser.getId())

        then:
        foundList == [
                eventToDTO(firstEvent),
                eventToDTO(secondEvent)
        ] as List
    }

    def "method getEventsByOwner should return empty list when user doesn't own any event"() {
        given:
        def emptyOwnEventsSet = [] as Set
        userRepository.findOneById(secondUser.getId()) >> Optional.of(secondUser)
        secondUser.userOwnedEvents = emptyOwnEventsSet

        when:
        def foundList = eventService.getEventsByOwner(secondUser.getId())

        then:
        foundList.isEmpty()
    }

    def "method getEventsByOwner should throw BadRequestException when user doesn't exist"() {
        given:
        userRepository.findOneById(_) >> { throw new BadRequestException(ErrorInfo.USER_NOT_FOUND) }

        when:
        eventService.getEventsByOwner("x")

        then:
        thrown(BadRequestException)
    }

    def "method getEventsByDate should return list of events found by date"() {
        given:
        def returnedList = [
                                firstEvent,
                                secondEvent

            ] as List
            eventRepository.findByStartDateTimeBetween(dtf.parseLocalDateTime("2016-12-31 23:40:00"),dtf.parseLocalDateTime("2016-12-31 23:50:00")) >> returnedList
        when:
            def listOfFoundItems = eventService.getEventsByDateBetween(dtf.parseLocalDateTime("2016-12-31 23:40:00"),dtf.parseLocalDateTime("2016-12-31 23:50:00"))
        then:
                listOfFoundItems == [
                        eventToDTO(firstEvent),
                        eventToDTO(secondEvent),
                    ] as List
           }
    def "method getEventsByDate should return empty list"() {
        given:
                def returnedList = [
                ] as List
                eventRepository.findByStartDateTimeBetween(dtf.parseLocalDateTime("2016-12-31 23:50:00"),dtf.parseLocalDateTime("2016-12-31 23:55:00")) >> returnedList

        when:
              def listOfFoundItems = eventService.getEventsByDateBetween(dtf.parseLocalDateTime("2016-12-31 23:50:00"),dtf.parseLocalDateTime("2016-12-31 23:55:00"))
        then:
            listOfFoundItems == [] as List
    }

    def "method addNews(NewsDTO newsDTO, String id) should throw BadRequestException"() {
        given:
        eventRepository.findOneById("123") >> Optional.empty()

        when:
        eventService.addNews(new NewsDTO(), "123")

        then:
        thrown(BadRequestException)
    }

    def "method addNews(NewsDTO newsDTO, String id) should save newsRepository and flush eventRepository"() {
        given:
        eventRepository.findOneById(someEvent.getId()) >> Optional.of(someEvent)

        when:
        eventService.addNews(new NewsDTO(), someEvent.getId())

        then:
        1 * newsRepository.save(_)
        1 * eventRepository.flush()
    }
}
