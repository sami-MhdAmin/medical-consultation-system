package com.grad.akemha.service;

import com.grad.akemha.dto.comment.CommentResponse;
import com.grad.akemha.dto.post.DoctorResponse;
import com.grad.akemha.entity.Comment;
import com.grad.akemha.entity.Post;
import com.grad.akemha.entity.User;
import com.grad.akemha.entity.enums.Role;
import com.grad.akemha.exception.ForbiddenException;
import com.grad.akemha.exception.NotFoundException;
import com.grad.akemha.repository.CommentRepository;
import com.grad.akemha.repository.PostRepository;
import com.grad.akemha.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final JwtService jwtService;

    // not necessary
    public CommentResponse getCommentById(int id) {
        Optional<Comment> optionalComment = commentRepository.findById((long) id);

        if (optionalComment.isPresent()) {
            Comment comment = optionalComment.get();
            return CommentResponse
                    .builder()
                    .id(comment.getId())
                    .doctor(new DoctorResponse(comment.getUser()))
                    .description(comment.getDescription())
                    .build();
        } else {
            throw new NotFoundException("No comment in that Id: " + id);
        }
    }

    public List<Comment> getAllCommentsOfSpecificPost(int postId, int page) {
        Optional<Post> optionalPost = postRepository.findById((long) postId);
        if (optionalPost.isPresent()) {
            Pageable pageable = PageRequest.of(page, 10, Sort.by("id").descending());
            Page<Comment> commentPage = commentRepository.findAllByPost(optionalPost.get(), pageable);

            return commentPage.getContent();
        } else {
            throw new NotFoundException("Can't find post with the id of: " + postId);
        }
    }

    // Create
    public CommentResponse createComment(int postId,
                                         String description,
                                         HttpHeaders httpHeaders) {
        Optional<Post> optionalPost = postRepository.findById((long) postId);
        if (optionalPost.isPresent()) {
            User user = jwtService.extractUserFromToken(httpHeaders);
            Comment comment = new Comment();
            comment.setDescription(description);
            comment.setUser(user);
            comment.setPost(optionalPost.get());
            commentRepository.save(comment);
            return CommentResponse
                    .builder()
                    .id(comment.getId())
                    .doctor(new DoctorResponse(comment.getUser()))
                    .description(comment.getDescription())
                    .build();
        } else {
            throw new NotFoundException("Can't find post with the id of: " + postId);
        }
    }

    // Update
    public CommentResponse updateComment(int id, String description, HttpHeaders httpHeaders) {
        if (description == null) {
            throw new NotFoundException("No Data have been entered");
        }
        Optional<Comment> optionalComment = commentRepository.findById((long) id);
        if (optionalComment.isPresent()) {
            Comment comment = optionalComment.get();

            // checking if the comment belongs to the same user that added the comment
            // note: doctors can update any comment
            User user = jwtService.extractUserFromToken(httpHeaders);
            if (comment.getUser() != user && user.getRole() == Role.USER) {
                throw new ForbiddenException("You can't Update Comment that is not yours");
            }
            if (!Objects.equals(comment.getDescription(), description)) {
                comment.setDescription(description);
            }
            commentRepository.save(comment);
            return CommentResponse
                    .builder()
                    .id(comment.getId())
                    .doctor(new DoctorResponse(comment.getUser()))
                    .description(comment.getDescription())
                    .build();
        } else {
            throw new NotFoundException("No Comment with That id: " + id);
        }
    }

    // Delete
    public CommentResponse deleteComment(int id, HttpHeaders httpHeaders) {
        Optional<Comment> optionalComment = commentRepository.findById((long) id);

        if (optionalComment.isPresent()) {
            Comment comment = optionalComment.get();
            // checking if the comment belongs to the same user that added the comment
            // note: doctors can update any comment
            User user = jwtService.extractUserFromToken(httpHeaders);
            if (comment.getUser() != user && user.getRole() == Role.USER) {
                throw new ForbiddenException("You can't Delete Comment that is not yours");
            }
            commentRepository.deleteById((long) id);
            return CommentResponse
                    .builder()
                    .id(comment.getId())
                    .doctor(new DoctorResponse(comment.getUser()))
                    .description(comment.getDescription())
                    .build();
        } else {
            throw new NotFoundException("No Comment in that id: " + id);
        }
    }
}
