package com.sat.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class EventDTO {
    private String id;
    private String name;
    private String ownerName;
    private List<String> participants;
    private long date;
    private String place;
    private String description;
    private boolean publicEvent;
}
