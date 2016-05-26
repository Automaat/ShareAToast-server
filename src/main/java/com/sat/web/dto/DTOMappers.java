package com.sat.web.dto;

import com.sat.model.Authority;
import com.sat.model.Event;
import com.sat.model.User;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class DTOMappers {

    public static EventDTO eventToDTO(Event event) {
        return EventDTO.builder()
                .id(event.getId())
                .name(event.getName())
                .date(event.getStartDateTime().toDate().getTime())
                .description(event.getDescription())
                .ownerName(event.getOwner().getUsername())
                .place(event.getPlaceName())
                .publicEvent(event.isPublicEvent())
                .participants(extractParticipantsAsList(event.getParticipants()))
                .build();
    }

    public static User parseUser(UserDTO userDTO) {
        return User.builder()
                .name(userDTO.getName())
                .email(userDTO.getEmail())
                .password(userDTO.getPassword())
                .grantedAuthorities(new LinkedList<Authority>())
                .nonExpired(true)
                .nonLocked(true)
                .credentialsNotExpired(true)
                .enabled(true)
                .build();
    }

    public static UserDTO parseToDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .password(user.getPassword())
                .build();
    }

    private static List<String> extractParticipantsAsList(Set<User> users) {
        List<String> participants = new ArrayList<>();

        if (users != null) {
            participants = users.stream().map(User::getUsername).collect(Collectors.toList());
        }

        return participants;
    }
}
