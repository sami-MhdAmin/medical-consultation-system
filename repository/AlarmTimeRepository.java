package com.grad.akemha.repository;

import com.grad.akemha.entity.AlarmTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlarmTimeRepository extends JpaRepository<AlarmTime, Long> {
}
