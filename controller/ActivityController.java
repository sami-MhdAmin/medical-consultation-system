package com.grad.akemha.controller;

import com.grad.akemha.dto.BaseResponse;
import com.grad.akemha.dto.activity.ActivityRequest;
import com.grad.akemha.dto.activity.ActivityResponse;
import com.grad.akemha.entity.Activity;
import com.grad.akemha.service.ActivityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/activity")
@RequiredArgsConstructor
public class ActivityController {
    private final ActivityService activityService;

    // Read
    @PreAuthorize("hasRole('USER') or hasRole('DOCTOR') or hasRole('OWNER')")
    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<ActivityResponse>> getActivityById(
            @PathVariable int id) {
        ActivityResponse response = activityService.getActivityById(id);

        return ResponseEntity.ok().body(new BaseResponse<>
                (HttpStatus.OK.value(), "Activity Found successfully", response));

    }

    @PreAuthorize("hasRole('USER') or hasRole('DOCTOR') or hasRole('OWNER')")
    @GetMapping()
    public ResponseEntity<BaseResponse<Page<ActivityResponse>>> getAllActivities(
            // this page is for pagination //this may be an Integer instead of int
            @RequestParam(name = "page", defaultValue = "0") int page
    ) {
        Page<Activity> activityPage= (Page<Activity>) activityService.getAllActivities(page);
        Page<ActivityResponse> responsePage = activityPage.map(ActivityResponse::new);

        return ResponseEntity.ok().body(new BaseResponse<>
                (HttpStatus.OK.value(), "All Activities", responsePage));
    }



    @PreAuthorize("hasRole('DOCTOR') or hasRole('OWNER')")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BaseResponse<ActivityResponse>> addActivity(
            @Valid @ModelAttribute ActivityRequest activityRequest,
            @RequestHeader HttpHeaders httpHeaders
    ) {
        ActivityResponse response = activityService.createActivity(activityRequest, httpHeaders);
        return ResponseEntity.ok().body(new BaseResponse<>
                (HttpStatus.CREATED.value(), "تم إضافة النشاط بنجاح !", response));
    }


    // Update
    @PreAuthorize("hasRole('DOCTOR') or hasRole('OWNER')")
    @PatchMapping(path = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BaseResponse<ActivityResponse>> updateActivity(
            @PathVariable int id,
            @RequestParam(value = "image", required = false) MultipartFile image,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "title", required = false) String title,
            @RequestHeader HttpHeaders httpHeaders
    ) {
        ActivityResponse response = activityService.updateActivity(id, image, description, title, httpHeaders);
        return ResponseEntity.ok().body(new BaseResponse<>
                (HttpStatus.OK.value(), "تم تعديل النشاط بنجاح !", response));
    }


    // Delete
    @PreAuthorize("hasRole('DOCTOR') or hasRole('OWNER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<ActivityResponse>> deletePost(
            @PathVariable int id) {
        ActivityResponse response = activityService.deleteActivity(id);
        return ResponseEntity.ok().body(new BaseResponse<>
                (HttpStatus.OK.value(), "تم حذف الشاط بنجاح !", response));
    }
}
