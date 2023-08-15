package com.trygod.prophiusassessment.data;

import jakarta.persistence.*;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;
import java.util.Objects;


@Entity
@Data
@Table(name = "USERS")
public class UserData extends BaseEntityData {

    @Column(name = "USERNAME", unique = true)
    private String username;

    @Column(name = "PASSWORD")
    private String password;

    @Column(name = "EMAIL", unique = true)
    private String email;

    @Column(name = "PROFILE_PICTURE")
    private String profilePicture;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PostData> posts = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "USER_FOLLOWERS",
            joinColumns = @JoinColumn(name = "USERS_ID"),
            inverseJoinColumns = @JoinColumn(name = "FOLLOWERS_ID")
    )
    private Set<UserData> followers = new HashSet<>();

    @ManyToMany(mappedBy = "followers", cascade = CascadeType.PERSIST)
    private Set<UserData> following = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<NotificationData> notifications = new HashSet<>();

    @Override
    public int hashCode() {
        return Objects.hash(username, email, getId());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserData userData = (UserData) o;
        return Objects.equals(username, userData.username) &&
                Objects.equals(email, userData.email) &&
                Objects.equals(getId(), userData.getId());
    }
}
