package com.grad.akemha.service;

import com.grad.akemha.dto.consultation.consultationRequest.AnswerConsultationRequest;
import com.grad.akemha.dto.consultation.consultationResponse.ConsultationRes;
import com.grad.akemha.dto.notification.NotificationRequestToken;
import com.grad.akemha.dto.notification.NotificationRequestTopic;
import com.grad.akemha.dto.statistic.SpecializationConsultationCountResponse;
import com.grad.akemha.dto.statistic.StatisticCountResponse;
import com.grad.akemha.entity.Consultation;
import com.grad.akemha.entity.Image;
import com.grad.akemha.entity.Specialization;
import com.grad.akemha.entity.User;
import com.grad.akemha.entity.enums.ConsultationStatus;
import com.grad.akemha.entity.enums.ConsultationType;
import com.grad.akemha.exception.CloudinaryException;
import com.grad.akemha.exception.NotFoundException;
import com.grad.akemha.repository.ConsultationRepository;
import com.grad.akemha.repository.ImageRepository;
import com.grad.akemha.repository.SpecializationRepository;
import com.grad.akemha.repository.UserRepository;
import com.grad.akemha.security.JwtService;
import com.grad.akemha.service.cloudinary.CloudinaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
public class ConsultationService {


    @Autowired
    private ConsultationRepository consultationRepository;

    @Autowired
    private SpecializationRepository specializationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CloudinaryService cloudinaryService;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private FCMService fcmService;

    @Autowired
    private APIService apiService;


//    public List<ConsultationRes> getAllConsultations(Integer page) {
//        Pageable pageable = PageRequest.of(page, 10, Sort.by("id").descending());
//        Page<Consultation> consultationPage = consultationRepository.findAll(pageable);
//        List<ConsultationRes> consultationResponseList = consultationPage.getContent().stream().map(consultation -> new ConsultationRes(consultation)).toList();
//        return consultationResponseList;
////        List<Consultation> consultationList = consultationRepository.findAll();
////        List<ConsultationRes> consultationResponseList = consultationList.stream().map(consultation -> new ConsultationRes(consultation)).toList();
////        return consultationResponseList;
//    }

