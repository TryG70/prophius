package com.trygod.prophiusassessment.data;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Data
@Table(name = "POSTS")
public class PostData extends BaseEntityData {

    @Column(name = "CONTENT")
    private String content;

    @Column(name = "LIKE_COUNT")
    private int likeCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USERS_ID")
    private UserData user;

    @OneToMany
    @JoinColumn(name = "POSTS_ID")
    private Set<UserData> likedBy = new HashSet<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CommentData> comments = new ArrayList<>();

}
