package com.grad.akemha.repository;

import com.grad.akemha.dto.statistic.DeviceReservationCountResponse;
import com.grad.akemha.dto.statistic.StatisticCountResponse;
import com.grad.akemha.entity.DeviceReservation;
import com.grad.akemha.entity.User;
import com.grad.akemha.entity.enums.DeviceReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface DeviceReservationRepository extends JpaRepository<DeviceReservation, Long> {
    List<DeviceReservation> findByUserAndStatus(User user, DeviceReservationStatus status);

    int countByUserAndStatus(User user, DeviceReservationStatus status);

    List<DeviceReservation> findByExpirationTimeBeforeAndStatusIsNull(LocalDateTime currentTime);

//    List<DeviceReservation> findByUserAndStatusIn(User user, List<DeviceReservationStatus> statuses);

    @Query("SELECT dr FROM DeviceReservation dr WHERE dr.user = :user AND (dr.status IN :statuses OR dr.status IS NULL) ORDER BY dr.id DESC")
    List<DeviceReservation> findByUserAndStatusIn(@Param("user") User user, @Param("statuses") List<DeviceReservationStatus> statuses);


//    @Query("SELECT new com.grad.akemha.dto.statistic.DeviceReservationCountResponse(" +
//            "d.name, YEAR(r.timestamp), MONTH(r.timestamp), COUNT(r)) " +
//            "FROM DeviceReservation r " +
//            "JOIN r.medicalDevice d " +
//            "WHERE r.status IN (:statuses) " +
//            "GROUP BY d.name, YEAR(r.timestamp), MONTH(r.timestamp) " +
//            "ORDER BY d.name, YEAR(r.timestamp), MONTH(r.timestamp)")
//    List<DeviceReservationCountResponse> countReservationsByMonth(@Param("statuses") List<DeviceReservationStatus> statuses);
    @Query("SELECT new com.grad.akemha.dto.statistic.DeviceReservationCountResponse(" +
            "d.name, YEAR(r.timestamp), MONTH(r.timestamp), COUNT(r)) " +
            "FROM DeviceReservation r " +
            "JOIN r.medicalDevice d " +
            "WHERE r.status IN (:statuses) " +
            "GROUP BY d.name, YEAR(r.timestamp), MONTH(r.timestamp) " +
            "ORDER BY d.name, YEAR(r.timestamp), MONTH(r.timestamp)")
    List<DeviceReservationCountResponse> countReservationsByMonth(@Param("statuses") List<DeviceReservationStatus> statuses);

}