    public Page<Consultation> getAllConsultations(Integer page) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by("id").descending());
        return consultationRepository.findAll(pageable);

    }


    public List<ConsultationRes> getConsultationsBySpecializationId(Long specializationId, Integer page) {
        if (specializationRepository.findById(specializationId).isPresent()) {
            Pageable pageable = PageRequest.of(page, 10, Sort.by("id").descending());
            Page<Consultation> consultationList = consultationRepository.findBySpecializationId(specializationId, pageable);
            List<ConsultationRes> consultationResponseList = consultationList.stream().map(consultation -> new ConsultationRes(consultation)).toList();
            return consultationResponseList;
        } else {
            throw new NotFoundException("SpecializationId " + specializationId + " is not found");
        }

    }

    public Page<ConsultationRes> adminGetConsultationsBySpecialization(Long specializationId, Integer page) {
        if (specializationRepository.findById(specializationId).isPresent()) {
            Pageable pageable = PageRequest.of(page, 10, Sort.by("id").descending());
            Page<Consultation> consultationPage = consultationRepository.findBySpecializationId(specializationId, pageable);
            return consultationPage.map(ConsultationRes::new);
        } else {
            throw new NotFoundException("SpecializationId " + specializationId + " is not found");
        }

    }

    public List<ConsultationRes> getAnsweredConsultationsBySpecializationId(Long specializationId, Integer page) {
        if (specializationRepository.findById(specializationId).isPresent()) {
            Pageable pageable = PageRequest.of(page, 10, Sort.by("id").descending());
            List<Consultation> consultationList = consultationRepository.findAllByConsultationAnswerIsNotNullAndSpecializationIdAndConsultationTypeNot(specializationId, ConsultationType.PRIVATE, pageable);
            List<ConsultationRes> consultationResponseList = consultationList.stream().map(consultation -> new ConsultationRes(consultation)).toList();
            return consultationResponseList;
        } else {
            throw new NotFoundException("SpecializationId " + specializationId + " is not found");
        }
    }

    public List<ConsultationRes> findConsultationsByKeyword(String keyword) {
        List<Consultation> consultationList = consultationRepository.findByKeywordInConsultationText(keyword);
        List<ConsultationRes> consultationResponseList = consultationList.stream().map(consultation -> new ConsultationRes(consultation)).toList();
        return consultationResponseList;
    }
    public Map<String, List<ConsultationRes>> adminSearchConsultation(String keyword, Integer page) throws IOException {
        List<Integer> integerIds = apiService.searchDocument(keyword, page);
        List<Long> longIds = integerIds.stream()
                .map(Integer::longValue)
                .collect(Collectors.toList());
        System.out.println("longIds"+longIds);
        List<Consultation> consultations = consultationRepository.findAllById(longIds);
        Map<Long, Consultation> consultationMap = consultations.stream()
                .collect(Collectors.toMap(Consultation::getId, consultation -> consultation));
        List<ConsultationRes> orderedConsultations = longIds.stream()
                .map(consultationMap::get)
                .filter(Objects::nonNull)
                .map(ConsultationRes::new)
                .collect(Collectors.toList());
        Map<String, List<ConsultationRes>> res = new HashMap<>();
        res.put("content", orderedConsultations);

        return res;
    }






    public List<ConsultationRes> searchConsultation(String keyword, Integer page) throws IOException {
        List<Integer> integerIds = apiService.searchDocument(keyword, page);
        List<Long> longIds = integerIds.stream()
                .map(Integer::longValue)
                .collect(Collectors.toList());
        List<Consultation> consultations = consultationRepository.findAllById(longIds);
        Map<Long, Consultation> consultationMap = consultations.stream()
                .collect(Collectors.toMap(Consultation::getId, consultation -> consultation));
        List<ConsultationRes> orderedConsultations = longIds.stream()
                .map(consultationMap::get)
                .filter(Objects::nonNull)
                .map(ConsultationRes::new)
                .collect(Collectors.toList());
        return orderedConsultations;
    }




    //    @SneakyThrows
    @Transactional
    public Consultation postConsultation(HttpHeaders httpHeaders, String title, String consultationText, Long specializationId, ConsultationType consultationType, List<MultipartFile> files) {
        Long beneficiaryId = Long.parseLong(jwtService.extractUserId(httpHeaders));
        User beneficiary = userRepository.findById(beneficiaryId).orElseThrow(() -> new NotFoundException("beneficiary Id: " + beneficiaryId + " is not found"));

        Specialization specialization = specializationRepository.findById(specializationId).orElseThrow(() -> new NotFoundException("specialization Id: " + specializationId + " is not found"));

        // Upload and save multiple images
        List<Image> images = new ArrayList<>();
        if (files != null && !files.isEmpty()) {
            images = files.stream().map(file -> {
                Image image = new Image();
                image.setImageUrl(cloudinaryService.uploadFile(file, "Consultations", beneficiaryId.toString()));
                if (image.getImageUrl() == null) {
                    throw new CloudinaryException("Image upload failed");
                }
                return imageRepository.save(image);
            }).collect(Collectors.toList());
        }

        Consultation consultation = Consultation.builder().title(title).consultationText(consultationText).specialization(specialization).consultationStatus(ConsultationStatus.NULL).beneficiary(beneficiary).consultationType(consultationType).images(images).createTime(new Date()).build();
        consultationRepository.save(consultation);
        // modifying body to be small
//        String notificationBody = shortenString(consultation.getTitle());
//        sendConsultationNotification(specialization.getSpecializationType(), "New Consultation", notificationBody);

        String res = apiService.addDocument(consultation.getConsultationText(), consultation.getId());
        System.out.println(res);

        return consultation;
    }

    //    @SneakyThrows
    public Consultation answerConsultation(AnswerConsultationRequest request, HttpHeaders httpHeaders) {
        Long doctorId = Long.parseLong(jwtService.extractUserId(httpHeaders));
        User doctor = userRepository.findById(doctorId).orElseThrow(() -> new NotFoundException("doctor Id: " + doctorId + " is not found"));
        Consultation consultation = consultationRepository.findById(request.consultationId()).orElseThrow(() -> new NotFoundException("consultation not found"));
        consultation.setDoctor(doctor);
        consultation.setConsultationAnswer(request.answer());
        consultation.setConsultationStatus(ConsultationStatus.ARCHIVED);
        consultationRepository.save(consultation);

        // modifying body to be small
        String notificationBody = shortenString(request.answer());
//        String topic = "answered_" + consultation.getBeneficiary().getId().toString();
//        sendConsultationNotification(topic, "تمت الإجابة على استشارتك", notificationBody);

        NotificationRequestToken tokenRequest = new NotificationRequestToken();
        tokenRequest.setTitle("تمت الإجابة على استشارتك");
        tokenRequest.setBody(notificationBody);
        tokenRequest.setDeviceToken(consultation.getBeneficiary().getDeviceToken());
        try {
            fcmService.sendMessageToToken(tokenRequest);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }

        return consultation;
    }

    public List<ConsultationRes> getAllAnsweredConsultations(Integer page) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by("id").descending());
        Page<Consultation> consultationPage = consultationRepository.findAllByConsultationAnswerIsNotNull(pageable);
