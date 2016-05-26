package com.sat.model;

import lombok.*;
import org.hibernate.annotations.Type;
import org.joda.time.LocalDateTime;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.*;

@Entity
@Table(name = "events")
@Builder(builderMethodName = "hiddenEventBuilder")
@Data
@EqualsAndHashCode(exclude = {"id", "participants", "news"})
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"owner", "participants", "news"})
public class Event {

    @Id
    private String id = UUID.randomUUID().toString();

    @NotNull
    @Size(min = 5)
    private String name;

    @NotNull
    @ManyToOne
    private User owner;

    @NotNull
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDateTime")
    private LocalDateTime startDateTime;

    @NotNull
    @Size(min = 5)
    private String placeName;

    @NotNull
    @Size(min = 5)
    private String description;

    @NotNull
    private boolean publicEvent;

    @ManyToMany
    @JoinTable(
            name = "users_to_events",
            joinColumns = {@JoinColumn(name = "event_id")},
            inverseJoinColumns = {@JoinColumn(name = "user_id")}
    )
    private Set<User> participants = new HashSet<>();

    @OneToMany
    @JoinColumn(name = "event_id")
    @OrderBy("create_date DESC")
    private List<News> news = new ArrayList<>();

    public static EventBuilder builder(){
        return hiddenEventBuilder().id(UUID.randomUUID().toString());
    }
}
