package com.sat.service;

import com.sat.model.Event;
import com.sat.model.News;
import com.sat.model.User;
import com.sat.repositories.EventRepository;
import com.sat.repositories.NewsRepository;
import com.sat.repositories.UserRepository;
import com.sat.security.SecurityUtil;
import com.sat.web.BadRequestException;
import com.sat.web.dto.DTOMappers;
import com.sat.web.dto.EventDTO;
import com.sat.web.dto.NewsDTO;
import org.joda.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.sat.web.ErrorInfo.*;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final NewsRepository newsRepository;

    @Autowired
    public EventService(UserRepository userRepository, EventRepository eventRepository, NewsRepository newsRepository) {
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
        this.newsRepository = newsRepository;
    }

    public EventDTO addEvent(EventDTO eventDTO) {
        String currentUser = SecurityUtil.getCurrentUser();
        Optional<User> user = userRepository.findOneByName(currentUser);


        user.orElseThrow(() -> new BadRequestException(EVENT_OWNER_NOT_FOUND));

        Event event = Event.builder()
                .name(eventDTO.getName())
                .owner(user.get())
                .participants(extractParticipants(eventDTO.getParticipants()))
                .startDateTime(new LocalDateTime(eventDTO.getDate()))
                .placeName(eventDTO.getPlace())
                .description(eventDTO.getDescription())
                .publicEvent(eventDTO.isPublicEvent())
                .build();

        eventRepository.save(event);

        return EventDTO.builder()
                .id(event.getId())
                .build();
    }

    public void deleteEventWithId(String id) {
        Optional<Event> eventToDel = eventRepository.findOneById(id);

        eventToDel.ifPresent(eventRepository::delete);

        eventToDel.orElseThrow(() -> new BadRequestException(EVENT_NOT_FOUND));

    }

    public List<EventDTO> getEventsByDateBetween(LocalDateTime start_date, LocalDateTime end_date) {
        return eventRepository
                .findByStartDateTimeBetween(start_date, end_date)
                .stream()
                .map(DTOMappers::eventToDTO)
                .collect(Collectors.toList());
    }


    public List<EventDTO> getAllEvents() {
        return eventRepository.findAll()
                .stream()
                .map(DTOMappers::eventToDTO)
                .collect(Collectors.toList());
    }

    public List<EventDTO> getEventsByPlaceName(String placeName) {
        return eventRepository
                .findByPlaceNameContaining(placeName)
                .stream()
                .map(DTOMappers::eventToDTO)
                .collect(Collectors.toList());
    }

    public EventDTO getEvent(String id) {
        return eventRepository.findOneById(id).map(DTOMappers::eventToDTO).orElseThrow(() ->
                new BadRequestException(EVENT_NOT_FOUND));
    }

    public List<EventDTO> getEventsByName(String name) {
        return eventRepository
                .findByNameContaining(name)
                .stream()
                .map(DTOMappers::eventToDTO)
                .collect(Collectors.toList());
    }

    public List<EventDTO> getEventsByOwner(String owner) {
        return userRepository.findOneById(owner)
                .orElseThrow(() -> new BadRequestException(USER_NOT_FOUND))
                .getUserOwnedEvents()
                .stream()
                .map(DTOMappers::eventToDTO)
                .collect(Collectors.toList());
    }

    public void joinEvent(String eventId) {
        String currentUser = SecurityUtil.getCurrentUser();
        Optional<User> user = userRepository.findOneByName(currentUser);

        Optional<Event> event = eventRepository.findOneById(eventId);

        event.map(e -> {
            if (!e.getOwner().equals(user.get())) {
                e.getParticipants().add(user.get());
                eventRepository.save(e);
                return e;
            } else {
                throw new BadRequestException(ALREADY_JOINED);
            }
        }).orElseThrow(() -> new BadRequestException(EVENT_NOT_FOUND));
    }

    public void addNews(NewsDTO newsDTO, String id) {
        Optional<Event> opt = eventRepository.findOneById(id);

        opt.orElseThrow(() -> new BadRequestException(EVENT_NOT_FOUND));

        News news = News.builder()
                .content(newsDTO.getContent())
                .createDate(new LocalDateTime(newsDTO.getDate()))
                .build();

        newsRepository.save(news);

        opt.get().getNews().add(news);
        eventRepository.flush();
    }

    private Set<User> extractParticipants(List<String> participants) {
        Set<User> participantsSet = new HashSet<>();
        if (participants != null) {
            for (String userName : participants) {
                userRepository.findOneByName(userName)
                        .ifPresent(participantsSet::add);
            }
        }
        return participantsSet;
    }

}
