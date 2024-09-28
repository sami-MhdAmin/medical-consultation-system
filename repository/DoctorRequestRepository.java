package com.grad.akemha.repository;

import com.grad.akemha.entity.DeviceReservation;
import com.grad.akemha.entity.DoctorRequest;
import com.grad.akemha.entity.User;
import com.grad.akemha.entity.enums.DoctorRequestStatus;
import com.grad.akemha.entity.enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface DoctorRequestRepository extends JpaRepository<DoctorRequest, Long> {
    @Query("SELECT dr FROM DoctorRequest dr WHERE (:status IS NULL AND dr.status IS NULL) OR dr.status = :status")
    Page<DoctorRequest> findByStatusOrNull(@Param("status") DoctorRequestStatus status, Pageable pageable);


    @Query("SELECT COUNT(dr) FROM DoctorRequest dr WHERE (:status IS NULL AND dr.status IS NULL) OR dr.status = :status")
    long countByStatusOrNull(@Param("status") DoctorRequestStatus status);


    @Query("SELECT dr FROM DoctorRequest dr WHERE dr.timestamp <= :threshold AND dr.status IS NULL")
    List<DoctorRequest> findDoctorRequestsOlderThanAndStatusIsNull(@Param("threshold") LocalDateTime threshold);


}
