package com.grad.akemha.entity;

import jakarta.persistence.*;
import lombok.*;


@ToString
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "device_token")
public class DeviceToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "device_token",nullable = false)
    private String deviceToken;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
