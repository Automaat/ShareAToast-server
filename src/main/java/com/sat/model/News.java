package com.sat.model;

import lombok.*;
import org.hibernate.annotations.Type;
import org.joda.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Entity
@Builder(builderMethodName = "hiddenEventBuilder")
@Data
@EqualsAndHashCode(exclude = {"id"})
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "news")
public class News {
    @Id
    private String id = UUID.randomUUID().toString();

    @NotNull
    private String content;

    @NotNull
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDateTime")
    private LocalDateTime createDate;

    public static NewsBuilder builder() {
        return hiddenEventBuilder().id(UUID.randomUUID().toString());
    }
}
