package com.grad.akemha.entity;

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
@Table(name = "verification_code")
public class VerificationCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String code;


    @Column(name = "expiry_time", nullable = false)
    private LocalDateTime expiryTime;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
