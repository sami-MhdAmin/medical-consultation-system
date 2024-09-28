package com.grad.akemha.service;

import com.grad.akemha.dto.notification.NotificationRequestTopic;
import com.grad.akemha.dto.post.DoctorResponse;
import com.grad.akemha.dto.post.PostRequest;
import com.grad.akemha.dto.post.PostResponse;
import com.grad.akemha.entity.Like;
import com.grad.akemha.entity.Post;
import com.grad.akemha.entity.User;
import com.grad.akemha.exception.ForbiddenException;
import com.grad.akemha.exception.NotFoundException;
import com.grad.akemha.repository.LikeRepository;
import com.grad.akemha.repository.PostRepository;
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
import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final JwtService jwtService;
    private final LikeRepository likeRepository;
    private final CloudinaryService cloudinaryService;
    private final FCMService fcmService;

    // Read
    public PostResponse getPostById(int id) {
        Optional<Post> optionalPost = postRepository.findById((long) id);

        if (optionalPost.isPresent()) {
            Post post = optionalPost.get();
            return PostResponse
                    .builder()
                    .id(post.getId())
                    .doctor(new DoctorResponse(post.getUser()))
                    .imageUrl(post.getImageUrl())
                    .description(post.getDescription())
                    .likesCount(post.getLikes().size())
                    .commentsCount(post.getComments().size())
                    .isLiked(false)
                    .build();
        } else {
            throw new NotFoundException("No Post in that Id: " + id + " was found");
        }
    }


    //    public List<Post> getAllPosts(int page) {
