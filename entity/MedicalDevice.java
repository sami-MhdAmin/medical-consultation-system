package com.grad.akemha.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

//@ToString
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "medical_device")
public class MedicalDevice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Integer count;

    @Column()
    private Integer reservedCount;

    @Column(nullable = false, unique = true)
    private String name;


    @Column(name = "image_url", nullable = true)
    private String imageUrl;

    @Column(name = "image_public_id", nullable = true)
    private String imagePublicId;

    @JsonIgnore
    @ToString.Exclude
    @OneToMany(mappedBy = "medicalDevice", cascade = CascadeType.ALL)
    private List<DeviceReservation> deviceReservations;
}
