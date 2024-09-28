package com.grad.akemha.repository;

import com.grad.akemha.entity.MedicalRecord;
import com.grad.akemha.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Long> {
    Optional<MedicalRecord> findFirstByUserOrderByCreateTimeDesc(User user);
    List<MedicalRecord> findAllByUserId(Long userId);
}
