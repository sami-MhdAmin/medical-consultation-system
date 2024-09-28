package com.grad.akemha.bootstrap;

import com.grad.akemha.repository.TokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Component
@Transactional
public class TokenCleanupTask {

    @Autowired
    private TokenRepository tokenRepository;

    @Scheduled(fixedRate = 259200000) // 3 days in milliseconds
    public void deleteExpiredTokens() {
        Date now = new Date(System.currentTimeMillis());
        tokenRepository.deleteByExpiryTimeBefore(now);
    }
}
