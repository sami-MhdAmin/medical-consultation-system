package com.grad.akemha.entity;

import com.grad.akemha.entity.enums.AdditionalInfoType;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@ToString
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "additional_record_info")
public class AdditionalRecordInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    @Column
    private String description;


    @Enumerated(EnumType.STRING)
    private AdditionalInfoType type;

    @Column(name = "create_time", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;


    @ManyToOne()
    @JoinColumn(name = "medical_record_id")
    private MedicalRecord medicalRecord;
}


//    @OneToMany(mappedBy = "additionalRecordInfo")
//    private List<AdditionalRecordDetails> additionalRecordDetailsList;
