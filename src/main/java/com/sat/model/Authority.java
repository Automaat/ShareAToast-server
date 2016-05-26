package com.sat.model;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table
@Data
public class Authority implements GrantedAuthority {

    @Id
    @Column(unique = true, nullable = false)
    private String authority;

    @ManyToMany(mappedBy = "grantedAuthorities")
    private List<User> users = new ArrayList<>();

    @Override
    public String getAuthority() {
        return authority;
    }
}
