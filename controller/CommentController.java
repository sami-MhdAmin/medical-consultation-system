package com.grad.akemha.controller;

import com.grad.akemha.dto.BaseResponse;
import com.grad.akemha.dto.comment.CommentResponse;
import com.grad.akemha.entity.Comment;
import com.grad.akemha.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/post/comment")
@CrossOrigin(origins = "http://localhost:3000")

@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;


    // getting all comments for specific post
    @PreAuthorize("hasRole('USER') or hasRole('DOCTOR') or hasRole('OWNER')")
    @GetMapping("/{postId}")
    public ResponseEntity<BaseResponse<List<CommentResponse>>> getAllCommentsForSpecificPost(@PathVariable int postId,
                                                                                             @RequestParam(name = "page", defaultValue = "0") int page
    ) {
        List<Comment> comments = commentService.getAllCommentsOfSpecificPost(postId, page);
        List<CommentResponse> response = comments.stream().map(CommentResponse::new).toList();

        return ResponseEntity.ok().body(new BaseResponse<>
                (HttpStatus.CREATED.value(), "Comments found successfully", response));
    }


    @PreAuthorize("hasRole('USER') or hasRole('DOCTOR')")
    @PostMapping("/{postId}")
    public ResponseEntity<BaseResponse<CommentResponse>> addComment(@PathVariable int postId,
                                                                    @RequestParam String description,
                                                                    @RequestHeader HttpHeaders httpHeaders
    ) {
        CommentResponse response = commentService.createComment(postId, description, httpHeaders);
        return ResponseEntity.ok().body(new BaseResponse<>
                (HttpStatus.CREATED.value(), "Comment created successfully", response));
    }

    // Update
    @PreAuthorize("hasRole('USER') or hasRole('DOCTOR')")
    @PatchMapping("/{id}")
    public ResponseEntity<BaseResponse<CommentResponse>> updateComment(
            @PathVariable int id,
            @RequestParam String description,
            @RequestHeader HttpHeaders httpHeaders) {
        CommentResponse response = commentService.updateComment(id, description, httpHeaders);
        return ResponseEntity.ok().body(new BaseResponse<>
                (HttpStatus.CREATED.value(), "Comment updated successfully", response));
    }


    // Delete
    @PreAuthorize("hasRole('USER') or hasRole('DOCTOR')")
    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<CommentResponse>> deleteComment(
            @PathVariable int id,
            @RequestHeader HttpHeaders httpHeaders) {
        CommentResponse response = commentService.deleteComment(id, httpHeaders);
        return ResponseEntity.ok().body(new BaseResponse<>
                (HttpStatus.OK.value(), "Comment deleted successfully", response));
    }

}
