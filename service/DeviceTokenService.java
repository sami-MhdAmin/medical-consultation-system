package com.grad.akemha.service;

import com.grad.akemha.entity.DeviceToken;
import com.grad.akemha.entity.User;
import com.grad.akemha.repository.DeviceTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DeviceTokenService {
    private final DeviceTokenRepository deviceTokenRepository;


    public boolean saveDeviceTokenIfNotExists(String token, User user) {
        Optional<DeviceToken> existingToken = deviceTokenRepository.findByDeviceTokenAndUser(token,user);
        if (existingToken.isEmpty()) {
            DeviceToken deviceToken = new DeviceToken();
            deviceToken.setDeviceToken(token);
            deviceToken.setUser(user);
            deviceTokenRepository.save(deviceToken);
            return true;
        } else {
            return false;
        }
    }
}
