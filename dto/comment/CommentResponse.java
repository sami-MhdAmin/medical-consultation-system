package com.grad.akemha.dto.comment;

import com.grad.akemha.dto.post.DoctorResponse;
import com.grad.akemha.entity.Comment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponse {
    private Long id;
    private DoctorResponse doctor;
    private String description;

    public CommentResponse(Comment comment) {
        this.id = comment.getId();
        this.doctor = new DoctorResponse(comment.getUser());
        this.description = comment.getDescription();
    }
}
