package com.grad.akemha.dto.doctor;

import com.grad.akemha.entity.Specialization;
import com.grad.akemha.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DoctorResponseMobile {
    private Long id;
    private String name;
    private String email;
    private String phoneNumber;
    private String profileImage;
    private String description;
    private String location;
    private String openingTimes;
    private Specialization specialization;
    private long answeredConsultation;

    public DoctorResponseMobile(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.phoneNumber = user.getPhoneNumber();
        this.profileImage = user.getProfileImage();
        this.description = user.getDescription();
        this.location = user.getLocation();
        this.openingTimes = user.getOpeningTimes();
        this.specialization = user.getSpecialization();
        this.answeredConsultation = 0;
    }
}