//        List<Consultation> consultationList = consultationRepository.findAllByConsultationAnswerIsNotNull(pageable);
        List<ConsultationRes> consultationResponseList = consultationPage.stream().map(consultation -> new ConsultationRes(consultation)).toList();
        return consultationResponseList;
    }

    public List<ConsultationRes> getAllAnsweredConsultationsExceptPrivateAndExceptActive(Integer page) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by("id").descending());
//        Page<Consultation> consultationPage = consultationRepository.findAllByConsultationAnswerIsNotNullAndConsultationTypeNot(ConsultationType.PRIVATE, pageable);
        Page<Consultation> consultationPage = consultationRepository.findAllByConsultationAnswerIsNotNullAndConsultationTypeNotAndConsultationStatusNot(ConsultationType.PRIVATE, ConsultationStatus.ACTIVE,pageable);

        //findAllByConsultationAnswerIsNotNullAndConsultationTypeNotAndConsultationStatusNot
//        List<Consultation> consultationList = consultationRepository.findAllByConsultationAnswerIsNotNull(pageable);
        List<ConsultationRes> consultationResponseList = consultationPage.stream().map(consultation -> new ConsultationRes(consultation)).toList();
        return consultationResponseList;
    }

    public List<ConsultationRes> getPersonalNullConsultations(HttpHeaders httpHeaders) {
        Long beneficiaryId = Long.parseLong(jwtService.extractUserId(httpHeaders));
        List<Consultation> consultationList = consultationRepository.findByBeneficiaryIdAndConsultationStatus(beneficiaryId, ConsultationStatus.NULL);
        List<ConsultationRes> consultationResponseList = consultationList.stream().map(consultation -> new ConsultationRes(consultation)).toList();
        return consultationResponseList;
    }

    public List<ConsultationRes> getPersonalAnsweredConsultations(HttpHeaders httpHeaders, Integer page) {
        Long beneficiaryId = Long.parseLong(jwtService.extractUserId(httpHeaders));
        Pageable pageable = PageRequest.of(page, 10, Sort.by("id").descending());
        List<Consultation> consultationList = consultationRepository.findAllByConsultationAnswerIsNotNullAndBeneficiaryId(beneficiaryId, pageable);
        List<ConsultationRes> consultationResponseList = consultationList.stream().map(consultation -> new ConsultationRes(consultation)).toList();
        return consultationResponseList;
    }

    public List<ConsultationRes> getPendingConsultationsForDoctor(HttpHeaders httpHeaders, Integer page) { //الاستشارات يلي بيقدر يجاوب عليهاالدكتور
        Long doctorId = Long.parseLong(jwtService.extractUserId(httpHeaders));
        User doctor = userRepository.findById(doctorId).orElseThrow(() -> new NotFoundException("doctor Id: " + doctorId + " is not found"));
//        List<Consultation> consultationList = consultationRepository.findAllByConsultationAnswerIsNullAndSpecializationId(doctor.getSpecialization().getId());
//        List<Consultation> consultationListTwo = consultationRepository.findAllBySpecializationIsPublicTrue();
        Pageable pageable = PageRequest.of(page, 10, Sort.by("id").ascending());

        List<Consultation> consultationList = consultationRepository.findByConsultationAnswerIsNullAndSpecializationIdOrSpecializationIsPublicTrue(doctor.getSpecialization().getId(), pageable);

        List<ConsultationRes> consultationResponseList = consultationList.stream().map(consultation -> new ConsultationRes(consultation)).toList();
        return consultationResponseList;
    }

    public List<ConsultationRes> getBeneficiaryAnsweredConsultations(Long beneficiaryId, Integer page) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by("id").descending());
        List<Consultation> consultationList = consultationRepository.findAllByConsultationAnswerIsNotNullAndBeneficiaryId(beneficiaryId, pageable);
        List<ConsultationRes> consultationResponseList = consultationList.stream().map(ConsultationRes::new).toList();
        return consultationResponseList;
    }

    public List<ConsultationRes> getDoctorAnsweredConsultations(HttpHeaders httpHeaders, Integer page) {
        Long doctorId = Long.parseLong(jwtService.extractUserId(httpHeaders));
        Pageable pageable = PageRequest.of(page, 10, Sort.by("id").descending());
        List<Consultation> consultationList = consultationRepository.findAllByDoctorId(doctorId, pageable);
        List<ConsultationRes> consultationResponseList = consultationList.stream().map(consultation -> new ConsultationRes(consultation)).toList();
        return consultationResponseList;
    }

    public void deleteConsultation(Long consultationId) throws IOException {
        apiService.deleteDocument(consultationId);
        consultationRepository.deleteById(consultationId);
    }

    public Consultation makeConsultationAnonymous(Long consultationId) {
        Consultation consultation = consultationRepository.findById(consultationId).orElseThrow(() -> new NotFoundException("consultation not found"));
        consultation.setConsultationType(ConsultationType.ANONYMOUS);
        consultationRepository.save(consultation);
        return consultation;
    }

    public long getAnsweredConsultationByDoctorCount(Long doctorId) {
        return consultationRepository.countAnsweredConsultationsByDoctorId(doctorId);
    }


    private String shortenString(String input) {
        final int maxLength = 50;
        if (input.length() > maxLength) {
            return input.substring(0, maxLength) + "...";
        } else {
            return input + "...";
        }
    }

    // sami
    public void sendConsultationNotification(String topic, String title, String body) throws ExecutionException, InterruptedException {
        //this methods using for sending Notification
        NotificationRequestTopic request = new NotificationRequestTopic();
        request.setTopic(topic);
        request.setTitle(title);
        request.setBody(body);
        fcmService.sendMessageToTopic(request);
    }

