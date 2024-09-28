package com.grad.akemha.repository;

import com.grad.akemha.dto.statistic.AgeRangeStatisticResponse;
import com.grad.akemha.dto.statistic.StatisticCountResponse;
import com.grad.akemha.dto.statistic.StatisticTypeResponse;
import com.grad.akemha.entity.Consultation;
import com.grad.akemha.entity.User;
import com.grad.akemha.entity.enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    Optional<User> findById(Long id);

    boolean existsByEmail(String email);

    Page<User> findByRole(Role role, Pageable pageable);
    Page<User> findBySpecializationIdAndRole(Long specializationId, Pageable pageable,Role role);


    List<User> findAllByRole(Role role);
    // to delete users that are not verified
    // note this function is called in AccountCleanupTask in bootstrap folder
    @Query("SELECT u FROM User u WHERE u.isVerified = false AND u.creationDate < :timeLimit")
    List<User> findUnverifiedAccountsCreatedBefore(LocalDateTime timeLimit);

    @Query("SELECT u FROM User u WHERE u.name LIKE %:keyword%")
    List<User> findByKeywordInName(String keyword);

    // super vision related

    // 1. retrieve 10 user who matches they key word the most
    @Query("SELECT u FROM User u WHERE u.role <> 'OWNER' AND u.id <> :userId AND u.email LIKE %:keyword% ORDER BY CASE " +
            "WHEN u.name LIKE :keyword THEN 1 " +
            "WHEN u.name LIKE :keyword% THEN 2 " +
            "WHEN u.name LIKE %:keyword THEN 3 " +
            "ELSE 4 END, u.name ASC")
    List<User> findByNameContaining(@Param("keyword") String keyword,@Param("userId") Long userId,Pageable pageable);

    // 2. retrieve 10 random users
    @Query(value = "SELECT * FROM User WHERE role <> 'OWNER' AND id <> :userId ORDER BY RAND() LIMIT 10",nativeQuery = true)
    List<User> findRandomUsers(@Param("userId") Long userId);


    @Query("SELECT new com.grad.akemha.dto.statistic.StatisticCountResponse(" +
            "YEAR(u.creationDate), MONTH(u.creationDate), COUNT(u)) " +
            "FROM User u " +
            "WHERE u.role = :role " +
            "GROUP BY YEAR(u.creationDate), MONTH(u.creationDate) " +
            "ORDER BY YEAR(u.creationDate), MONTH(u.creationDate)")
    List<StatisticCountResponse> countUserByMonth(@Param("role") Role role);


    @Query("SELECT new com.grad.akemha.dto.statistic.StatisticTypeResponse(" +
            "u.gender AS gender, " +
            "COUNT(u) AS count, " +
            "CONCAT(ROUND(COUNT(u) * 100.0 / (SELECT COUNT(u) FROM User u WHERE u.role = :role), 2), '%') AS percent) " +
            "FROM User u " +
            "WHERE u.role = :role " +
            "GROUP BY u.gender")
    List<StatisticTypeResponse> countUsersByGender(@Param("role") Role role);


    @Query("SELECT new com.grad.akemha.dto.statistic.AgeRangeStatisticResponse(" +
            "CASE " +
            "WHEN TIMESTAMPDIFF(YEAR, u.dob, CURRENT_DATE) < 18 THEN 'أصغر من 18' " +
            "WHEN TIMESTAMPDIFF(YEAR, u.dob, CURRENT_DATE) BETWEEN 18 AND 60 THEN 'من 18 إلى 60' " +
            "WHEN TIMESTAMPDIFF(YEAR, u.dob, CURRENT_DATE) > 60 THEN 'أكبر من 60' " +
            "END, " +
            "COUNT(u)) " +
            "FROM User u " +
            "WHERE u.role = :role " +
            "GROUP BY " +
            "CASE " +
            "WHEN TIMESTAMPDIFF(YEAR, u.dob, CURRENT_DATE) < 18 THEN 'أصغر من 18' " +
            "WHEN TIMESTAMPDIFF(YEAR, u.dob, CURRENT_DATE) BETWEEN 18 AND 60 THEN 'من 18 إلى 60' " +
            "WHEN TIMESTAMPDIFF(YEAR, u.dob, CURRENT_DATE) > 60 THEN 'أكبر من 60' " +
            "END")
    List<AgeRangeStatisticResponse> countUsersByAgeRangeAndRole(@Param("role") Role role);



    // search for doctors
    @Query("SELECT u FROM User u WHERE u.role <> 'OWNER' AND u.role <> 'USER' AND u.id <> :userId AND u.name LIKE %:keyword% ORDER BY CASE " +
            "WHEN u.name LIKE :keyword THEN 1 " +
            "WHEN u.name LIKE :keyword% THEN 2 " +
            "WHEN u.name LIKE %:keyword THEN 3 " +
            "ELSE 4 END, u.name ASC")
    List<User> findDoctorsByName(@Param("keyword") String keyword,@Param("userId") Long userId);

}