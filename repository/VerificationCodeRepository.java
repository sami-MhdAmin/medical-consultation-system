package com.grad.akemha.repository;

import com.grad.akemha.entity.User;
import com.grad.akemha.entity.VerificationCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface VerificationCodeRepository extends JpaRepository<VerificationCode, Long> {
    Iterable<VerificationCode> findByExpiryTimeBefore(LocalDateTime currentTime);

    Optional<VerificationCode> findByUser(User user);

    void deleteByUser(User user);
}
