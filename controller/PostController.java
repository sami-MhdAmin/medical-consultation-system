package com.grad.akemha.controller;

import com.grad.akemha.dto.BaseResponse;
import com.grad.akemha.dto.post.PostRequest;
import com.grad.akemha.dto.post.PostResponse;
import com.grad.akemha.entity.Post;
import com.grad.akemha.entity.User;
import com.grad.akemha.repository.LikeRepository;
import com.grad.akemha.security.JwtService;
import com.grad.akemha.service.PostService;
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

import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/post")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    private final JwtService jwtService;
    private final LikeRepository likeRepository;

    // Read
    @PreAuthorize("hasRole('USER') or hasRole('DOCTOR') or hasRole('OWNER')")
    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<PostResponse>> getPostById(
            @PathVariable int id) {
        PostResponse response = postService.getPostById(id);

        return ResponseEntity.ok().body(new BaseResponse<>
                (HttpStatus.OK.value(), "Post Found successfully", response));

    }


    @PreAuthorize("hasRole('USER') or hasRole('DOCTOR') or hasRole('OWNER')")
    @GetMapping()
    public ResponseEntity<BaseResponse<Page<PostResponse>>> getAllPosts(
            // this page is for pagination //this may be an Integer instead of int
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestHeader HttpHeaders httpHeaders
    ) {

        Page<Post> postPage = (Page<Post>) postService.getAllPosts(page);
//        Page<PostResponse> responsePage = postPage.map(PostResponse::new);

        Page<PostResponse> responsePage = postPage.map(post -> {
            User user = jwtService.extractUserFromToken(httpHeaders);
            Boolean isLiked =  likeRepository.existsByUserAndPost( user, post);
            return new PostResponse(post, isLiked);
        });

        return ResponseEntity.ok().body(new BaseResponse<>
                (HttpStatus.OK.value(), "All Posts", responsePage));

    }


    @PreAuthorize("hasRole('DOCTOR') or hasRole('OWNER')")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BaseResponse<PostResponse>> addPost(
            @Valid @ModelAttribute PostRequest postRequest,
            @RequestHeader HttpHeaders httpHeaders
    ) throws ExecutionException, InterruptedException {
        PostResponse response = postService.createPost(postRequest, httpHeaders);
        return ResponseEntity.ok().body(new BaseResponse<>
                (HttpStatus.CREATED.value(), "تم إضافة المنشور بنجاح !", response));
    }

    // Update
    @PreAuthorize("hasRole('DOCTOR') or hasRole('OWNER')")
    @PatchMapping(path = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BaseResponse<PostResponse>> updatePost(
            @PathVariable int id,
            @RequestParam(value = "image", required = false) MultipartFile image,
            @RequestParam(value = "description", required = false) String description,
            @RequestHeader HttpHeaders httpHeaders
    ) {
        PostResponse response = postService.updatePost(id, image, description, httpHeaders);
        return ResponseEntity.ok().body(new BaseResponse<>
                (HttpStatus.OK.value(), "تم تعديل المنشور بنجاح !", response));

    }

    // Delete
    @PreAuthorize("hasRole('DOCTOR') or hasRole('OWNER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<PostResponse>> deletePost(
            @PathVariable int id) {
        PostResponse response = postService.deletePost(id);
        return ResponseEntity.ok().body(new BaseResponse<>
                (HttpStatus.OK.value(), "تم حذف المنشور بنجاح !", response));
    }

    @PreAuthorize("hasRole('USER') or hasRole('DOCTOR')")
    @PostMapping("/add_like/{postId}")
    public ResponseEntity<BaseResponse<PostResponse>> addLikeToPost(
            @PathVariable int postId,
            @RequestHeader HttpHeaders httpHeaders) {
        PostResponse response = postService.addLike(postId, httpHeaders);
        return ResponseEntity.ok().body(new BaseResponse<>
                (HttpStatus.OK.value(), "Added like to the Post successfully", response));
    }

    @PreAuthorize("hasRole('DOCTOR') or hasRole('OWNER')")
    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<BaseResponse<List<PostResponse>>> getAllDoctorsPosts(
            @PathVariable Long doctorId,
            @RequestParam(name = "page", defaultValue = "0") int page
    ) {
        List<Post> posts = postService.getAllDoctorsPosts(doctorId, page);
        List<PostResponse> response = posts.stream().map(PostResponse::new).toList();

        return ResponseEntity.ok().body(new BaseResponse<>
                (HttpStatus.OK.value(), "All Posts", response));
    }
}