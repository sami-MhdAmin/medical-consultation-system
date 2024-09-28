package com.grad.akemha.controller;

import com.grad.akemha.dto.BaseResponse;
import com.grad.akemha.dto.medicine.AddMedicineRequest;
import com.grad.akemha.dto.medicine.MedicineResponse;
import com.grad.akemha.dto.medicine.TakeMedicineRequest;
import com.grad.akemha.entity.Medicine;
import com.grad.akemha.service.MedicineNotebookService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@PreAuthorize("hasRole('USER')")
@RequestMapping("/api/medicine")
public class MedicineNotebookController {

    @Autowired
    MedicineNotebookService medicalNotebookService;

    @GetMapping()
    public ResponseEntity<BaseResponse<List<MedicineResponse>>> getMedicine(@RequestHeader HttpHeaders httpHeaders) {
        List<Medicine> medicines = medicalNotebookService.getMedicine(httpHeaders);
        List<MedicineResponse> responsePage = medicines.stream().map(MedicineResponse::new).toList();

        return ResponseEntity.ok().body(new BaseResponse<>(HttpStatus.OK.value(), "medicines", responsePage));
    }

    @PostMapping()
    public ResponseEntity<BaseResponse<String>> addMedicine(
            @Valid @RequestBody AddMedicineRequest request,
            @RequestHeader HttpHeaders httpHeaders,BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            System.out.println("hello sami");
            StringBuilder errorMessage = new StringBuilder();
            bindingResult.getFieldErrors().forEach(error -> {
                errorMessage.append(error.getDefaultMessage()).append("; ");
            });
            errorMessage.delete(errorMessage.length() - 2, errorMessage.length() - 1); // Remove the last "; "
            return ResponseEntity.badRequest()
                    .body(new BaseResponse<>(HttpStatus.BAD_REQUEST.value(), errorMessage.toString(), null));
        }
        System.out.println("hello sami");
        System.out.println("AlarmRoutine: " + request.getAlarmRoutine());
        System.out.println("AlarmRoutineType: " + request.getAlarmRoutineType());
        System.out.println("AlarmWeekDay: " + request.getAlarmWeekDay());
        System.out.println("MedicamentName: " + request.getMedicamentName());
        System.out.println("MedicamentType: " + request.getMedicamentType());
        System.out.println("AlarmTimes: " + request.getAlarmTimes());
        System.out.println("EndDate: " + request.getEndDate());
        System.out.println("SelectedDayInMonth: " + request.getSelectedDayInMonth());
        System.out.println("StartDate: " + request.getStartDate());
        System.out.println("getUselessId: " + request.getId());
        System.out.println("********************************************************************");
        medicalNotebookService.addMedicine(request, httpHeaders);
        return ResponseEntity.ok()
                .body(new BaseResponse<>(HttpStatus.OK.value(), "Medicine added successfully", "okkkkkkkkkkk"));
    }

    @DeleteMapping("/{medicineId}")
    public ResponseEntity<BaseResponse<?>> deleteMedicine(@PathVariable Long medicineId, @RequestHeader HttpHeaders httpHeaders) {
        System.out.println("I am in delete medicine");
        medicalNotebookService.deleteMedicine(medicineId, httpHeaders);
        return ResponseEntity.ok().body(new BaseResponse<>(HttpStatus.OK.value(), "medicine delete successfully", null));
    }

    @PostMapping("/take/{localAlarmId}")
    public ResponseEntity<BaseResponse<?>> takeMedicine(@PathVariable Long localAlarmId,  @Valid @RequestBody TakeMedicineRequest request,
                                                        @RequestHeader HttpHeaders httpHeaders,BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            System.out.println("hello sami");
            StringBuilder errorMessage = new StringBuilder();
            bindingResult.getFieldErrors().forEach(error -> {
                errorMessage.append(error.getDefaultMessage()).append("; ");
            });
            errorMessage.delete(errorMessage.length() - 2, errorMessage.length() - 1); // Remove the last "; "
            return ResponseEntity.badRequest()
                    .body(new BaseResponse<>(HttpStatus.BAD_REQUEST.value(), errorMessage.toString(), null));
        }
        System.out.println("takeMedicine");
        medicalNotebookService.takeMedicine(localAlarmId,request, httpHeaders);
        return ResponseEntity.ok().body(new BaseResponse<>(HttpStatus.OK.value(), "successfully", null));
    }
    @PreAuthorize("hasRole('USER') or hasRole('DOCTOR') or hasRole('OWNER')")
    @GetMapping("beneficiary/{supervisedId}")
    public ResponseEntity<BaseResponse<?>> getSupervisedMedicineState(@PathVariable Long supervisedId,
                                                                      @RequestHeader HttpHeaders httpHeaders) {
        System.out.println("takeMedicine");
        var supervisedMedicineList = medicalNotebookService.getSupervisedMedicineState(supervisedId, httpHeaders);
        return ResponseEntity.ok().body(new BaseResponse<>(HttpStatus.OK.value(), "successfully", supervisedMedicineList));
    }

    @GetMapping("beneficiary/today/{supervisedId}")
    public ResponseEntity<BaseResponse<?>> getSupervisedTodayMedicineState(@PathVariable Long supervisedId,
                                                                      @RequestHeader HttpHeaders httpHeaders) {
        //return the statues is it taken or not depends on take_date in alarm history
        System.out.println("takeMedicine");
        var printList = medicalNotebookService.getSupervisedTodayMedicineState(supervisedId, httpHeaders);
        return ResponseEntity.ok().body(new BaseResponse<>(HttpStatus.OK.value(), "successfully", printList));
    }
}
