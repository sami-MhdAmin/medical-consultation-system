package com.grad.akemha.service;

import com.grad.akemha.entity.User;
import com.grad.akemha.entity.VerificationCode;
import com.grad.akemha.exception.ForbiddenException;
import com.grad.akemha.repository.VerificationCodeRepository;
import com.grad.akemha.utils.AESEncryptionUtil;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Random;

@Service
public class WhatsAppService {

    private static final String BASE_URL = "https://api.ultramsg.com/instance84730/messages/chat";
    private static final Random RANDOM = new Random();
    private final OkHttpClient client = new OkHttpClient();

    @Autowired
    private VerificationCodeRepository verificationCodeRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    public void sendVerificationCode(User user) throws IOException {
        String code = generateVerificationCode();

        RequestBody body = new FormBody.Builder()
                .add("token", "75ofifvy5vodgox5")
                .add("to", user.getPhoneNumber())
                .add("body", "Your Akemha Code:\n" + code)
                .build();

        Request request = new Request.Builder()
                .url(BASE_URL)
                .post(body)
                .addHeader("content-type", "application/x-www-form-urlencoded")
                .build();

        try (Response response = client.newCall(request).execute()) {
            System.out.println(Objects.requireNonNull(response.body()).string());
            if (response.code() == 200 || response.code() == 201) {
                LocalDateTime expiryTime = LocalDateTime.now().plusHours(3);
                // Create VerificationCode entity and link it to the user
                VerificationCode verificationCodeEntity = new VerificationCode();
                verificationCodeEntity.setCode(AESEncryptionUtil.encrypt(code));
                verificationCodeEntity.setUser(user);
                verificationCodeEntity.setExpiryTime(expiryTime);
                // Save the verification code entity
                verificationCodeRepository.save(verificationCodeEntity);
            } else {
                throw new ForbiddenException("An error has occurred when sending verification code");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }


    private String generateVerificationCode() {
        // Generate a random 6-digit verification code
        int code = 100000 + RANDOM.nextInt(900000);
        return String.valueOf(code);
    }
}
