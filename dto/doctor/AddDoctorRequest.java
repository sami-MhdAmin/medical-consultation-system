package com.grad.akemha.dto.doctor;

import com.grad.akemha.entity.enums.Gender;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AddDoctorRequest {
    @NotNull(message = "name can't be Null")
    private String name;

    @NotNull(message = "email can't be Null")
    private String email;

    @NotNull(message = "password can't be Null")
    private String password;

    @NotNull(message = "gender can't be Null")
    private Gender gender;

    @NotNull(message = "specialization can't be Null")
    private String specialization;

}
