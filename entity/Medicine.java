package com.grad.akemha.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.grad.akemha.entity.enums.AlarmRoutine;
import com.grad.akemha.entity.enums.AlarmRoutineType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@ToString
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "medicine", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "localId"})
})
public class Medicine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;
    @Column
    private String description;

    @Column(unique = true, nullable = false)
    private Long localId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "medicine", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AlarmTime> alarmTimes;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AlarmRoutine alarmRoutine;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AlarmRoutineType alarmRoutineType;

    @Column
    private String alarmWeekDay;

    @Column
    private Integer selectedDayInMonth;

    public void setAlarmTimes(List<LocalTime> times) {
        this.alarmTimes = times.stream()
                .map(time -> new AlarmTime(null, time, this))
                .collect(Collectors.toList());
    }
}
