package com.grad.akemha.controller;

import com.grad.akemha.dto.BaseResponse;
import com.grad.akemha.dto.consultation.consultationRequest.AnswerConsultationRequest;
import com.grad.akemha.dto.consultation.consultationRequest.RateConsultationReq;
import com.grad.akemha.dto.consultation.consultationResponse.ConsultationRes;
import com.grad.akemha.dto.message.response.MessageResponse;
import com.grad.akemha.dto.statistic.SpecializationConsultationCountResponse;
import com.grad.akemha.entity.Consultation;
import com.grad.akemha.entity.Message;
import com.grad.akemha.entity.enums.ConsultationType;
import com.grad.akemha.service.ConsultationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/consultation")
public class ConsultationController {
    @Autowired
    private ConsultationService consultationService;


    @PreAuthorize("hasRole('USER') or hasRole('OWNER') or hasRole('DOCTOR')")
    @GetMapping()
    public ResponseEntity<BaseResponse<Page<ConsultationRes>>> getAllConsultations(
            @RequestParam(name = "page", defaultValue = "0") Integer page) { //ConsultationResponse

        Page<Consultation> consultationPage = consultationService.getAllConsultations(page);
        Page<ConsultationRes> responsePage = consultationPage.map(ConsultationRes::new);
        return ResponseEntity.ok()
                .body(new BaseResponse<>(HttpStatus.OK.value(), "successfully", responsePage));
    }


    @PreAuthorize("hasRole('OWNER') or hasRole('DOCTOR')")
    @GetMapping("/answered")
    public ResponseEntity<BaseResponse<List<ConsultationRes>>> getAllAnsweredConsultations(
            @RequestParam(name = "page", defaultValue = "0") Integer page
    ) { //ConsultationResponse
        List<ConsultationRes> response = consultationService.getAllAnsweredConsultations(page);
        return ResponseEntity.ok()
                .body(new BaseResponse<>(HttpStatus.OK.value(), "successfully", response));
    }

    @PreAuthorize("hasRole('USER') or hasRole('DOCTOR')")
    @GetMapping("/answered/beneficiary")
    public ResponseEntity<BaseResponse<List<ConsultationRes>>> getAllAnsweredConsultationsExceptPrivateAndExceptActive(  //this API is for beneficiary
                                                                                                          @RequestParam(name = "page", defaultValue = "0") Integer page
    ) { //ConsultationResponse
        List<ConsultationRes> response = consultationService.getAllAnsweredConsultationsExceptPrivateAndExceptActive(page);
        return ResponseEntity.ok()
                .body(new BaseResponse<>(HttpStatus.OK.value(), "successfully", response));
    }

    @PreAuthorize("hasRole('OWNER') or hasRole('DOCTOR')")
    @GetMapping("/{specializationId}")
    public ResponseEntity<BaseResponse<List<ConsultationRes>>> getConsultationsBySpecialization(
            @PathVariable Long specializationId,
            @RequestParam(name = "page", defaultValue = "0") Integer page) {
        List<ConsultationRes> response = consultationService.getConsultationsBySpecializationId(specializationId, page);
        return ResponseEntity.ok()
                .body(new BaseResponse<>(HttpStatus.OK.value(), "successfully", response));
    }


    @PreAuthorize("hasRole('OWNER')")
    @GetMapping("/admin/{specializationId}")
    public ResponseEntity<BaseResponse<Page<ConsultationRes>>> adminGetConsultationsBySpecialization(
            @PathVariable Long specializationId,
            @RequestParam(name = "page", defaultValue = "0") Integer page) {
        Page<ConsultationRes> consultationResPage = consultationService.adminGetConsultationsBySpecialization(specializationId, page);
        return ResponseEntity.ok()
                .body(new BaseResponse<>(HttpStatus.OK.value(), "Successfully retrieved consultations", consultationResPage));
    }

    @PreAuthorize("hasRole('USER') or hasRole('OWNER') or hasRole('DOCTOR')")
    @GetMapping("/answered/{specializationId}")
    public ResponseEntity<BaseResponse<List<ConsultationRes>>> getAnsweredConsultationsBySpecializationId( //answered + not private + by specialization
                                                                                                           @PathVariable Long specializationId,
                                                                                                           @RequestParam(name = "page", defaultValue = "0") Integer page) {
        List<ConsultationRes> response = consultationService.getAnsweredConsultationsBySpecializationId(specializationId, page);
        return ResponseEntity.ok()
                .body(new BaseResponse<>(HttpStatus.OK.value(), "successfully", response));
    }

//    @PreAuthorize("hasRole('USER') or hasRole('OWNER') or hasRole('DOCTOR')")
//    @GetMapping("/{specialization}")
//    public ResponseEntity<BaseResponse<List<ConsultationRes>>> getPendingConsultationsBySpecialization(@PathVariable String specialization) { //TODO get only Pending
//        List<ConsultationRes> response = consultationService.getConsultationsBySpecialization(specialization);
//        return ResponseEntity.ok()
//                .body(new BaseResponse<>(HttpStatus.OK.value(), "successfully", response));
//    }

