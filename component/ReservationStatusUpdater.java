package com.grad.akemha.component;

import com.grad.akemha.entity.DeviceReservation;
import com.grad.akemha.entity.MedicalDevice;
import com.grad.akemha.entity.enums.DeviceReservationStatus;
import com.grad.akemha.repository.DeviceReservationRepository;
import com.grad.akemha.repository.MedicalDeviceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class ReservationStatusUpdater {

    @Autowired
    private MedicalDeviceRepository medicalDeviceRepository;
    @Autowired
    private DeviceReservationRepository deviceReservationRepository;

    @Scheduled(fixedDelay = 1000)
    public void updateCheckOutStatus() {
        LocalDateTime currentTime = LocalDateTime.now();
        List<DeviceReservation> deviceReservations = deviceReservationRepository.findByExpirationTimeBeforeAndStatusIsNull(
                currentTime);
        for (DeviceReservation reservation : deviceReservations) {
            reservation.setStatus(DeviceReservationStatus.PENDING);
            deviceReservationRepository.save(reservation);
            MedicalDevice medicalDevice = reservation.getMedicalDevice();
            medicalDevice.setReservedCount(medicalDevice.getReservedCount() - 1);
            medicalDeviceRepository.save(medicalDevice);
        }
    }
}
