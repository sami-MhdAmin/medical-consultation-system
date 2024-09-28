package com.grad.akemha.repository;

import com.grad.akemha.dto.statistic.SpecializationUserCountResponse;
import com.grad.akemha.entity.Specialization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SpecializationRepository extends JpaRepository<Specialization, Long> {
    List<Specialization> findAll();
    Specialization findBySpecializationType(String specializationType);
    Optional<Specialization> findById(Long id);

    List<Specialization> findByIsPublicFalse();


    @Query("SELECT new com.grad.akemha.dto.statistic.SpecializationUserCountResponse(s.specializationType, COUNT(u)) " +
            "FROM Specialization s LEFT JOIN s.users u " +
            "GROUP BY s.specializationType")
    List<SpecializationUserCountResponse> countUsersBySpecialization();


    List<Specialization> findByConsultationsIsNotEmpty();
    List<Specialization> findByUsersIsNotEmpty();
}