//    public List<StatisticCountResponse> countConsultationsByMonth() {
//        return consultationRepository.countConsultationsByMonth();
//    }

    public Map<Integer, List<Map<String, Object>>> countConsultationsByMonth() {
        List<StatisticCountResponse> responses = consultationRepository.countConsultationsByMonth();
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

    public List<SpecializationConsultationCountResponse> countConsultationsBySpecialization() {
        return consultationRepository.countConsultationsBySpecialization();
    }

    public Consultation rateConsultation(Long consultationId, Double rating) {
        Optional<Consultation> optionalConsultation = consultationRepository.findById(consultationId);
        if (optionalConsultation.isPresent()) {
            Consultation consultation = optionalConsultation.get();
            consultation.setRating(rating);
            return consultationRepository.save(consultation);
        } else {
            throw new IllegalArgumentException("Consultation not found with id: " + consultationId);
        }
    }

    public Consultation changeConsultationChatStatus(Long consultationId, Boolean status) {
        Optional<Consultation> optionalConsultation = consultationRepository.findById(consultationId);
        if (optionalConsultation.isPresent()) {
            Consultation consultation = optionalConsultation.get();
            consultation.setIsChatOpen(status);
            return consultationRepository.save(consultation);
        } else {
            throw new IllegalArgumentException("Consultation not found with id: " + consultationId);
        }
    }
}
