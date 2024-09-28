package com.grad.akemha.dto.medicalDevice;

import com.grad.akemha.dto.user.response.UserLessResponse;
import com.grad.akemha.entity.DeviceReservation;
import com.grad.akemha.entity.MedicalDevice;
import com.grad.akemha.entity.User;
import com.grad.akemha.entity.enums.DeviceReservationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationResponse {
    private Long id;
    private UserLessResponse user;
    private DeviceReservationStatus status;
    private LocalDateTime timestamp;
    private LocalDateTime expirationTime;
    private LocalDateTime takeTime;
    private LocalDateTime rewindTime;
    private MedicalDevice medicalDevice;

    public ReservationResponse(DeviceReservation deviceReservation) {
        this.id = deviceReservation.getId();
        this.user = new UserLessResponse(deviceReservation.getUser());
        this.status = deviceReservation.getStatus();
        this.timestamp = deviceReservation.getTimestamp();
        this.expirationTime = deviceReservation.getExpirationTime();
        this.takeTime = deviceReservation.getTakeTime();
        this.rewindTime = deviceReservation.getRewindTime();
        this.medicalDevice = deviceReservation.getMedicalDevice();

    }
}
