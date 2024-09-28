package com.grad.akemha.repository;

import com.grad.akemha.entity.Token;
import com.grad.akemha.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {

    Optional<Token> findByToken(String token);

    List<Token> findAllByUser(User user);

    void deleteByExpiryTimeBefore(Date now);
}
