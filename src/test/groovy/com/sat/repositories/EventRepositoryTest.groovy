package com.sat.repositories

import com.sat.SaTApplication
import com.sat.model.Event
import com.sat.model.News
import com.sat.model.User
import org.joda.time.LocalDateTime
import org.joda.time.format.DateTimeFormat
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.SpringApplicationConfiguration
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import spock.lang.Specification

@SpringApplicationConfiguration(classes = SaTApplication.class)
@WebAppConfiguration
@ActiveProfiles("test")
@Transactional
@Rollback
class EventRepositoryTest extends Specification {

    @Autowired
    UserRepository userRepository;

    @Autowired
    EventRepository eventRepository;

    @Autowired
    NewsRepository newsRepository;

    def dtf = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");

    def janusz = User.builder()
            .name("Janusz")
            .email("janusz@xxx.pl")
            .password("qwerty123")
            .events(new HashSet<Event>())
            .userOwnedEvents(new HashSet<Event>())
            .friends(new HashSet<User>())
            .mates(new HashSet<User>())
            .build()

    def grazyna = User.builder()
            .name("Grazyna")
            .email("grazia@xxx.pl")
            .password("zxcvbn987")
            .events(new HashSet<Event>())
            .userOwnedEvents(new HashSet<Event>())
            .friends(new HashSet<User>())
            .mates(new HashSet<User>())
            .build()

    def sebastian = User.builder()
            .name("Sebiastian")
            .email("seba@xxx.pl")
            .password("qwertyuiodhdh")
            .events(new HashSet<Event>())
            .userOwnedEvents(new HashSet<Event>())
            .friends(new HashSet<User>())
            .mates(new HashSet<User>())
            .build()

    def someEvent = Event.builder()
            .id("1")
            .name("SomeEvent")
            .startDateTime(dtf.parseLocalDateTime("2016-12-31 23:45:00"))
            .placeName("Krakow")
            .description("sylwester z jedynka")
            .publicEvent(true)
            .owner(janusz)
            .build()

    def otherEvent = Event.builder()
            .name("SpecialEvent")
            .startDateTime(new LocalDateTime())
            .description("very very otherEvent")
            .placeName("Kraków")
            .publicEvent(true)
            .build()

    def someNews = News.builder()
            .content("Lorem ipsum")
            .createDate(dtf.parseLocalDateTime("2016-12-31 23:45:00"))
            .build()

    def "method save(Event event) should save event in database"() {
        given:
        def owner = userRepository.findOne("2")
        otherEvent.setOwner(owner)

        when:
        saveEvent(otherEvent)

        then:
        dbContainsEvent(otherEvent)
    }

    def "repo should delete event from database"() {
        when:
        deleteEvent(someEvent)

        then:
        !dbContainsEvent(someEvent)
    }

    def "method findByName finds event by name"() {
        when:
        def opt = eventRepository.findOneByName("SomeEvent")

        then:
        opt.isPresent()
        opt.get().getStartDateTime().equals(someEvent.getStartDateTime())
        opt.get().equals(someEvent)

    }

    def "method findByName finds event in database"() {
        when:
        def opt = eventRepository.findOneByName(someEvent.getName())

        then:
        opt.isPresent()
        someEvent.equals(opt.get())
    }

    def "repo adds participants to event"() {
        when:
        addParticipant(someEvent, grazyna)

        then:
        eventContainsParticipant(someEvent, grazyna)
        userIsEventMember(someEvent, grazyna)
    }

    def "repo doesn't remove participants of event from db while deleting the event"() {
        when:
        deleteEvent(someEvent)

        then:
        dbContainsUser(grazyna)
        dbContainsUser(sebastian)
    }

    def "repo should delete News from Event"() {
        when:
        deleteNews(someNews, "1")

        then:
        !dbContainsNews(someNews, "1")
    }


    def "repo should return News ordered"() {
        given:
        def event = eventRepository.findOne("1")

        expect:
        event.getNews().equals([
                News.builder().content("Lorem ipsum").createDate(dtf.parseLocalDateTime("2016-12-31 23:47:00")).build(),
                News.builder().content("Lorem ipsum").createDate(dtf.parseLocalDateTime("2016-12-31 23:45:00")).build(),
                News.builder().content("Lorem ipsum").createDate(dtf.parseLocalDateTime("2016-12-31 23:43:00")).build()
        ] as List)
    }

    def "repo should keep News ordered after insert"() {
        given:
        def news = News.builder().content("Lorem ipsum").createDate(dtf.parseLocalDateTime("2016-12-31 23:42:00")).build();

        when:
        addNews(news, "1")

        then:
        isNewsEqual([
                News.builder().content("Lorem ipsum").createDate(dtf.parseLocalDateTime("2016-12-31 23:47:00")).build(),
                News.builder().content("Lorem ipsum").createDate(dtf.parseLocalDateTime("2016-12-31 23:45:00")).build(),
                News.builder().content("Lorem ipsum").createDate(dtf.parseLocalDateTime("2016-12-31 23:43:00")).build(),
                News.builder().content("Lorem ipsum").createDate(dtf.parseLocalDateTime("2016-12-31 23:42:00")).build()
        ] as List, "1")
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    def saveEvent(event) {
        eventRepository.save(event)
        eventRepository.flush()
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    def dbContainsEvent(event) {
        eventRepository.findAll().contains(event)
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    void addParticipant(event, user) {

        def eventOpt = eventRepository.findOneByName(event.getName())
        assert eventOpt.isPresent()

        def userOpt = userRepository.findOneByName(user.getName())
        assert userOpt.isPresent()

        eventOpt.get().getParticipants().add(userOpt.get())

        eventRepository.flush()
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    def eventContainsParticipant(event, user) {

        def eventOpt = eventRepository.findOneByName(event.getName())
        assert eventOpt.isPresent()

        eventOpt.get().getParticipants().contains(user)
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    void deleteEvent(event) {
        def eventOpt = eventRepository.findOneByName(event.getName())
        assert eventOpt.isPresent()

        eventRepository.delete(eventOpt.get())
        eventRepository.flush()
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    def dbContainsUser(user) {
        userRepository.findAll().contains(user)
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    def userIsEventMember(event, user) {
        def userOpt = userRepository.findOneByName(user.getName())
        assert userOpt.isPresent()

        userOpt.get().getEvents().contains(event)
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    def deleteNews(news, eventId) {
        def dbEvent = eventRepository.findOne(eventId)
        def dbNews = dbEvent.getNews()
        dbNews.remove(news)
        eventRepository.flush()
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    def addNews(News news, String eventId) {
        def dbEvent = eventRepository.findOne(eventId)
        def dbNews = dbEvent.getNews()
        newsRepository.save(news)
        dbNews.add(news)
        eventRepository.flush()
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    def dbContainsNews(news, eventId) {
        def dbEvent = eventRepository.findOne(eventId)
        def dbNews = dbEvent.getNews()

        dbNews.contains(news)
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    def isNewsEqual(news, eventId) {
        def dbEvent = eventRepository.findOne(eventId)

        dbEvent.getNews().equals(news)
    }
}