    @PreAuthorize("hasRole('USER') or hasRole('OWNER') or hasRole('DOCTOR')")
    @GetMapping("/keyword")
    public ResponseEntity<BaseResponse<List<ConsultationRes>>> getConsultationsByKeyword(@RequestParam String keyword) {
        List<ConsultationRes> response = consultationService.findConsultationsByKeyword(keyword);
        return ResponseEntity.ok()
                .body(new BaseResponse<>(HttpStatus.OK.value(), "successfully", response));
    }


    @PreAuthorize("hasRole('USER') or hasRole('OWNER') or hasRole('DOCTOR')")
    @GetMapping("/search")
    public ResponseEntity<BaseResponse<List<ConsultationRes>>> searchConsultation(@RequestParam String keyword, @RequestParam(name = "page", defaultValue = "0") Integer page) throws IOException {
        List<ConsultationRes> response = consultationService.searchConsultation(keyword, page);
        return ResponseEntity.ok()
                .body(new BaseResponse<>(HttpStatus.OK.value(), "successfully", response));
    }
    @PreAuthorize("hasRole('USER') or hasRole('OWNER') or hasRole('DOCTOR')")
    @GetMapping("/search/admin")
    public ResponseEntity<BaseResponse<Map<String, List<ConsultationRes>>>> adminSearchConsultation(@RequestParam String keyword, @RequestParam(name = "page", defaultValue = "0") Integer page) throws IOException {
        Map<String, List<ConsultationRes>> response = consultationService.adminSearchConsultation(keyword, page);
        return ResponseEntity.ok()
                .body(new BaseResponse<>(HttpStatus.OK.value(), "successfully", response));
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BaseResponse<Consultation>> postConsultation(
//            @RequestPart("request") ConsultationRequest request,
//            @RequestPart(value = "file", required = false) List<MultipartFile> files,
            @RequestParam(value = "title", required = true) String title,
            @RequestParam(value = "consultationText", required = true) String consultationText,
            @RequestParam(value = "specializationId", required = true) Long specializationId,
            @RequestParam(value = "consultationType", required = true) ConsultationType consultationType,
            @RequestParam(value = "files", required = false) List<MultipartFile> files,
            @RequestHeader HttpHeaders httpHeaders) {
        try {
            Consultation response = consultationService.postConsultation(httpHeaders, title, consultationText, specializationId, consultationType, files);
            return ResponseEntity.ok()
                    .body(new BaseResponse<>(HttpStatus.OK.value(), "Consultation added successfully", response));
        } catch (NumberFormatException e) {
            System.out.println("Invalid input: " + e.getMessage());
            return ResponseEntity.ok()
                    .body(new BaseResponse<>(HttpStatus.BAD_REQUEST.value(), "failed", null));
        }
    }


    @PatchMapping("/")
    public ResponseEntity<BaseResponse<Consultation>> answerConsultation(@RequestBody AnswerConsultationRequest request, @RequestHeader HttpHeaders httpHeaders) {
        try {
            Consultation response = consultationService.answerConsultation(request, httpHeaders);
            return ResponseEntity.ok()
                    .body(new BaseResponse<>(HttpStatus.OK.value(), "successfully", null));
        } catch (NumberFormatException e) {
            System.out.println("Invalid input: " + e.getMessage());
            return ResponseEntity.ok()
                    .body(new BaseResponse<>(HttpStatus.BAD_REQUEST.value(), "failed", null));
        }
    }


    @PreAuthorize("hasRole('USER') or hasRole('OWNER') or hasRole('DOCTOR')")
    @GetMapping("/PersonalNullConsultations")
    public ResponseEntity<BaseResponse<List<ConsultationRes>>> getPersonalNullConsultations(@RequestHeader HttpHeaders httpHeaders) { //P.11
        List<ConsultationRes> response = consultationService.getPersonalNullConsultations(httpHeaders);
        return ResponseEntity.ok()
                .body(new BaseResponse<>(HttpStatus.OK.value(), "successfully", response));
    }

    @PreAuthorize("hasRole('USER') or hasRole('OWNER') or hasRole('DOCTOR')")
    @GetMapping("/PersonalAnsweredConsultations")
    public ResponseEntity<BaseResponse<List<ConsultationRes>>> getPersonalAnsweredConsultations(
            @RequestHeader HttpHeaders httpHeaders,
            @RequestParam(name = "page", defaultValue = "0") Integer page) { //P.11
        List<ConsultationRes> response = consultationService.getPersonalAnsweredConsultations(httpHeaders, page);
        return ResponseEntity.ok()
                .body(new BaseResponse<>(HttpStatus.OK.value(), "successfully", response));
    }

    @PreAuthorize("hasRole('DOCTOR')")
    @GetMapping("/PendingConsultationsForDoctor")
    public ResponseEntity<BaseResponse<List<ConsultationRes>>> getPendingConsultationsForDoctor(
            @RequestHeader HttpHeaders httpHeaders,
            @RequestParam(name = "page", defaultValue = "0") Integer page) { // P.12
        List<ConsultationRes> response = consultationService.getPendingConsultationsForDoctor(httpHeaders, page);
        return ResponseEntity.ok()
                .body(new BaseResponse<>(HttpStatus.OK.value(), "successfully", response));
    }

    @PreAuthorize("hasRole('USER') or hasRole('OWNER') or hasRole('DOCTOR')")
    @GetMapping("/getBeneficiaryAnsweredConsultations/{beneficiaryId}")
    public ResponseEntity<BaseResponse<List<ConsultationRes>>> getBeneficiaryAnsweredConsultations(
            @RequestHeader HttpHeaders httpHeaders,
            @PathVariable Long beneficiaryId,
            @RequestParam(name = "page", defaultValue = "0") Integer page) { //P.13
        List<ConsultationRes> response = consultationService.getBeneficiaryAnsweredConsultations(beneficiaryId, page);
        return ResponseEntity.ok()
                .body(new BaseResponse<>(HttpStatus.OK.value(), "successfully", response));
    }

    @PreAuthorize("hasRole('DOCTOR')")
    @GetMapping("/getDoctorAnsweredConsultations")
    public ResponseEntity<BaseResponse<List<ConsultationRes>>> getDoctorAnsweredConsultations(
            @RequestHeader HttpHeaders httpHeaders,
            @RequestParam(name = "page", defaultValue = "0") Integer page) { //P.14
        List<ConsultationRes> response = consultationService.getDoctorAnsweredConsultations(httpHeaders, page);
        return ResponseEntity.ok()
                .body(new BaseResponse<>(HttpStatus.OK.value(), "successfully", response));
    }

    @PreAuthorize("hasRole('OWNER') or hasRole('DOCTOR')")
    @DeleteMapping("/{consultationId}")
    public ResponseEntity<BaseResponse> deleteConsultation(
            @PathVariable Long consultationId) throws IOException {
        consultationService.deleteConsultation(consultationId);
        return ResponseEntity.ok()
                .body(new BaseResponse<>(HttpStatus.OK.value(), "successfully", null));
    }

    @PreAuthorize("hasRole('OWNER') or hasRole('DOCTOR')")
    @PatchMapping("/anonymous/{consultationId}")
    public ResponseEntity<BaseResponse<Consultation>> makeConsultationAnonymous(@PathVariable Long consultationId) {
        try {
            consultationService.makeConsultationAnonymous(consultationId);
            return ResponseEntity.ok()
                    .body(new BaseResponse<>(HttpStatus.OK.value(), "successfully", null));
        } catch (NumberFormatException e) {
            System.out.println("Invalid input: " + e.getMessage());
            return ResponseEntity.ok()
                    .body(new BaseResponse<>(HttpStatus.BAD_REQUEST.value(), "failed", null));
        }
    }

    @PreAuthorize("hasRole('OWNER')")
    @GetMapping("/statistic/count")
    public ResponseEntity<BaseResponse<Map<Integer, List<Map<String, Object>>>>> statistic() {
        return ResponseEntity.ok().body(new BaseResponse<>(HttpStatus.OK.value(), "statistic", consultationService.countConsultationsByMonth()));
    }


    @PreAuthorize("hasRole('OWNER')")
    @GetMapping("/statistic/specialization")
    public ResponseEntity<BaseResponse<List<SpecializationConsultationCountResponse>>> countConsultationsBySpecialization() {
        return ResponseEntity.ok().body(new BaseResponse<>(HttpStatus.OK.value(), "statistic", consultationService.countConsultationsBySpecialization()));
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/{id}/rate")
    public ResponseEntity<BaseResponse<?>> rateConsultation(
            @PathVariable Long id,
            @RequestBody RateConsultationReq rate) {
        consultationService.rateConsultation(id, rate.rating());
        return ResponseEntity.ok().body(new BaseResponse<>(HttpStatus.OK.value(), "your ratting is added", null));
    }

    @PreAuthorize("hasRole('DOCTOR')")
    @PostMapping("/consultation/chatStatus/{consultationId}/{status}")
    public ResponseEntity<BaseResponse<?>> changeConsultationChatStatus(
            @PathVariable Long consultationId,
            @PathVariable Boolean status
    ) {
        Consultation consultation = consultationService.changeConsultationChatStatus(consultationId,status);
        return ResponseEntity.ok().body(new BaseResponse<>(HttpStatus.OK.value(), "messages", "chat status changed"));
    }
}
