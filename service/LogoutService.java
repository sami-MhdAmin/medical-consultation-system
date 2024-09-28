package com.grad.akemha.service;

import com.grad.akemha.entity.DeviceToken;
import com.grad.akemha.entity.Token;
import com.grad.akemha.entity.User;
import com.grad.akemha.repository.DeviceTokenRepository;
import com.grad.akemha.repository.TokenRepository;
import com.grad.akemha.security.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LogoutService implements LogoutHandler {

    private final TokenRepository tokenRepository;
    private final JwtService jwtService;
    private final DeviceTokenRepository deviceTokenRepository;
    private final FCMService fcmService;


    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }
        jwt = authHeader.substring(7);
        User user = jwtService.extractUserFromJwtToken(jwt);
        var storedToken = tokenRepository.findByToken(jwt).orElse(null);
        if (storedToken != null) {
            storedToken.setExpired(true);
            storedToken.setRevoked(true);
            tokenRepository.delete(storedToken);

            // to delete all tokens related
            List<Token> tokens = tokenRepository.findAllByUser(user);
            for (Token token : tokens) {
                tokenRepository.delete(token);
            }
//            Optional<DeviceToken> optionalDeviceToken = deviceTokenRepository.findByDeviceToken(user.getDeviceToken());
//            // TODO: unsubscribe to topics
//            if (optionalDeviceToken.isPresent()) {
//                DeviceToken deviceToken = optionalDeviceToken.get();
//                fcmService.unSubscribeFromTopic(deviceToken.getDeviceToken(), "all");
//                fcmService.unSubscribeFromTopic(deviceToken.getDeviceToken(), "posts");
//                deviceTokenRepository.delete(deviceToken);
//            }
            // salimo modified for logout
            List<DeviceToken> deviceTokens = deviceTokenRepository.findAllByUser(user);
            for (DeviceToken deviceToken : deviceTokens) {
                System.out.println("ENTERED HEREEEEEEEE: TO REMOVE DEVICE TOKENS");
                fcmService.unSubscribeFromTopic(deviceToken.getDeviceToken(), "all");
                fcmService.unSubscribeFromTopic(deviceToken.getDeviceToken(), "posts");
                deviceTokenRepository.delete(deviceToken);
                System.out.println("ENTERED HEREEEEEEEE: TO REMOVE DEVICE TOKENS");
            }

            SecurityContextHolder.clearContext();
        }
    }


}