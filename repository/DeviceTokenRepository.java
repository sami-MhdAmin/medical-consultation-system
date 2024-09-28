package com.grad.akemha.repository;

import com.grad.akemha.entity.DeviceToken;
import com.grad.akemha.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DeviceTokenRepository extends JpaRepository<DeviceToken, Long> {
    Optional<DeviceToken> findByDeviceTokenAndUser(String token, User user);

    Optional<DeviceToken> findByDeviceToken(String token);

    List<DeviceToken> findAllByUser(User user);

}