//        // this page size indicates of number of data retrieved
//        Pageable pageable = PageRequest.of(page, 10, Sort.by("id").descending());
//        Page<Post> postPage = postRepository.findAll(pageable);
//        return postPage.getContent();
//    }
    public Page<Post> getAllPosts(int page) {
        // this page size indicates of number of data retrieved
        Pageable pageable = PageRequest.of(page, 10, Sort.by("id").descending());
        return postRepository.findAll(pageable);
    }


    // Create
    public PostResponse createPost(@NotNull PostRequest postRequest,
                                   HttpHeaders httpHeaders) throws ExecutionException, InterruptedException {

        if (postRequest.getDescription() == null) {
            throw new ForbiddenException("Description Data is Null");
        }
        if (postRequest.getImage() == null) {
            throw new ForbiddenException("Image Data is Null");
        }
        User user = jwtService.extractUserFromToken(httpHeaders);
        Map<String, String> cloudinaryMap = cloudinaryService.uploadOneFile(postRequest.getImage(), "Posts", user.getId().toString());
        Post post = new Post();
        post.setDescription(postRequest.getDescription());
        post.setImageUrl(cloudinaryMap.get("image_url"));
        post.setImagePublicId(cloudinaryMap.get("public_id"));
        post.setUser(user);
        post.setLikes(new ArrayList<>());
        post.setComments(new ArrayList<>());
        postRepository.save(post);
        // modifying body to be small
        String notificationBody = shortenString(post.getDescription());
        NotificationRequestTopic notificationRequestTopic = new NotificationRequestTopic("منشور جديد", notificationBody, "posts");
        fcmService.sendMessageToTopic(notificationRequestTopic);

        return PostResponse
                .builder()
                .id(post.getId())
                .doctor(new DoctorResponse(post.getUser()))
                .imageUrl(post.getImageUrl())
                .description(post.getDescription())
                .likesCount(post.getLikes().size())
                .commentsCount(post.getComments().size())
                .isLiked(false)
                .build();
    }

    // to cut the description (body of the notification)
    private String shortenString(String input) {
        final int maxLength = 50;
        if (input.length() > maxLength) {
            return input.substring(0, maxLength) + "...";
        } else {
            return input;
        }
    }

    // Update
    public PostResponse updatePost(int id,
                                   MultipartFile image,
                                   String description,
                                   HttpHeaders httpHeaders) {
        if (description == null
                && image == null) {
            throw new NotFoundException("No Data have been entered");
        }

        Optional<Post> optionalPost = postRepository.findById((long) id);
        if (optionalPost.isPresent()) {
            Post post = optionalPost.get();
            String userId = jwtService.extractUserId(httpHeaders);


            if (description != null
                    && !Objects.equals(description, post.getDescription())) {
                post.setDescription(description);
            }


            if (image != null) {
                System.out.println("ENTERED ?HERE");
                // deleting the image from cloudinary
                cloudinaryService.destroyOneFile(post.getImagePublicId());
                // setting the new image file, url and public id
                Map<String, String> cloudinaryMap = cloudinaryService.uploadOneFile(image, "Posts", userId);
                post.setImageUrl(cloudinaryMap.get("image_url"));
                post.setImagePublicId(cloudinaryMap.get("public_id"));
            }

            postRepository.save(post);

            return PostResponse
                    .builder()
                    .id(post.getId())
                    .doctor(new DoctorResponse(post.getUser()))
                    .imageUrl(post.getImageUrl())
                    .description(post.getDescription())
                    .likesCount(post.getLikes().size())
                    .commentsCount(post.getComments().size())
                    .isLiked(false)
                    .build();
        } else {
            throw new NotFoundException("No Post with That id: " + id + " was found");
        }

    }

    // Delete
    public PostResponse deletePost(int id) {
        Optional<Post> optionalPost = postRepository.findById((long) id);

        if (optionalPost.isPresent()) {
            Post post = optionalPost.get();
            cloudinaryService.destroyOneFile(post.getImagePublicId());
            postRepository.deleteById((long) id);
            return PostResponse
                    .builder()
                    .id(post.getId())
                    .doctor(new DoctorResponse(post.getUser()))
                    .imageUrl(post.getImageUrl())
                    .description(post.getDescription())
                    .likesCount(post.getLikes().size())
                    .commentsCount(post.getComments().size())
                    .isLiked(false)
                    .build();
        } else {
            throw new NotFoundException("No Post in that id: " + id);
        }
    }

    // add like
    public PostResponse addLike(int id, HttpHeaders httpHeaders) {
        Optional<Post> optionalPost = postRepository.findById((long) id);

        if (optionalPost.isPresent()) {
            Post post = optionalPost.get();
            User user = jwtService.extractUserFromToken(httpHeaders);
//            likeRepository.existsByUserAndPost(user, post);
            Boolean isLiked = false;

            if (likeRepository.existsByUserAndPost(user, post)) {
                Like like = likeRepository.findByUserAndPost(user, post).orElseThrow();
                likeRepository.delete(like);
            } else {
                Like like = new Like();
                like.setPost(post);
                like.setUser(user);
                likeRepository.save(like);
                isLiked = true;
            }
            return PostResponse
                    .builder()
                    .id(post.getId())
                    .doctor(new DoctorResponse(post.getUser()))
                    .imageUrl(post.getImageUrl())
                    .description(post.getDescription())
                    .likesCount(post.getLikes().size())
                    .commentsCount(post.getComments().size())
                    .isLiked(isLiked)
                    .build();
        } else {
            throw new NotFoundException("No Post in that id: " + id);
        }

    }

    // remove like
//    public PostResponse removeLike(int id, HttpHeaders httpHeaders) {
//        Optional<Post> optionalPost = postRepository.findById((long) id);
//
//        if (optionalPost.isPresent()) {
//            Post post = optionalPost.get();
//            User user = jwtService.extractUserFromToken(httpHeaders);
//
//            Optional<Like> optionalLike = likeRepository.findByUserAndPost(user, post);
//            if (optionalLike.isPresent()) {
//                Like like = optionalLike.get();
//                likeRepository.delete(like);
//            } else {
////                throw new NotFoundException("The Like record in the Like table can' be found");
//                throw new NotFoundException("You can't remove like that doesn't exist");
//            }
//
//            return PostResponse
//                    .builder()
//                    .id(post.getId())
//                    .doctor(new DoctorResponse(post.getUser()))
//                    .imageUrl(post.getImageUrl())
//                    .description(post.getDescription())
//                    .likesCount(post.getLikes().size())
//                    .commentsCount(post.getComments().size())
//                    .build();
//        } else {
//            throw new NotFoundException("No Post in that id: " + id);
//        }
//
//    }

    public List<Post> getAllDoctorsPosts(Long doctorId, int page) {
        // this page size indicates of number of data retrieved
        Pageable pageable = PageRequest.of(page, 10, Sort.by("id").descending());
        return postRepository.findAllByUserId(doctorId, pageable);
    }
}
