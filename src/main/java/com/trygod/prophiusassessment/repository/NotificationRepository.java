package com.trygod.prophiusassessment.repository;

import com.trygod.prophiusassessment.data.NotificationData;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends CrudRepository<NotificationData, Long>,
        QuerydslPredicateExecutor<NotificationData>,
        QuerydslPredicateProjectionRepository<NotificationData>{

    List<NotificationData> findAllByUser_Id(Long userId);

    void deleteAllByUser_Id(Long userId);
}
