package com.grad.akemha.service;

import com.grad.akemha.dto.activity.ActivityRequest;
import com.grad.akemha.dto.activity.ActivityResponse;
import com.grad.akemha.entity.Activity;
import com.grad.akemha.entity.User;
import com.grad.akemha.exception.ForbiddenException;
import com.grad.akemha.exception.NotFoundException;
import com.grad.akemha.repository.ActivityRepository;
import com.grad.akemha.security.JwtService;
import com.grad.akemha.service.cloudinary.CloudinaryService;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ActivityService {
    private final ActivityRepository activityRepository;
    private final JwtService jwtService;
    private final CloudinaryService cloudinaryService;


    // Read
    public ActivityResponse getActivityById(int id) {
        Optional<Activity> optionalActivity = activityRepository.findById((long) id);

        if (optionalActivity.isPresent()) {
            Activity activity = optionalActivity.get();
            return ActivityResponse
                    .builder()
                    .id(activity.getId())
                    .title(activity.getTitle())
                    .description(activity.getDescription())
                    .imageUrl(activity.getImageUrl())
                    .build();
        } else {
            throw new NotFoundException("No Activity in that Id: " + id + " was found");
        }
    }


//    public List<Activity> getAllActivities(int page) {
//        // this page size indicates of number of data retrieved, // the descending to fetch the latest posts to older posts
//        Pageable pageable = PageRequest.of(page, 10, Sort.by("id").descending());
//        Page<Activity> activityPage = activityRepository.findAll(pageable);
//        return activityPage.getContent();
//    }

    public Page<Activity> getAllActivities(int page) {
        // this page size indicates of number of data retrieved, // the descending to fetch the latest posts to older posts
        Pageable pageable = PageRequest.of(page, 10, Sort.by("id").descending());
        return activityRepository.findAll(pageable);
    }

    // Create
    public ActivityResponse createActivity(@NotNull ActivityRequest activityRequest,
                                           HttpHeaders httpHeaders) {

        if (activityRequest.getDescription() == null) {
            throw new ForbiddenException("Description Data is Null");
        }
        if (activityRequest.getImage() == null) {
            throw new ForbiddenException("Image Data is Null");
        }
        User user = jwtService.extractUserFromToken(httpHeaders);
        Map<String, String> cloudinaryMap = cloudinaryService.uploadOneFile(activityRequest.getImage(), "Activity", user.getId().toString());
        Activity activity = new Activity();
        activity.setTitle(activityRequest.getTitle());
        activity.setDescription(activityRequest.getDescription());
        activity.setImageUrl(cloudinaryMap.get("image_url"));
        activity.setImagePublicId(cloudinaryMap.get("public_id"));
        activityRepository.save(activity);
        return ActivityResponse
                .builder()
                .id(activity.getId())
                .title(activity.getTitle())
                .description(activity.getDescription())
                .imageUrl(activity.getImageUrl())
                .build();
    }

    // Update
    public ActivityResponse updateActivity(int id,
                                           MultipartFile image,
                                           String description,
                                           String title,
                                           HttpHeaders httpHeaders) {
        if (description == null
                && image == null
                && title == null) {
            throw new NotFoundException("No Data have been entered");
        }

        Optional<Activity> optionalActivity = activityRepository.findById((long) id);
        if (optionalActivity.isPresent()) {
            Activity activity = optionalActivity.get();
            String userId = jwtService.extractUserId(httpHeaders);

            if (description != null
                    && !Objects.equals(description, activity.getDescription())) {
                activity.setDescription(description);
            }

            if (title != null
                    && !Objects.equals(title, activity.getTitle())) {
                activity.setTitle(title);
            }

            if (image != null) {
                // deleting the image from cloudinary
                cloudinaryService.destroyOneFile(activity.getImagePublicId());
                // setting the new image file, url and public id
                Map<String, String> cloudinaryMap = cloudinaryService.uploadOneFile(image, "Activity", userId);
                activity.setImageUrl(cloudinaryMap.get("image_url"));
                activity.setImagePublicId(cloudinaryMap.get("public_id"));
            }

            activityRepository.save(activity);

            return ActivityResponse
                    .builder()
                    .id(activity.getId())
                    .title(activity.getTitle())
                    .description(activity.getDescription())
                    .imageUrl(activity.getImageUrl())
                    .build();
        } else {
            throw new NotFoundException("No Activity with That id: " + id + " was found");
        }

    }

    // Delete
    public ActivityResponse deleteActivity(int id) {
        Optional<Activity> optionalActivity = activityRepository.findById((long) id);

        if (optionalActivity.isPresent()) {
            Activity activity = optionalActivity.get();
            cloudinaryService.destroyOneFile(activity.getImagePublicId());
            activityRepository.deleteById((long) id);
            return ActivityResponse
                    .builder()
                    .id(activity.getId())
                    .title(activity.getTitle())
                    .description(activity.getDescription())
                    .imageUrl(activity.getImageUrl())
                    .build();
        } else {
            throw new NotFoundException("No Activity in that id: " + id + " was found");
        }
    }
}
