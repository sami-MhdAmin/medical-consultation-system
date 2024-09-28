package com.grad.akemha.controller;

import com.grad.akemha.dto.BaseResponse;
import com.grad.akemha.dto.doctor.AddDoctorRequest;
import com.grad.akemha.entity.DoctorRequest;
import com.grad.akemha.entity.User;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import com.grad.akemha.service.DoctorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/admin/doctor")
public class DoctorController {
    @Autowired
    DoctorService doctorService;

    @PreAuthorize("hasRole('USER') or hasRole('DOCTOR') or hasRole('OWNER')")
    @GetMapping()
    public ResponseEntity<BaseResponse<Page<User>>> getDoctors(@RequestParam(name = "page", defaultValue = "0") int page) {
        Page<User> doctorsPage = doctorService.getDoctors(page);
        return ResponseEntity.ok().body(new BaseResponse<>(HttpStatus.OK.value(), "doctors", doctorsPage));
    }

    @PreAuthorize("hasRole('USER') or hasRole('DOCTOR') or hasRole('OWNER')")
    @GetMapping("/{specializationId}")
    public ResponseEntity<BaseResponse<Page<User>>> getDoctorsBySpecialization(@PathVariable Long specializationId, @RequestParam(name = "page", defaultValue = "0") int page) {
        Page<User> doctorsPage = doctorService.getDoctorsBySpecialization(specializationId, page);
        return ResponseEntity.ok().body(new BaseResponse<>(HttpStatus.OK.value(), "doctors", doctorsPage));
    }


    @PreAuthorize("hasRole('OWNER')")
    @PostMapping()
    public ResponseEntity<BaseResponse<?>> addDoctor(@Valid @RequestBody AddDoctorRequest request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            StringBuilder errorMessage = new StringBuilder();
            bindingResult.getFieldErrors().forEach(error -> {
                errorMessage.append(error.getDefaultMessage()).append("; ");
            });
            errorMessage.delete(errorMessage.length() - 2, errorMessage.length() - 1);
            return ResponseEntity.badRequest()
                    .body(new BaseResponse<>(HttpStatus.BAD_REQUEST.value(), errorMessage.toString(), null));
        }
        doctorService.addDoctor(request);
        return ResponseEntity.ok().body(new BaseResponse<>(HttpStatus.OK.value(), "تمت إضافة الطبيب بنجاح !", null));
    }

    @PreAuthorize("hasRole('OWNER')")
    @DeleteMapping("/{userId}")
    public ResponseEntity<BaseResponse<String>> deleteDoctor(@PathVariable Long userId) {
        doctorService.deleteDoctor(userId);
        return ResponseEntity.ok().body(new BaseResponse<>(HttpStatus.OK.value(), "تمت حذف الطبيب بنجاح !", null));

    }


    @PreAuthorize("hasRole('OWNER')")
    @GetMapping("/statistic")
    public ResponseEntity<BaseResponse<Map<Integer, List<Map<String, Object>>>>> statistic() {
        return ResponseEntity.ok().body(new BaseResponse<>(HttpStatus.OK.value(), "statistic", doctorService.getDoctorCountByMonth()));

    }

    @PreAuthorize("hasRole('OWNER')")
    @GetMapping("/doctor_request")
    public ResponseEntity<BaseResponse<Page<DoctorRequest>>> doctorRequest(@RequestParam(name = "page", defaultValue = "0") Integer page) {
        return ResponseEntity.ok().body(new BaseResponse<>(HttpStatus.OK.value(), "doctor requests", doctorService.doctorRequest(page)));
    }

    @PreAuthorize("hasRole('OWNER')")
    @PostMapping("/doctor_request/{requestId}")
    public ResponseEntity<BaseResponse<Long>> acceptDoctorRequest(@PathVariable Long requestId) throws ExecutionException, InterruptedException {

        return ResponseEntity.ok().body(new BaseResponse<>(HttpStatus.OK.value(), "تم قبول الطلب بنجاح !", doctorService.acceptDoctorRequest(requestId)));
    }

    @PreAuthorize("hasRole('OWNER')")
    @PatchMapping("/doctor_request/{requestId}")
    public ResponseEntity<BaseResponse<Long>> rejectDoctorRequest(@PathVariable Long requestId) {

        return ResponseEntity.ok().body(new BaseResponse<>(HttpStatus.OK.value(), "تم رفض الطلب بنجاح !", doctorService.rejectDoctorRequest(requestId)));
    }

    @PreAuthorize("hasRole('OWNER')")
    @GetMapping("/doctor_request/non_answered")
    public ResponseEntity<BaseResponse<Long>> doctorRequestNonAnswered() {
        return ResponseEntity.ok().body(new BaseResponse<>(HttpStatus.OK.value(), "doctor requests", doctorService.doctorRequestNonAnswered()));
    }


}

