package com.trygod.prophiusassessment.data;

import jakarta.persistence.*;
import lombok.Data;


@Entity
@Data
@Table(name = "COMMENTS")
public class CommentData extends BaseEntityData {

    @Column(name = "CONTENT")
    private String content;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "POSTS_ID")
    private PostData post;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USERS_ID")
    private UserData user;
}
