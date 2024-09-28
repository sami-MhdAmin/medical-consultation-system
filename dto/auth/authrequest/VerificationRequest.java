package com.grad.akemha.dto.auth.authrequest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VerificationRequest {
    private Long userId;
    private String code;
    private String deviceToken;
}
