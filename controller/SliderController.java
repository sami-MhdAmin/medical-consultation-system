package com.grad.akemha.controller;

import com.grad.akemha.dto.BaseResponse;
import com.grad.akemha.dto.slider.request.SliderRequest;
import com.grad.akemha.entity.Slider;
import com.grad.akemha.service.SliderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/slider")
public class SliderController {

    @Autowired
    private SliderService sliderService;

    @PreAuthorize("hasRole('OWNER')")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BaseResponse<String>> addSliderPage(
            @ModelAttribute SliderRequest sliderRequest
    ) {
        sliderService.addSliderPage(sliderRequest);
        return ResponseEntity.ok().body(new BaseResponse<>
                (HttpStatus.CREATED.value(), "Slider page is created successfully", "new page is added"));
    }

    @PreAuthorize("hasRole('USER') or hasRole('DOCTOR') or hasRole('OWNER')")
    @GetMapping()
    public ResponseEntity<BaseResponse<List<Slider>>> getSlider() {
        List<Slider> response = sliderService.getSliders();

        return ResponseEntity.ok().body(new BaseResponse<>
                (HttpStatus.OK.value(), "Slider Found successfully", response));
    }

    @PreAuthorize("hasRole('OWNER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<String>> deleteSliderPage(
            @PathVariable Long id) {
        sliderService.deleteSliderPage(id);
        return ResponseEntity.ok().body(new BaseResponse<>
                (HttpStatus.OK.value(), "Slide deleted successfully", "the slide is deleted"));
    }
}
