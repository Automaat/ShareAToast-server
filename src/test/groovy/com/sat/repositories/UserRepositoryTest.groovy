package com.sat.repositories

import com.sat.SaTApplication
import com.sat.model.Event
import com.sat.model.User
import org.joda.time.format.DateTimeFormat
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.SpringApplicationConfiguration
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import spock.lang.Ignore
import spock.lang.Specification

import javax.validation.ConstraintViolationException

@SpringApplicationConfiguration(classes = SaTApplication.class)
@WebAppConfiguration
@Transactional
@Rollback
@ActiveProfiles("test")
class UserRepositoryTest extends Specification {

    @Autowired
    UserRepository repository;

    def dtf = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");


    def janusz = User.builder()
            .id("1")
            .name("Janusz")
            .email("janusz@xxx.pl")
            .password("qwerty123")
            .events(new HashSet<Event>())
            .userOwnedEvents(new HashSet<Event>())
            .friends(new HashSet<User>())
            .mates(new HashSet<User>())
            .build()

    def grazyna = User.builder()
            .id("2")
            .name("Grazyna")
            .email("grazia@xxx.pl")
            .password("zxcvbn987")
            .events(new HashSet<Event>())
            .userOwnedEvents(new HashSet<Event>())
            .friends(new HashSet<User>())
            .mates(new HashSet<User>())
            .build()

    def kazek = User.builder()
            .id("4")
            .name("Kazimierz")
            .email("kazek@xxx.pl")
            .password("kakakzld")
            .events(new HashSet<Event>())
            .userOwnedEvents(new HashSet<Event>())
            .friends(new HashSet<User>())
            .mates(new HashSet<User>())
            .build()

    def roman = User.builder()
            .id("5")
            .name("Roman")
            .email("romek@aaaqqq.pl")
            .password("zaqxwsedce")
            .events(new HashSet<Event>())
            .userOwnedEvents(new HashSet<Event>())
            .friends(new HashSet<User>())
            .mates(new HashSet<User>())
            .build()

    def someEvent = Event.builder()
            .name("SomeEvent")
            .startDateTime(dtf.parseLocalDateTime("2016-12-31 23:45:00"))
            .placeName("Krakow")
            .description("sylwester z jedynka")
            .publicEvent(true)
            .owner(janusz)
            .build()

    def "repo finds user in database"() {
        when:
        def allUsers = repository.findAll()

        then:
        allUsers.contains(janusz)
        allUsers.contains(grazyna)
    }

    def "method findOneByEmail finds user by email"() {
        when:
        def januszOpt = repository.findOneByEmail(janusz.getEmail())

        then:
        januszOpt.isPresent()
        januszOpt.get().equals(janusz)
    }

    def "method findOneByName finds user by name"() {
        when:
        def grazynaOpt = repository.findOneByName(grazyna.getName())

        then:
        grazynaOpt.isPresent()
        grazynaOpt.get().equals(grazyna)
    }

    def "repo deletes user from database"() {
        when:
        deleteUser(kazek)

        then:
        !dbContainsUser(kazek)
    }

    def "method save fails when mail is not unique"() {
        given:
        roman.setEmail(grazyna.getEmail())

        when:
        repository.save(roman)
        repository.flush()

        then:
        thrown(Exception) //it should be DataIntegrityViolationException, but Hibernate with HSQL throws other exception
    }

    @Ignore
    def "repo saves friendship relation"() {
        given:
        saveFriendshipRelation()

        expect:
        oneHasFriend()
        twoHasMate()
    }

    def "repo deletes friendship relation"() {
        given:
        deleteFriendshipRelation()

        expect:
        !oneHasFriend()
        !twoHasMate()
    }

    def "repo saves user in database"() {
        when:
        saveUser(roman)

        then:
        dbContainsUser(roman)
    }

    def "repo doesn't remove owner of existing event"() {
        when:
        repository.delete("1")
        repository.flush()

        then:
        thrown(DataIntegrityViolationException)
    }

    def "repo doesn't save user with broken mail"() {
        given:
        roman.setEmail("fdsjkdfsb756")

        when:
        repository.save(roman)
        repository.flush()

        then:
        thrown(ConstraintViolationException)
    }

    def "repo doesn't save user with broken name"() {
        given:
        roman.setName("1")

        when:
        repository.save(roman)
        repository.flush()

        then:
        thrown(ConstraintViolationException)
    }

    def "repo should find user and events he participate"() {
        when:
        def user = repository.findOne("2") //Grazyna

        then:
        user.getEvents().size() == 1
        user.getEvents().contains(someEvent)
    }

    def "repo should find user and his events"() {
        when:
        def user = repository.findOne("1") //Janusz

        then:
        user.getUserOwnedEvents().size() == 1
        user.getUserOwnedEvents().contains(someEvent)
    }

    def "repo should find user and empty events set when he doesn't participate any event"() {
        when:
        def user = repository.findOne("4") //Kazek

        then:
        user.getEvents().isEmpty()
    }

    def "repo should find user and empty userOwnedEvents set when he doesn't own any event"() {
        when:
        def user = repository.findOne("2") //Grazyna

        then:
        user.getUserOwnedEvents().isEmpty()
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    void saveFriendshipRelation() {
        def user1 = repository.findOne("1")
        def user2 = repository.findOne("2")

        user1.getFriends().add(user2)
        repository.flush()
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    def oneHasFriend() {

        def one = repository.findOne("1")

        !one.getFriends().isEmpty()
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    def twoHasMate() {

        def two = repository.findOne("2")

        !two.getMates().isEmpty()
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    void deleteFriendshipRelation() {
        def one = repository.findOne("1")

        one.getFriends().removeAll()

        repository.flush()
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    void saveUser(user) {
        repository.save(user)
        repository.flush()
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    def dbContainsUser(user) {
        repository.findAll().contains(user)
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    void deleteUser(user) {

        def userOpt = repository.findOneByName(user.getName())
        assert userOpt.isPresent()

        repository.delete(userOpt.get().getId())
    }

}
