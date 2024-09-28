package com.grad.akemha.repository;

import com.grad.akemha.entity.AlarmHistory;
import com.grad.akemha.entity.AlarmTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Repository
public interface AlarmHistoryRepository extends JpaRepository<AlarmHistory, Long> {
    boolean existsByAlarmTimeAndTakeTimeBetween(AlarmTime alarmTime, LocalDateTime startDateTime, LocalDateTime  endDateTime);
}
