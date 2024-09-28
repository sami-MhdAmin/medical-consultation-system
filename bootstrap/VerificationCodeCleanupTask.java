package com.grad.akemha.bootstrap;

import com.grad.akemha.entity.VerificationCode;
import com.grad.akemha.repository.VerificationCodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class VerificationCodeCleanupTask {
    @Autowired
    private VerificationCodeRepository verificationCodeRepository;

    //    @Scheduled(cron = "0 * * * * *") // Runs every minute
    @Scheduled(cron = "0 0 */3 * * *") // Runs every 3 hours
    public void cleanupExpiredVerificationCodes() {
        // Retrieve expired verification codes
        LocalDateTime currentTime = LocalDateTime.now();
        Iterable<VerificationCode> expiredCodes = verificationCodeRepository.findByExpiryTimeBefore(currentTime);

        // Delete expired verification codes
        verificationCodeRepository.deleteAll(expiredCodes);
    }
}
