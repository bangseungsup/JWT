package com.hanghaeblog.hanghaeblog.repository;

import com.hanghaeblog.hanghaeblog.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username); //username 줄 값을 파라미터안에 기입

}