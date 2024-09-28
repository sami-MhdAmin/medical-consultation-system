package com.grad.akemha.dto.beneficiary;

import com.grad.akemha.entity.enums.Gender;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AddBeneficiaryRequest {
    @NotNull(message = "name can't be empty")
    private String name;
    @NotNull(message = "email can't be empty")
    private String email;
    @NotNull(message = "password can't be empty")
    private String password;
    @NotNull(message = "gender can't be empty")
    private Gender gender;
}
