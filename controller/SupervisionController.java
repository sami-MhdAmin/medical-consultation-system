package com.grad.akemha.controller;

import com.grad.akemha.dto.BaseResponse;
import com.grad.akemha.dto.supervision.request.SendNotificationToSupervisedRequest;
import com.grad.akemha.dto.supervision.response.SupervisionResponse;
import com.grad.akemha.dto.user.response.UserLessResponse;
import com.grad.akemha.service.SupervisionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/supervision")
public class SupervisionController {
    @Autowired
    private SupervisionService supervisionService;

    @PreAuthorize("hasRole('DOCTOR') or hasRole('USER')")
    @PostMapping("/request/{supervisedId}")
    public ResponseEntity<BaseResponse<String>> sendSupervisionRequest(
            @PathVariable Long supervisedId,
            @RequestHeader HttpHeaders httpHeaders
    ) {
        supervisionService.sendSupervisionRequest(supervisedId, httpHeaders);
        return ResponseEntity.ok().body(new BaseResponse<>
                (HttpStatus.OK.value(), "Request sent successfully", null));
    }

    @PreAuthorize("hasRole('DOCTOR') or hasRole('USER')")
    @GetMapping("/requests")
    public ResponseEntity<BaseResponse<List<SupervisionResponse>>> viewSupervisionRequest(
            @RequestHeader HttpHeaders httpHeaders
    ) {
        List<SupervisionResponse> supervisionList = supervisionService.viewSupervisionRequest(httpHeaders);
        return ResponseEntity.ok().body(new BaseResponse<>
                (HttpStatus.OK.value(), "Supervision requests successfully", supervisionList));
    }

    @PreAuthorize("hasRole('DOCTOR') or hasRole('USER')")
    @PostMapping("/reply/{supervisionId}")
    public ResponseEntity<BaseResponse<String>> replyToSupervisionRequest(
            @PathVariable Long supervisionId,
            @RequestHeader HttpHeaders httpHeaders
    ) {
        supervisionService.replyToSupervisionRequest(supervisionId, httpHeaders);
        return ResponseEntity.ok().body(new BaseResponse<>
                (HttpStatus.OK.value(), "The reply arrived successfully", null));
    }

    @PreAuthorize("hasRole('DOCTOR') or hasRole('USER')")
    @GetMapping("/approved/supervised")
    public ResponseEntity<BaseResponse<List<SupervisionResponse>>> getApprovedSupervisionBySupervised(
            @RequestHeader HttpHeaders httpHeaders
    ) {
        List<SupervisionResponse> supervisionList = supervisionService.getApprovedSupervisionBySupervised(httpHeaders);
        return ResponseEntity.ok().body(new BaseResponse<>
                (HttpStatus.OK.value(), "Approved supervision successfully", supervisionList));
    }

    @PreAuthorize("hasRole('DOCTOR') or hasRole('USER')")
    @GetMapping("/approved/supervisor")
    public ResponseEntity<BaseResponse<List<SupervisionResponse>>> getApprovedSupervisionBySupervisor(
            @RequestHeader HttpHeaders httpHeaders
    ) {
        List<SupervisionResponse> supervisionList = supervisionService.getApprovedSupervisionBySupervisor(httpHeaders);
        return ResponseEntity.ok().body(new BaseResponse<>
                (HttpStatus.OK.value(), "Approved supervision successfully", supervisionList));
    }

    @PreAuthorize("hasRole('DOCTOR') or hasRole('USER')")
    @DeleteMapping("/{supervisionId}")
    public ResponseEntity<BaseResponse<String>> deleteSupervision(
            @PathVariable Long supervisionId
    ) {
        supervisionService.deleteApprovedSupervision(supervisionId);
        return ResponseEntity.ok().body(new BaseResponse<>
                (HttpStatus.OK.value(), "The supervision deleted successfully", null));
    }


    @PreAuthorize("hasRole('USER') or hasRole('DOCTOR')")
    @GetMapping("/possible_supervisor")
    public ResponseEntity<BaseResponse<List<UserLessResponse>>> getRandomTenUsers(@RequestHeader HttpHeaders httpHeaders) {
        List<UserLessResponse> response = supervisionService.returnRandomTenUser(httpHeaders);
        return ResponseEntity.ok()
                .body(new BaseResponse<>(HttpStatus.OK.value(), "successfully", response));
    }

    @PreAuthorize("hasRole('USER') or hasRole('DOCTOR')")
    @GetMapping("/possible_supervisor/keyword")
    public ResponseEntity<BaseResponse<List<UserLessResponse>>> getTenUsersByKeyword(@RequestParam String keyword, @RequestHeader HttpHeaders httpHeaders) {
        List<UserLessResponse> response = supervisionService.returnTenUserByKeyword(keyword, httpHeaders);
        return ResponseEntity.ok()
                .body(new BaseResponse<>(HttpStatus.OK.value(), "successfully", response));
    }

    // send notification
    @PreAuthorize("hasRole('USER') or hasRole('DOCTOR')")
    @PostMapping("/send_reminder/{supervisedId}")
    public ResponseEntity<BaseResponse<String>> sendNotificationToSupervisedUser(
            @RequestBody SendNotificationToSupervisedRequest sendNotificationToSupervisedRequest,
            @PathVariable Long supervisedId,
            @RequestHeader HttpHeaders httpHeaders) throws ExecutionException, InterruptedException {
        String response = supervisionService.sendNotificationToSupervisedUser(sendNotificationToSupervisedRequest, supervisedId, httpHeaders);
        return ResponseEntity.ok()
                .body(new BaseResponse<>(HttpStatus.OK.value(), "successfully", response));
    }
}
