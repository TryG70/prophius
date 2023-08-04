package com.trygod.prophiusassessment.data;

import com.trygod.prophiusassessment.data.QPostData;
import com.querydsl.core.BooleanBuilder;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "POSTS")
public class PostData extends BaseEntityData {

    @Column(name = "CONTENT", nullable = false)
    private String content;

    @Column(name = "LIKE_COUNT")
    private int likeCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USERS_ID", nullable = false)
    private UserData user;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CommentData> comments = new ArrayList<>();

    public BooleanBuilder buildPredicate(String search){

        BooleanBuilder builder = new BooleanBuilder();
        QPostData qRecipient = QPostData.postData;
        if(!search.isBlank()){
            builder.or(qRecipient.user.username.containsIgnoreCase(search));
        }
        return builder;
    }
}
