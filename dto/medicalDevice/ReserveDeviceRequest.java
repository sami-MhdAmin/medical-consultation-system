package com.grad.akemha.dto.medicalDevice;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.Max;

@Setter
@Getter
public class ReserveDeviceRequest {
    @NotNull(message = "quantity can't be Null")
    @Max(value = 2, message = "You can add a maximum of 2 medicines")
    private Integer quantity;
    @NotNull(message = "medicalDeviceId can't be Null")
    private Long medicalDeviceId;
}
