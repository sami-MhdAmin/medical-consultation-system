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
@Table(name = "message")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //TODO user_id ?
    @Column(name = "user_id", nullable = false)
    private Long userId;
//    @Column(name = "text_msg", nullable = false)
////    private String textMsg;

    @Column(name = "text_msg", nullable = false,columnDefinition = "TEXT")
    private String textMsg;

    @ManyToOne
    @JoinColumn(name = "consultation_id")
    private Consultation consultation;


}
