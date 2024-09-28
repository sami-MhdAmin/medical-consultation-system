package com.grad.akemha.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;
import java.util.List;

@ToString
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "alarm_time")
public class AlarmTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private LocalTime time;

    @ManyToOne
    @JoinColumn(name = "medicine_id", nullable = false)
    private Medicine medicine;

    @OneToMany(mappedBy = "alarmTime", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AlarmHistory> alarmHistory;

    @Transient
    private boolean taken;

    public AlarmTime(Long id, LocalTime time, Medicine medicine) {
        this.id = id;
        this.time = time;
        this.medicine = medicine;
    }
}
