package com.trygod.prophiusassessment.data;

import com.trygod.prophiusassessment.data.QUserData;
import com.querydsl.core.BooleanBuilder;
import jakarta.persistence.*;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

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

    @ManyToMany(mappedBy = "followers")
    private Set<UserData> following = new HashSet<>();


    public BooleanBuilder buildPredicate(String search){

        BooleanBuilder builder = new BooleanBuilder();
        QUserData qRecipient = QUserData.userData;
        if(!search.isBlank()){
            builder.or(qRecipient.username.containsIgnoreCase(search));
        }
        return builder;
    }
}
