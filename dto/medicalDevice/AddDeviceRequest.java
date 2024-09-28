package com.grad.akemha.dto.medicalDevice;

import com.grad.akemha.entity.enums.Gender;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Setter
@Getter
public class AddDeviceRequest {
    @NotNull(message = "name can't be Null")
    private String name;
    @NotNull(message = "count can't be Null")
    private Integer count;
    @NotNull(message = "image can't be Null")
    private MultipartFile image;
}
