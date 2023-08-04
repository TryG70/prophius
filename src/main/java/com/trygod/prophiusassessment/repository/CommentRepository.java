package com.trygod.prophiusassessment.repository;

import com.trygod.prophiusassessment.data.CommentData;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends CrudRepository<CommentData, Long>,
        QuerydslPredicateExecutor<CommentData>,
        QuerydslPredicateProjectionRepository<CommentData>{
}
