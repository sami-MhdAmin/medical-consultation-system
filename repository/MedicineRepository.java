package com.grad.akemha.repository;

import com.grad.akemha.entity.Medicine;
import com.grad.akemha.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface MedicineRepository extends JpaRepository<Medicine, Long> {
    Optional<Medicine> findByIdAndUser(Long id, User user);

    Optional<Medicine> findByLocalIdAndUser(Long localId, User user);

    @Transactional
    void deleteByLocalIdAndUser(Long localId, User user);

    List<Medicine> findByUser(User user);

    @Query("SELECT m FROM Medicine m WHERE m.user = :user AND (m.endDate IS NULL OR m.endDate >= :currentDate)")
    List<Medicine> findCurrentMedicinesByUser(@Param("user") User user, @Param("currentDate") LocalDate currentDate);
}
