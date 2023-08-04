package com.trygod.prophiusassessment.repository;

import com.trygod.prophiusassessment.data.PostData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<PostData, Long> {

    Page<PostData> findAll(Pageable pageable);

    Page<PostData> findAllByContentContainingIgnoreCase(String keyWord, Pageable pageable);
}
