package com.trygod.prophiusassessment.repository;

import com.trygod.prophiusassessment.data.UserData;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<UserData, Long>,
        QuerydslPredicateExecutor<UserData>,
        QuerydslPredicateProjectionRepository<UserData>{
}
