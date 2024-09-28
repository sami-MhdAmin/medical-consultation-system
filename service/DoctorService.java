package com.grad.akemha.service;

import com.grad.akemha.dto.notification.NotificationRequestToken;
import com.grad.akemha.dto.statistic.StatisticCountResponse;
import com.grad.akemha.dto.doctor.AddDoctorRequest;
import com.grad.akemha.entity.DoctorRequest;
import com.grad.akemha.entity.Specialization;
import com.grad.akemha.entity.User;
import com.grad.akemha.entity.enums.DoctorRequestStatus;
import com.grad.akemha.entity.enums.Gender;
import com.grad.akemha.entity.enums.Role;
import com.grad.akemha.exception.CloudinaryException;
import com.grad.akemha.exception.ForbiddenException;
import com.grad.akemha.exception.NotFoundException;
import com.grad.akemha.exception.authExceptions.EmailAlreadyExistsException;
import com.grad.akemha.exception.authExceptions.UserNotFoundException;
import com.grad.akemha.service.cloudinary.CloudinaryService;
import com.grad.akemha.repository.DoctorRequestRepository;
import com.grad.akemha.repository.SpecializationRepository;
import com.grad.akemha.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
public class DoctorService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    SpecializationRepository specializationRepository;
    @Autowired
    DoctorRequestRepository doctorRequestRepository;

    private final PasswordEncoder passwordEncoder;
    private final CloudinaryService cloudinaryService;

    private final FCMService fcmService;
    private final EmailService emailService;


    public Page<User> getDoctors(Integer page) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by("id").descending());
        return userRepository.findByRole(Role.DOCTOR, pageable);
    }

    public Page<User> getDoctorsBySpecialization(Long specializationId, Integer page) {
        if (specializationRepository.findById(specializationId).isPresent()) {
            Pageable pageable = PageRequest.of(page, 10, Sort.by("id").descending());
            Page<User> userPage = userRepository.findBySpecializationIdAndRole(specializationId, pageable, Role.DOCTOR);
            return userPage;
        } else {
            throw new NotFoundException("SpecializationId " + specializationId + " is not found");
        }
    }
