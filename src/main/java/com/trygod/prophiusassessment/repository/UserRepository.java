package com.trygod.prophiusassessment.repository;

import com.trygod.prophiusassessment.data.UserData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserData, Long> {

    Optional<UserData> findByUsername(String username);

    Page<UserData> findAllByUsernameContainingIgnoreCase(String keyWord, Pageable pageable);

    Optional<UserData> findByEmailIgnoreCase(String email);
}
