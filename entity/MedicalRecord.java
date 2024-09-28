package com.grad.akemha.entity;

import com.grad.akemha.entity.enums.BloodType;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;

@ToString
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "medical_record")
public class MedicalRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column
    private Double height;

    @Column
    private Double weight;

    @Column
    private Boolean coffee;

    @Column
    private Boolean alcohol;

    @Column
    private Boolean married;

    @Column
    private Boolean smoker;

    @Column(name = "covid_vaccine")
    private Boolean covidVaccine;

    @Enumerated(EnumType.STRING)
    @Column
    private BloodType bloodType;


    @Column(name = "create_time", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime; // for beneficiary and doctor


    @OneToMany(mappedBy = "medicalRecord", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AdditionalRecordInfo> additionalRecordInfo;

}
