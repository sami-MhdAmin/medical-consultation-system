package com.grad.akemha.controller;

import com.grad.akemha.dto.BaseResponse;
import com.grad.akemha.dto.auth.authrequest.LoginRequest;
import com.grad.akemha.dto.auth.authrequest.RegisterRequest;
import com.grad.akemha.dto.auth.authresponse.AuthResponse;
import com.grad.akemha.dto.auth.authrequest.VerificationRequest;
import com.grad.akemha.service.AuthenticationService;
import com.grad.akemha.service.LogoutService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@CrossOrigin(origins = "http://localhost:3000")

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Validated
public class AuthController {
    //   s
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthController.class);

    private final AuthenticationService authService;
    private final LogoutService logoutService;

    @PostMapping("/register")
    public ResponseEntity<BaseResponse<String>> register(
            @Valid @RequestBody RegisterRequest request
    ) throws IOException {
        try {
            String response = authService.register(request);
            return ResponseEntity.ok()
                    .body(new BaseResponse<>(HttpStatus.OK.value(), "User registered successfully", response));
        } catch (IOException e) {
            System.out.println("============================ in catch");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), null));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<BaseResponse<AuthResponse>> login(
            @RequestBody LoginRequest request
    ) {
//        try {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok()
                .body(new BaseResponse<>(HttpStatus.OK.value(), "User logged successfully", response));
//        } catch (BadCredentialsException e) {
//            // Handle user not found exception
//            //another way
//           /* Map<String, String> errorResponse = new HashMap<>();
//            errorResponse.put("msg", "User not found");   then put errorResponse in .body(errorResponse) */
//            return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                    .body(new BaseResponse<>(HttpStatus.NOT_FOUND.value(), "user not found",null));
//        }
//    }
    }
    @PostMapping("/login/admin")
    public ResponseEntity<BaseResponse<AuthResponse>> loginAdmin(
            @RequestBody LoginRequest request
    ) {
        AuthResponse response = authService.loginAdmin(request);
        return ResponseEntity.ok()
                .body(new BaseResponse<>(HttpStatus.OK.value(), "User logged successfully", response));
    }

    @PostMapping("/verify_account")
    public ResponseEntity<BaseResponse<AuthResponse>> verifyAccount(
            @RequestBody VerificationRequest verificationRequest
    ) throws Exception {
        AuthResponse response = authService.verifyAccount(verificationRequest);
        return ResponseEntity.ok()
                .body(new BaseResponse<>(HttpStatus.OK.value(), "User Verified successfully", response));
    }

    @DeleteMapping("/logout")
    public ResponseEntity<BaseResponse<String>> logout(
            @RequestHeader HttpHeaders httpHeaders
    ) throws Exception {
//        AuthResponse response = logoutService.logout();
        return ResponseEntity.ok()
                .body(new BaseResponse<>(HttpStatus.OK.value(), "User logged out successfully",
                        "response"));
    }
}
