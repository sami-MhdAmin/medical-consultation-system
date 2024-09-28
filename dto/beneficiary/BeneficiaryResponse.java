package com.grad.akemha.dto.beneficiary;

import com.grad.akemha.dto.post.DoctorResponse;
import com.grad.akemha.entity.Post;
import com.grad.akemha.entity.User;
import com.grad.akemha.entity.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BeneficiaryResponse {
    private Long id;
    private String name;
    private String email;
    private String phoneNumber;
    private String profileImage;
    private LocalDate dob;

    private Gender gender;
    private boolean isActive;

    public BeneficiaryResponse(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.phoneNumber = user.getPhoneNumber();
        this.profileImage = user.getProfileImage();
        this.dob = user.getDob();
        this.gender = user.getGender();
        this.isActive = user.getIsActive();
    }
}
