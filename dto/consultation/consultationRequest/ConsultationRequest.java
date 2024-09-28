package com.grad.akemha.dto.consultation.consultationRequest;

import com.grad.akemha.entity.enums.ConsultationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConsultationRequest {
    private String title;
    private String consultationText;
    private Long specializationId;
    private ConsultationType consultationType;
    private List<MultipartFile> files;
}
