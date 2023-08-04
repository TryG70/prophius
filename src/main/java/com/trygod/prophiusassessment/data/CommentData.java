package com.trygod.prophiusassessment.data;

import jakarta.persistence.*;
import lombok.Data;


@Entity
@Data
@Table(name = "COMMENTS")
public class CommentData extends BaseEntityData {

    @Column(name = "CONTENT",nullable = false)
    private String content;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "POSTS_ID", nullable = false)
    private PostData post;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USERS_ID", nullable = false)
    private UserData user;
}
