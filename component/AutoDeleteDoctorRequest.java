package com.grad.akemha.component;

import com.grad.akemha.entity.DoctorRequest;
import com.grad.akemha.repository.DoctorRequestRepository;
import com.grad.akemha.service.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AutoDeleteDoctorRequest {

    @Autowired
    DoctorRequestRepository doctorRequestRepository;

    private final DoctorService doctorService;;

    public AutoDeleteDoctorRequest(DoctorService doctorService) {
        this.doctorService = doctorService;
    }


    @Scheduled(cron = "0 0 0 * * ?")
    public void deleteOldDoctorRequests() {
        List<DoctorRequest> oldRequests = doctorService.getOldDoctorRequests();
        for (DoctorRequest request : oldRequests) {
            doctorRequestRepository.delete(request);
        }
    }
}
