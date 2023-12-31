package com.trygod.prophiusassessment.repository;

import com.trygod.prophiusassessment.data.NotificationData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<NotificationData, Long> {

    List<NotificationData> findAllByUser_Id(Long userId);

    void deleteAllByUser_Id(Long userId);

    Page<NotificationData> findAllByUser_IdOrderByCreatedDateDesc(Long userId, Pageable pageable);
}
