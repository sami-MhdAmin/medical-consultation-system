package com.grad.akemha.controller;

import com.grad.akemha.dto.BaseResponse;
import com.grad.akemha.dto.medicalDevice.AddDeviceRequest;
import com.grad.akemha.dto.medicalDevice.ChangeQuantityRequest;
import com.grad.akemha.dto.medicalDevice.ReservationResponse;
import com.grad.akemha.dto.medicalDevice.ReserveDeviceRequest;
import com.grad.akemha.entity.DeviceReservation;
import com.grad.akemha.entity.MedicalDevice;
import com.grad.akemha.service.MedicalDeviceService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/medical-device")
public class MedicalDeviceController {
    @Autowired
    MedicalDeviceService medicalDeviceService;

    @PreAuthorize("hasRole('USER') or hasRole('OWNER') or hasRole('DOCTOR')")
    @GetMapping()
    public ResponseEntity<BaseResponse<List<MedicalDevice>>> getDevices(@RequestParam(name = "page", defaultValue = "0") Integer page) {
        List<MedicalDevice> devices = medicalDeviceService.getDevices(page);
        return ResponseEntity.ok().body(new BaseResponse<>(HttpStatus.OK.value(), "devices", devices));
    }


    @PreAuthorize("hasRole('OWNER')")
    @GetMapping("/admin")
    public ResponseEntity<BaseResponse<Page<MedicalDevice>>> getDevicesAdmin(@RequestParam(name = "page", defaultValue = "0") Integer page) {
        Page<MedicalDevice> devicePage = (Page<MedicalDevice>) medicalDeviceService.getDevicesAdmin(page);
        return ResponseEntity.ok().body(new BaseResponse<>
                (HttpStatus.OK.value(), "All devices", devicePage));
    }


    @PreAuthorize("hasRole('OWNER')")
    @PostMapping()
    public ResponseEntity<BaseResponse<?>> addDevice(@Valid @ModelAttribute AddDeviceRequest request, BindingResult bindingResult, @RequestHeader HttpHeaders httpHeaders) {
        if (bindingResult.hasErrors()) {
            StringBuilder errorMessage = new StringBuilder();
            bindingResult.getFieldErrors().forEach(error -> {
                errorMessage.append(error.getDefaultMessage()).append("; ");
            });
            errorMessage.delete(errorMessage.length() - 2, errorMessage.length() - 1); // Remove the last "; "
            return ResponseEntity.badRequest()
                    .body(new BaseResponse<>(HttpStatus.BAD_REQUEST.value(), errorMessage.toString(), null));
        }
        medicalDeviceService.addDevice(request, httpHeaders);
        return ResponseEntity.ok().body(new BaseResponse<>(HttpStatus.OK.value(), "تم إضافة الجهاز بنجاح !", null));
    }

    @PreAuthorize("hasRole('OWNER')")
    @DeleteMapping("/{medicalDeviceId}")
    public ResponseEntity<BaseResponse<String>> deleteDevice(@PathVariable Long medicalDeviceId) {
        medicalDeviceService.deleteDevice(medicalDeviceId);
        return ResponseEntity.ok().body(new BaseResponse<>(HttpStatus.OK.value(), "تم حذف الجهاز بنجاح !", null));

    }

    @PreAuthorize("hasRole('OWNER')")
    @GetMapping("/reservation/{medicalDeviceId}")
    public ResponseEntity<BaseResponse<List<DeviceReservation>>> getReservations(@PathVariable Long medicalDeviceId) {
        return ResponseEntity.ok().body(new BaseResponse<>(HttpStatus.OK.value(), "device reservations", medicalDeviceService.getReservations(medicalDeviceId)));
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/user")
    public ResponseEntity<BaseResponse<List<ReservationResponse>>> getUserReservations(@RequestHeader HttpHeaders httpHeaders) {
        List<DeviceReservation> reservations = medicalDeviceService.getUserReservations(httpHeaders);
        List<ReservationResponse> response = reservations.stream().map(ReservationResponse::new).toList();
        return ResponseEntity.ok().body(new BaseResponse<>(HttpStatus.OK.value(), " user device reservations", response));
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("reserve")
    public ResponseEntity<BaseResponse<?>> reserveDevice(@Valid @RequestBody ReserveDeviceRequest request, BindingResult bindingResult, @RequestHeader HttpHeaders httpHeaders) {
        if (bindingResult.hasErrors()) {
            StringBuilder errorMessage = new StringBuilder();
            bindingResult.getFieldErrors().forEach(error -> {
                errorMessage.append(error.getDefaultMessage()).append("; ");
            });
            errorMessage.delete(errorMessage.length() - 2, errorMessage.length() - 1); // Remove the last "; "
            return ResponseEntity.badRequest()
                    .body(new BaseResponse<>(HttpStatus.BAD_REQUEST.value(), errorMessage.toString(), null));
        }
        medicalDeviceService.reserveDevice(request, httpHeaders);
        return ResponseEntity.ok().body(new BaseResponse<>(HttpStatus.OK.value(), "device reserved successfully", null));
    }

    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("reserve/{deviceReservationId}")
    public ResponseEntity<BaseResponse<?>> deleteDeviceReservation(@PathVariable Long deviceReservationId, @RequestHeader HttpHeaders httpHeaders) {
        medicalDeviceService.deleteDeviceReservation(deviceReservationId, httpHeaders);
        return ResponseEntity.ok().body(new BaseResponse<>(HttpStatus.OK.value(), "device reservation canceled successfully", null));
    }

    @PreAuthorize("hasRole('OWNER')")
    @PostMapping("delivery/{deviceReservationId}")
    public ResponseEntity<BaseResponse<String>> deviceDelivery(@PathVariable Long deviceReservationId) {
        medicalDeviceService.deviceDelivery(deviceReservationId);
        return ResponseEntity.ok().body(new BaseResponse<>(HttpStatus.OK.value(), "تم تسليم الجهاز بنجاح", null));
    }

    @PreAuthorize("hasRole('OWNER')")
    @PostMapping("rewind/{deviceReservationId}")
    public ResponseEntity<BaseResponse<String>> deviceRewind(@PathVariable Long deviceReservationId) {
        medicalDeviceService.deviceRewind(deviceReservationId);
        return ResponseEntity.ok().body(new BaseResponse<>(HttpStatus.OK.value(), "تم استلام الجهاز بنجاح", null));
    }

    @PreAuthorize("hasRole('OWNER')")
    @PatchMapping("changQuantity/{medicalDeviceId}")
    public ResponseEntity<BaseResponse<String>> changQuantity(@RequestBody ChangeQuantityRequest request, @PathVariable Long medicalDeviceId) {
        medicalDeviceService.changQuantity(medicalDeviceId, request.getQuantity());
        return ResponseEntity.ok().body(new BaseResponse<>(HttpStatus.OK.value(), "quantity changed successfully", null));
    }

    @PreAuthorize("hasRole('OWNER')")
    @GetMapping("/statistic")
    public ResponseEntity<BaseResponse<Map<Integer, List<Map<String, Object>>>>> countReservationsByMonth() {
        return ResponseEntity.ok().body(new BaseResponse<>(HttpStatus.OK.value(), "statistic", medicalDeviceService.countReservationsByMonth()));
    }
}
