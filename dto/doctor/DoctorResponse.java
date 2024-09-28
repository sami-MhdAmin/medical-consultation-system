package com.grad.akemha.dto.doctor;

import com.grad.akemha.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DoctorResponse {
    private List<User> doctors;
    private long totalCount;
}
