//package com.grad.akemha.entity;
//
//import jakarta.persistence.*;
//import lombok.*;
//
//@ToString
//@Setter
//@Getter
//@Builder
//@AllArgsConstructor
//@NoArgsConstructor
//@Entity
//@Table(name = "additional_record_details")
//public class AdditionalRecordDetails {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @ManyToOne()
//    @JoinColumn(name="additional_record_info_id")
//    private AdditionalRecordInfo additionalRecordInfo;
//
//    @Column
//    private String name;
//
//    @Column
//    private String description;
//
//
//}
