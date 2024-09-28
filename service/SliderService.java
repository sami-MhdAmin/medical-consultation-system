package com.grad.akemha.service;

import com.grad.akemha.dto.slider.request.SliderRequest;
import com.grad.akemha.entity.Slider;
import com.grad.akemha.exception.CloudinaryException;
import com.grad.akemha.exception.ForbiddenException;
import com.grad.akemha.exception.NotFoundException;
import com.grad.akemha.service.cloudinary.CloudinaryService;
import com.grad.akemha.repository.SliderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SliderService {
    @Autowired
    private SliderRepository sliderRepository;

    @Autowired
    private CloudinaryService cloudinaryService;

    public void addSliderPage(SliderRequest sliderRequest) {
        if (sliderRequest.pageLink() == null) {
            throw new ForbiddenException("page Link Data is Null");
        }
        if (sliderRequest.image() == null) {
            throw new ForbiddenException("Image Data is Null");
        }

        String sliderImage = cloudinaryService.uploadFile(sliderRequest.image(), "Slider", "admin");
        if (sliderImage == null) {
            throw new CloudinaryException("Image upload failed");
        }

        Slider slider = new Slider();
        slider.setPageLink(sliderRequest.pageLink());
        slider.setImageUrl(sliderImage);
        sliderRepository.save(slider);
    }

    public List<Slider> getSliders() {
        return sliderRepository.findAll();
    }

    public void deleteSliderPage(Long id) {
        Slider slider = sliderRepository.findById(id).orElseThrow(() -> new NotFoundException("there is no slide with id: " + id));
        sliderRepository.delete(slider);
    }
}
