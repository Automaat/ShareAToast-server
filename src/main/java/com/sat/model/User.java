package com.sat.model;

import lombok.*;
import org.hibernate.validator.constraints.Email;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.*;

@Entity
@Table(name = "users")
@Builder(builderMethodName = "hiddenUserBuilder")
@Data
@EqualsAndHashCode(exclude = {"id", "friends", "mates", "events", "userOwnedEvents", "grantedAuthorities", "nonExpired", "nonLocked", "credentialsNotExpired", "enabled"})
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"events", "userOwnedEvents", "mates", "friends"})
public class User implements UserDetails, CredentialsContainer {

    @Id
    private String id = UUID.randomUUID().toString();

    @NotNull
    @Size(min = 3, max = 20)
    @Pattern(regexp = "[A-Za-z0-9]+")
    private String name;

    @NotNull
    @Email
    @Column(unique = true)
    private String email;

    @NotNull
    @Size(min = 5)
    private String password;

    @ManyToMany(mappedBy = "participants", fetch = FetchType.EAGER)
    private Set<Event> events = new HashSet<>();

    @OneToMany(mappedBy = "owner", fetch = FetchType.EAGER)
    private Set<Event> userOwnedEvents = new HashSet<>();

    /**
     * Users who where added as friends by this user
     */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_friends",
            joinColumns = {@JoinColumn(name = "user_id")},
            inverseJoinColumns = {@JoinColumn(name = "friend_id")}
    )
    private Set<User> friends = new HashSet<>();

    /**
     * Users who added this user as a friend
     */
    @ManyToMany(mappedBy = "friends", fetch = FetchType.EAGER)
    private Set<User> mates = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_authorities",
            joinColumns = @JoinColumn(name = "userid"),
            inverseJoinColumns = @JoinColumn(name = "authority")
    )
    private List<Authority> grantedAuthorities;

    private boolean nonExpired;
    private boolean nonLocked;
    private boolean credentialsNotExpired;
    private boolean enabled;

    public User(String name, String password, List<Authority> authorities) {
        this.name = name;
        this.password = password;
        this.grantedAuthorities = authorities;
        this.nonExpired = true;
        this.nonLocked = true;
        this.credentialsNotExpired = true;
        this.enabled = true;
    }


    public static UserBuilder builder() {
        return hiddenUserBuilder().id(UUID.randomUUID().toString());
    }

    @Override
    public void eraseCredentials() {
        this.password = null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.grantedAuthorities;
    }

    public void setAuthorities(Authority authority) {
        grantedAuthorities.add(authority);
    }

    @Override
    public String getUsername() {
        return this.name;
    }

    @Override
    public boolean isAccountNonExpired() {
        return this.nonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.nonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return this.nonExpired;
    }
}
