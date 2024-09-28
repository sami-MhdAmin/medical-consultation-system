package com.grad.akemha.controller;

import com.grad.akemha.dto.BaseResponse;
import com.grad.akemha.dto.specializationDTO.SpecializationRequest;
import com.grad.akemha.dto.statistic.SpecializationUserCountResponse;
import com.grad.akemha.entity.Specialization;
import com.grad.akemha.service.SpecializationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/specialization")
public class SpecializationController {

    @Autowired
    private SpecializationService specializationService;

//    @PreAuthorize("hasRole('USER') or hasRole('OWNER') or hasRole('DOCTOR')")
    @GetMapping()
    public ResponseEntity<BaseResponse<List<Specialization>>> getSpecializations() {
        List<Specialization> specializations = specializationService.getSpecializations();
        return ResponseEntity.ok().body(new BaseResponse<>(HttpStatus.OK.value(), "specializations", specializations));
    }

    @PreAuthorize("hasRole('USER') or hasRole('OWNER') or hasRole('DOCTOR')")
    @GetMapping("/consultations")
    public ResponseEntity<BaseResponse<List<Specialization>>> getSpecializationsWithConsultation() {
        List<Specialization> specializations = specializationService.getSpecializationsWithConsultation();
        return ResponseEntity.ok().body(new BaseResponse<>(HttpStatus.OK.value(), "specializations", specializations));
    }


    @PreAuthorize("hasRole('USER') or hasRole('OWNER') or hasRole('DOCTOR')")
    @GetMapping("/doctors")
    public ResponseEntity<BaseResponse<List<Specialization>>> getSpecializationsWithDoctor() {
        List<Specialization> specializations = specializationService.getSpecializationsWithDoctor();
        return ResponseEntity.ok().body(new BaseResponse<>(HttpStatus.OK.value(), "specializations", specializations));
    }


    @PreAuthorize("hasRole('OWNER')")
    @DeleteMapping("/{specializationId}")
    // TODO: note: all consultation will be deleted because of casecade. solve it later
    public ResponseEntity<BaseResponse<Specialization>> deleteSpecializationById(@PathVariable Long specializationId) {
        Specialization response = specializationService.deleteSpecializationById(specializationId);
        return ResponseEntity.ok().body(new BaseResponse<>(HttpStatus.OK.value(), "specializations", response));
    }

    @PreAuthorize("hasRole('OWNER')")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BaseResponse<Specialization>> addSpecialization(@ModelAttribute SpecializationRequest request) {
        Specialization savedSpecialization = specializationService.addSpecialization(request);
        return ResponseEntity.ok().body(new BaseResponse<>(HttpStatus.OK.value(), "تم إضافة التخصص بنجاح !", savedSpecialization));
    }


    @PreAuthorize("hasRole('USER') or hasRole('OWNER') or hasRole('DOCTOR')")
    @GetMapping("/doctor_specializations")
    public ResponseEntity<BaseResponse<List<Specialization>>> getSpecializationsNotPublic() {
        List<Specialization> specializations = specializationService.getSpecializationsNotPublic();
        return ResponseEntity.ok().body(new BaseResponse<>(HttpStatus.OK.value(), "specializations", specializations));
    }

    @GetMapping("/new_doctor_specializations")
    public ResponseEntity<BaseResponse<List<Specialization>>> getSpecializationsNotPublicWithoutToken() {
        List<Specialization> specializations = specializationService.getSpecializationsNotPublic();
        return ResponseEntity.ok().body(new BaseResponse<>(HttpStatus.OK.value(), "specializations", specializations));
    }

    @PreAuthorize("hasRole('OWNER')")
    @GetMapping("/statistic")
    public ResponseEntity<BaseResponse<List<SpecializationUserCountResponse>>> countUsersBySpecialization() {
        return ResponseEntity.ok().body(new BaseResponse<>(HttpStatus.OK.value(), "statistic", specializationService.countUsersBySpecialization()));
    }
}
