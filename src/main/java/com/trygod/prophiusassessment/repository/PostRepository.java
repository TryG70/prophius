package com.trygod.prophiusassessment.repository;

import com.trygod.prophiusassessment.data.PostData;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends CrudRepository<PostData, Long>,
        QuerydslPredicateExecutor<PostData>,
        QuerydslPredicateProjectionRepository<PostData>{
}
