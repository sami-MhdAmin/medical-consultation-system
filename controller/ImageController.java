package com.grad.akemha.controller;

import com.grad.akemha.dto.ImageModel;
import com.grad.akemha.repository.ImageRepository;
import com.grad.akemha.service.cloudinary.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

    @RestController
    public class ImageController {

        @Autowired
        private ImageRepository imageRepository;

        @Autowired
        private ImageService imageService;

        @PostMapping(path = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
        public ResponseEntity<Map> upload(@ModelAttribute ImageModel imageModel) {
            try {
                return imageService.uploadImage(imageModel);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @PostMapping()
        public ResponseEntity<Map> destroy() {
            try {
                return imageService.destroyImage("mi");
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }
