package com.grad.akemha.service;

import com.grad.akemha.dto.specializationDTO.SpecializationRequest;
import com.grad.akemha.dto.statistic.SpecializationUserCountResponse;
import com.grad.akemha.entity.Specialization;
import com.grad.akemha.exception.CloudinaryException;
import com.grad.akemha.exception.ForbiddenException;
import com.grad.akemha.exception.authExceptions.UserNotFoundException;
import com.grad.akemha.service.cloudinary.CloudinaryService;
import com.grad.akemha.repository.SpecializationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Sort;
import java.util.List;
import java.util.Optional;

@Service
public class SpecializationService {
    @Autowired
    SpecializationRepository specializationRepository;
    @Autowired
    private CloudinaryService cloudinaryService;

    public List<Specialization> getSpecializations() {
        return specializationRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
    }
    public List<Specialization> getSpecializationsWithConsultation() {
        return specializationRepository.findByConsultationsIsNotEmpty();
    }
    public List<Specialization> getSpecializationsWithDoctor() {
        return specializationRepository.findByUsersIsNotEmpty();
    }

    public Specialization deleteSpecializationById(Long specializationId) {
        Optional<Specialization> optionalSpecialization = specializationRepository.findById(specializationId);
        if (optionalSpecialization.isPresent()) {
            Specialization specialization = optionalSpecialization.get();
            specializationRepository.deleteById(specializationId);
            return specialization;
        } else {
            throw new UserNotFoundException("specialization id " + specializationId + " is not found");
        }
    }

    public Specialization addSpecialization(SpecializationRequest request) {
        if (request.specializationType() == null) {
            throw new ForbiddenException("specializationType Data is Null");
        }
        if (request.isPublic() == null) {
            throw new ForbiddenException("specialization isPublic Data is Null");
        }

        String specializationImage = cloudinaryService.uploadFile(request.image(), "Specialization", "admin");
        if (specializationImage == null) {
            throw new CloudinaryException("Image upload failed");
        }

        Specialization specialization = new Specialization();
        specialization.setIsPublic(request.isPublic());
        specialization.setSpecializationType(request.specializationType());
        specialization.setImageUrl(specializationImage);
        return specializationRepository.save(specialization);
    }

    public List<Specialization> getSpecializationsNotPublic() {
        return specializationRepository.findByIsPublicFalse();
    }


    public List<SpecializationUserCountResponse> countUsersBySpecialization() {
        return specializationRepository.countUsersBySpecialization();
    }
}
