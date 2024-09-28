package com.grad.akemha.entity;

import com.grad.akemha.entity.enums.DeviceReservationStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@ToString
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "device_reservation")
public class DeviceReservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "medical_device_id")
    private MedicalDevice medicalDevice;

    @Enumerated(EnumType.STRING)
    @Column()
    private DeviceReservationStatus status;
    private LocalDateTime timestamp;
    private LocalDateTime expirationTime;

    private LocalDateTime takeTime;

    private LocalDateTime rewindTime;

    @PrePersist
    private void setTimestamp() {
        this.timestamp = LocalDateTime.now();
        this.expirationTime = LocalDateTime.now().plusDays(1L);
    }
}
