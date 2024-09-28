package com.grad.akemha.entity;

import com.grad.akemha.entity.enums.DeviceReservationStatus;
import com.grad.akemha.entity.enums.DoctorRequestStatus;
import com.grad.akemha.entity.enums.Gender;
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
@Table(name = "doctor_request")
public class DoctorRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String cv;

    @Column(nullable = false,length = 2000)
    private String aboutMe;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @ManyToOne
    @JoinColumn(name = "specialization_id")
    private Specialization specialization;

    @Column(name = "cv_public_id", nullable = false)
    private String cvPublicId;


    @Enumerated(EnumType.STRING)
    private DoctorRequestStatus status;

    @Column(name = "device_token")
    private String deviceToken;

    private LocalDateTime timestamp;


}
