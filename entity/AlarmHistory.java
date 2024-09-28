package com.grad.akemha.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.time.LocalTime;

@ToString
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "alarm_history")
public class AlarmHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //    private LocalDateTime takeDate; //TODO , check the type
    @Column
    private LocalDateTime takeTime;
    @ManyToOne
    @JoinColumn(name = "alarm_time_id", nullable = false)
    private AlarmTime alarmTime;
}
