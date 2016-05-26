package com.sat.repositories;


import com.sat.model.Event;
import org.joda.time.LocalDateTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface EventRepository extends JpaRepository<Event, String> {

    Optional<Event> findOneByName(String name);

    Optional<Event> findOneById(String id);

    List<Event> findByStartDateTimeBetween(LocalDateTime start_date, LocalDateTime end_date);

    List<Event> findByPlaceNameContaining(String placeName);

    List<Event> findByNameContaining(String name);

}
