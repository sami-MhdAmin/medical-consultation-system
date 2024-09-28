package com.grad.akemha.dto.medical_record;

import com.grad.akemha.entity.enums.AdditionalInfoType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdditionalRecordInfoRequest {
    @NotNull
    private String name;
    @NotNull
    private String description;
    @NotNull
    private AdditionalInfoType type;
}
