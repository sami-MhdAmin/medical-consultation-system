package com.grad.akemha.repository;

import com.grad.akemha.dto.statistic.SpecializationConsultationCountResponse;
import com.grad.akemha.dto.statistic.StatisticCountResponse;
import com.grad.akemha.entity.Consultation;
import com.grad.akemha.entity.Specialization;
import com.grad.akemha.entity.enums.ConsultationStatus;
import com.grad.akemha.entity.enums.ConsultationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ConsultationRepository extends JpaRepository<Consultation, Long> {
    List<Consultation> findAllBySpecialization_SpecializationType(Optional<Specialization> specialization);
    Optional<Consultation> findById(Long id);

    @Query("SELECT c FROM Consultation c WHERE c.consultationText LIKE %:keyword%")
    List<Consultation> findByKeywordInConsultationText(String keyword); //TODO make the search in title also.

    @Query(value = "SELECT * FROM consultation WHERE id IN :ids ORDER BY FIELD(id, :orderedIds)", nativeQuery = true)
    List<Consultation> findByIdsInOrder(@Param("ids") List<Long> ids, @Param("orderedIds") String orderedIds);
    Page<Consultation> findAllByConsultationAnswerIsNotNull(Pageable pageable);
    Page<Consultation> findAllByConsultationAnswerIsNotNullAndConsultationTypeNot(ConsultationType consultationType, Pageable pageable);


    List<Consultation> findAllByConsultationAnswerIsNotNullAndSpecializationIdAndConsultationTypeNot(Long specializationId, ConsultationType consultationType, Pageable pageable);
    Page<Consultation> findBySpecializationId(Long specializationId, Pageable pageable);



//    List<Consultation> findAllById(Iterable<Integer> ids);


    List<Consultation> findByBeneficiaryIdAndConsultationStatus(Long beneficiaryId, ConsultationStatus consultationStatus);

    List<Consultation> findAllByConsultationAnswerIsNotNullAndBeneficiaryId(Long beneficiaryId, Pageable pageable);

    List<Consultation> findAllByConsultationAnswerIsNullAndSpecializationId(Long specializationId);

    List<Consultation> findAllBySpecializationIsPublicTrue();

    @Query("SELECT c FROM Consultation c WHERE c.consultationAnswer IS NULL AND (c.specialization.id = :specializationId OR c.specialization.isPublic = TRUE)")
    List<Consultation> findByConsultationAnswerIsNullAndSpecializationIdOrSpecializationIsPublicTrue(@Param("specializationId") Long specializationId, Pageable pageable);

    List<Consultation> findAllByDoctorId(Long doctorId, Pageable pageable);

    // to get the count of answered consultation by doctor
    @Query("SELECT COUNT(c) FROM Consultation c WHERE c.consultationStatus IN ('ARCHIVED', 'ACTIVE') AND c.doctor.id = :doctorId")
    long countAnsweredConsultationsByDoctorId(@Param("doctorId") Long doctorId);

    @Query("SELECT new com.grad.akemha.dto.statistic.StatisticCountResponse(" +
            "YEAR(u.createTime), MONTH(u.createTime), COUNT(u)) " +
            "FROM Consultation u " +
            "GROUP BY YEAR(u.createTime), MONTH(u.createTime) " +
            "ORDER BY YEAR(u.createTime), MONTH(u.createTime)")
    List<StatisticCountResponse> countConsultationsByMonth();
    @Query("SELECT new com.grad.akemha.dto.statistic.SpecializationConsultationCountResponse(" +
            "s.specializationType, COUNT(c)) " +
            "FROM Consultation c " +
            "JOIN c.specialization s " +
            "GROUP BY s.specializationType")
    List<SpecializationConsultationCountResponse> countConsultationsBySpecialization();

    Page<Consultation> findAllByConsultationAnswerIsNotNullAndConsultationTypeNotAndConsultationStatusNot(
            ConsultationType consultationType,
            ConsultationStatus consultationStatus,
            Pageable pageable);

}
