package com.grad.akemha.service;

import com.grad.akemha.dto.medicalDevice.AddDeviceRequest;
import com.grad.akemha.dto.medicalDevice.ReserveDeviceRequest;
import com.grad.akemha.dto.statistic.DeviceReservationCountResponse;
import com.grad.akemha.entity.DeviceReservation;
import com.grad.akemha.entity.MedicalDevice;
import com.grad.akemha.entity.User;
import com.grad.akemha.entity.enums.DeviceReservationStatus;
import com.grad.akemha.exception.*;
import com.grad.akemha.repository.DeviceReservationRepository;
import com.grad.akemha.repository.MedicalDeviceRepository;
import com.grad.akemha.security.JwtService;
import com.grad.akemha.service.cloudinary.CloudinaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class MedicalDeviceService {
    @Autowired
    private MedicalDeviceRepository medicalDeviceRepository;

    @Autowired
    private DeviceReservationRepository deviceReservationRepository;
    @Autowired
    private CloudinaryService cloudinaryService;

    @Autowired
    private JwtService jwtService;

    public List<MedicalDevice> getDevices(Integer page) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by("id").descending());
        Page<MedicalDevice> devicePage = medicalDeviceRepository.findAll(pageable);
        return devicePage.getContent();
    }

    public Page<MedicalDevice> getDevicesAdmin(Integer page) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by("id").descending());
        return medicalDeviceRepository.findAll(pageable);
    }

    public List<DeviceReservation> getReservations(Long medicalDeviceId) {
        MedicalDevice medicalDevice = medicalDeviceRepository.findById(medicalDeviceId).orElseThrow(() -> new DeviceNotFoundException("Device not found"));
        List<DeviceReservation> reservations = medicalDevice.getDeviceReservations();
        Collections.sort(reservations, Comparator.comparing(DeviceReservation::getId).reversed());
        return reservations;
    }

    public List<DeviceReservation> getUserReservations(HttpHeaders httpHeaders) {
        User user = jwtService.extractUserFromToken(httpHeaders);
        return deviceReservationRepository.findByUserAndStatusIn(user, Arrays.asList(null, DeviceReservationStatus.TAKEN));
    }


    public void addDevice(AddDeviceRequest request, HttpHeaders httpHeaders) {
        if (deviceAlreadyExists(request.getName())) {
            throw new DeviceAlreadyExistsException("Device already exists");
        }
        User user = jwtService.extractUserFromToken(httpHeaders);
        MedicalDevice medicalDevice = new MedicalDevice();
        medicalDevice.setName(request.getName());
        medicalDevice.setCount(request.getCount());
        medicalDevice.setReservedCount(0);
        if (request.getImage() != null) {
            Map<String, String> cloudinaryMap = cloudinaryService.uploadOneFile(request.getImage(), "Devices", user.getId().toString());
            medicalDevice.setImageUrl(cloudinaryMap.get("image_url"));
            medicalDevice.setImagePublicId(cloudinaryMap.get("public_id"));
        }

        medicalDeviceRepository.save(medicalDevice);
    }

    public boolean deviceAlreadyExists(String name) {
        return medicalDeviceRepository.existsByName(name);
    }

    public void reserveDevice(ReserveDeviceRequest request, HttpHeaders httpHeaders) {
        //TODO remove the reservation automatically
        User user = jwtService.extractUserFromToken(httpHeaders);
        boolean hasMoreThanTwoReservedRecords = deviceReservationRepository.countByUserAndStatus(user, null) >= 2;
        if (hasMoreThanTwoReservedRecords) {
            throw new DeviceReservationQuantityException("You cannot reserve more than two device");
        }
        MedicalDevice medicalDevice = medicalDeviceRepository.findById(request.getMedicalDeviceId()).orElseThrow(() -> new DeviceNotFoundException("Device not found"));
        if (request.getQuantity() > 2)
            throw new DeviceReservationQuantityException("You cannot reserve more than two of the same device");
        int availableQuantity = medicalDevice.getCount() - medicalDevice.getReservedCount();
        if (request.getQuantity() <= availableQuantity) {
            int newReservedCount = medicalDevice.getReservedCount() + request.getQuantity();
            medicalDevice.setReservedCount(newReservedCount);
            DeviceReservation reservation = DeviceReservation.builder()
                    .user(user)
                    .medicalDevice(medicalDevice)
                    .build();
            if (request.getQuantity() == 2) {
                DeviceReservation reservation2 = DeviceReservation.builder()
                        .user(user)
                        .medicalDevice(medicalDevice)
                        .build();
                deviceReservationRepository.save(reservation2);
            }
            deviceReservationRepository.save(reservation);
            medicalDeviceRepository.save(medicalDevice);
        } else {
            throw new DeviceReservationNoQuantityException("No enough devices available for reservation");
        }
    }

    public void deleteDevice(Long medicalDeviceId) {
        MedicalDevice medicalDevice = medicalDeviceRepository.findById(medicalDeviceId).orElseThrow(() -> new DeviceNotFoundException("Device not found"));
        cloudinaryService.destroyOneFile(medicalDevice.getImagePublicId());
        medicalDeviceRepository.delete(medicalDevice);
    }

    public void deleteDeviceReservation(Long deviceReservationId, HttpHeaders httpHeaders) {
        DeviceReservation deviceReservation = deviceReservationRepository.findById(deviceReservationId).orElseThrow(() -> new DeviceNotFoundException("Device reservation not found"));
        User user = jwtService.extractUserFromToken(httpHeaders);
        if (!deviceReservation.getUser().equals(user)) {
            throw new ReservationUnauthorizedException("You are not authorized to delete this reservation");
        }
        if (deviceReservation.getStatus() != null) {
            throw new ReservationDeleteException("Can't delete ended reservation");
        }
        deviceReservation.getMedicalDevice().setReservedCount(deviceReservation.getMedicalDevice().getReservedCount() - 1);
        deviceReservationRepository.delete(deviceReservation);
    }

    public void deviceDelivery(Long deviceReservationId) {
        DeviceReservation deviceReservation = deviceReservationRepository.findById(deviceReservationId).orElseThrow(() -> new DeviceNotFoundException("Device reservation not found"));
        if (deviceReservation.getStatus() != null) {
            throw new ReservationDeleteException("Can't delivery device for ended or pending or taken reservation");
        }
        deviceReservation.setStatus(DeviceReservationStatus.TAKEN);
        deviceReservation.setTakeTime(LocalDateTime.now());
        deviceReservationRepository.save(deviceReservation);
    }

    public void deviceRewind(Long deviceReservationId) {
        DeviceReservation deviceReservation = deviceReservationRepository.findById(deviceReservationId).orElseThrow(() -> new DeviceNotFoundException("Device reservation not found"));
        if (deviceReservation.getStatus() != DeviceReservationStatus.TAKEN) {
            throw new ReservationDeleteException("Can't rewind device for ended or pending reservation");
        }
        deviceReservation.setStatus(DeviceReservationStatus.END);
        deviceReservation.setRewindTime(LocalDateTime.now());
        deviceReservation.getMedicalDevice().setReservedCount(deviceReservation.getMedicalDevice().getReservedCount() - 1);
        deviceReservationRepository.save(deviceReservation);
    }

    public void changQuantity(Long medicalDeviceId, int quantity) {
        MedicalDevice medicalDevice = medicalDeviceRepository.findById(medicalDeviceId).orElseThrow(() -> new DeviceNotFoundException("Device not found"));
        medicalDevice.setCount(quantity);
        medicalDeviceRepository.save(medicalDevice);
    }


    public Map<Integer, List<Map<String, Object>>> countReservationsByMonth() {
        List<DeviceReservationStatus> statuses = Arrays.asList(DeviceReservationStatus.TAKEN, DeviceReservationStatus.END);
        List<DeviceReservationCountResponse> responses = deviceReservationRepository.countReservationsByMonth(statuses);

        Map<Integer, List<Map<String, Object>>> result = new HashMap<>();

        for (DeviceReservationCountResponse response : responses) {
            int year = response.getYear();
            result.putIfAbsent(year, new ArrayList<>());
            Map<String, Object> monthData = new HashMap<>();
            monthData.put("deviceName", response.getDeviceName());
            monthData.put("month", response.getMonth());
            monthData.put("count", response.getCount());
            result.get(year).add(monthData);
        }

        return result;
    }
}
