package com.grad.akemha.bootstrap;

import com.grad.akemha.entity.User;
import com.grad.akemha.repository.UserRepository;
import com.grad.akemha.repository.VerificationCodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Transactional
public class AccountCleanupTask {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VerificationCodeRepository verificationCodeRepository;

    // Define the time interval for running the cleanup task (e.g., every 24 hours)
    @Scheduled(cron = "0 0 0 * * *")  // Runs at midnight every day
    public void cleanupUnverifiedAccounts() {
        // Define the time limit for account verification (e.g., 24 hours ago)
        LocalDateTime verificationTimeLimit = LocalDateTime.now().minusHours(24);

        // Retrieve unverified accounts registered before the verification time limit
        List<User> unverifiedAccounts = userRepository.findUnverifiedAccountsCreatedBefore(verificationTimeLimit);

        // Delete unverified accounts from the database
        for (User user : unverifiedAccounts) {
            // Delete verification code if exists for the user
            verificationCodeRepository.deleteByUser(user);
            userRepository.delete(user);
        }
    }
}

