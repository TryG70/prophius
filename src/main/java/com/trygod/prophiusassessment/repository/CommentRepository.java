package com.trygod.prophiusassessment.repository;

import com.trygod.prophiusassessment.data.CommentData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<CommentData, Long> {

    Page<CommentData> findAllByPost_Id(Long postId, Pageable pageable);
}
