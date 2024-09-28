package com.grad.akemha.repository;

import com.grad.akemha.entity.MedicalDevice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MedicalDeviceRepository extends JpaRepository<MedicalDevice,Long> {
    boolean existsByName(String name);
}