//    public DoctorResponse getDoctors(Integer page) {
//        Pageable pageable = PageRequest.of(page, 10, Sort.by("id").descending());
//        Page<User> doctorPage = userRepository.findByRole(Role.DOCTOR, pageable);
//        return new DoctorResponse(doctorPage.getContent(), doctorPage.getTotalElements());
//    }

    public void addDoctor(AddDoctorRequest request) {
        if (userAlreadyExists(request.getEmail())) {
            throw new EmailAlreadyExistsException("User already exists");
        }
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setRole(Role.DOCTOR);
        user.setIsVerified(true);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setGender(request.getGender());
        user.setCreationDate(LocalDateTime.now());
        Specialization specialization = specializationRepository.findBySpecializationType(request.getSpecialization());
        user.setSpecialization(specialization);
        userRepository.save(user);
    }


    public DoctorRequest addDoctorRequest(String name, String email, String aboutMe, Long specializationId, MultipartFile cv, Gender gender, String deviceToken) throws IOException {
        if (name == null) {
            throw new ForbiddenException("name can't be Null");
        }
        if (deviceToken == null) {
            throw new ForbiddenException("deviceToken can't be Null");
        }
        if (email == null) {
            throw new ForbiddenException("email can't be Null");
        }
        if (aboutMe == null) {
            throw new ForbiddenException("aboutMe can't be Null");
        }
        if (gender == null) {
            throw new ForbiddenException("gender can't be Null");
        }
        if (specializationId == null) {
            throw new ForbiddenException("specialization can't be Null");
        }
        if (cv == null) {
            throw new CloudinaryException("cv upload failed");
        }

        File file = convertToFile(cv);
        Map<String, String> cloudinaryMap = cloudinaryService.uploadRawFile(file, "CVs", email.toString());
        file.delete();
        Specialization specialization = specializationRepository.findById(specializationId).orElseThrow(() -> new NotFoundException("specialization Id: " + specializationId + " is not found"));
        DoctorRequest doctorRequest = new DoctorRequest();
        doctorRequest.setEmail(email);
        doctorRequest.setName(name);
        doctorRequest.setDeviceToken(deviceToken);
        doctorRequest.setCv(cloudinaryMap.get("url"));
        doctorRequest.setCvPublicId(cloudinaryMap.get("public_id"));
        doctorRequest.setGender(gender);
        doctorRequest.setSpecialization(specialization);
        doctorRequest.setAboutMe(aboutMe);
        doctorRequestRepository.save(doctorRequest);
        return doctorRequest;
    }


    private File convertToFile(MultipartFile multipartFile) throws IOException {
        File convFile = new File(multipartFile.getOriginalFilename());
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(multipartFile.getBytes());
        fos.close();
        return convFile;
    }

    private boolean userAlreadyExists(String email) {
        return userRepository.existsByEmail(email);
    }

    public void deleteDoctor(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));
        userRepository.delete(user);
    }

    public Map<Integer, List<Map<String, Object>>> getDoctorCountByMonth() {
        List<StatisticCountResponse> responses = userRepository.countUserByMonth(Role.DOCTOR);
        Map<Integer, List<Map<String, Object>>> result = new HashMap<>();

        for (StatisticCountResponse response : responses) {
            int year = response.getYear();
            Map<String, Object> data = new HashMap<>();
            data.put("month", response.getMonth());
            data.put("count", response.getCount());

            if (!result.containsKey(year)) {
                result.put(year, new ArrayList<>());
            }
            result.get(year).add(data);
        }

        return result;
    }

    public Page<DoctorRequest> doctorRequest(Integer page) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by("id").descending());
        return doctorRequestRepository.findByStatusOrNull(null, pageable);
    }

    public long rejectDoctorRequest(Long requestId) {
        DoctorRequest doctorRequest = doctorRequestRepository.findById(requestId).orElseThrow(() -> new UserNotFoundException("request not found"));
        doctorRequest.setStatus(DoctorRequestStatus.REJECTED);
        doctorRequestRepository.save(doctorRequest);
        return doctorRequestRepository.countByStatusOrNull(null);
    }

    @Transactional
    public long acceptDoctorRequest(Long requestId) throws ExecutionException, InterruptedException {
        DoctorRequest doctorRequest = doctorRequestRepository.findById(requestId).orElseThrow(() -> new UserNotFoundException("request not found"));
        doctorRequest.setStatus(DoctorRequestStatus.ACCEPTED);
        doctorRequestRepository.save(doctorRequest);

        User user = new User();
        user.setName(doctorRequest.getName());
        user.setEmail(doctorRequest.getEmail());
        user.setRole(Role.DOCTOR);
        user.setIsVerified(true);
        user.setPassword(passwordEncoder.encode(doctorRequest.getEmail()));
        user.setGender(doctorRequest.getGender());
        user.setCreationDate(LocalDateTime.now());
        user.setSpecialization(doctorRequest.getSpecialization());
        userRepository.save(user);
        NotificationRequestToken tokenRequest = new NotificationRequestToken();
        tokenRequest.setTitle("تم قبول طلبك للإنضمام لكادرنا الطبي");
        tokenRequest.setBody("يرجى مراجعة بريدك الألكتروني لمزيد من التفاصيل");
        tokenRequest.setDeviceToken(doctorRequest.getDeviceToken());
        try {
            fcmService.sendMessageToToken(tokenRequest);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
        emailService.sendSimpleEmail(
                doctorRequest.getEmail(),
                "تم قبول طلبك للإنضمام لكادرنا الطبي",
                "لتسجيل الدخول إلى التطبيق استخدم المعلومات التالية:\n" +
                        "البريد الالكتروني: " + user.getEmail() + "\n" +
                        "كلمة المرور: " + user.getEmail()
        );

        return doctorRequestRepository.countByStatusOrNull(null);
    }

    public long doctorRequestNonAnswered() {
        return doctorRequestRepository.countByStatusOrNull(null);
    }

    public List<DoctorRequest> getOldDoctorRequests() {
        LocalDateTime threshold = LocalDateTime.now().minusDays(10);
        return doctorRequestRepository.findDoctorRequestsOlderThanAndStatusIsNull(threshold);
    }
}


