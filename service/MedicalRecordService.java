package com.grad.akemha.service;

import com.grad.akemha.dto.medical_record.AdditionalRecordInfoRequest;
import com.grad.akemha.dto.medical_record.AdditionalRecordInfoResponse;
import com.grad.akemha.dto.medical_record.MedicalRecordRequest;
import com.grad.akemha.dto.medical_record.MedicalRecordResponse;
import com.grad.akemha.entity.AdditionalRecordInfo;
import com.grad.akemha.entity.MedicalRecord;
import com.grad.akemha.entity.User;
import com.grad.akemha.entity.enums.AdditionalInfoType;
import com.grad.akemha.exception.NotFoundException;
import com.grad.akemha.repository.MedicalRecordRepository;
import com.grad.akemha.repository.UserRepository;
import com.grad.akemha.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MedicalRecordService {
    private final MedicalRecordRepository medicalRecordRepository;
    private final JwtService jwtService;

    private final UserRepository userRepository;


    // Read
    public MedicalRecordResponse getLastMedicalRecord(HttpHeaders httpHeaders) {
        // finding the user to get it's medical record
        User user = jwtService.extractUserFromToken(httpHeaders);
//        Optional<MedicalRecord> optionalMedicalRecord = medicalRecordRepository.findLastMedicalRecordByUser(user, PageRequest.of(0, 1));
        Optional<MedicalRecord> optionalMedicalRecord = medicalRecordRepository.findFirstByUserOrderByCreateTimeDesc(user);
        if (optionalMedicalRecord.isPresent()) {
            MedicalRecord medicalRecord = optionalMedicalRecord.get();
            // getting the additional record info as a AdditionalRecordInfoResponse object
            Map<AdditionalInfoType, List<AdditionalRecordInfoResponse>> groupedInfoResponses = medicalRecord.getAdditionalRecordInfo().stream().map(AdditionalRecordInfoResponse::new)
                    .collect(Collectors.groupingBy(AdditionalRecordInfoResponse::getType));

            MedicalRecordResponse response = mapToMedicalRecordResponse(medicalRecord);
            response.setPreviousSurgeries(groupedInfoResponses.getOrDefault(AdditionalInfoType.PREVIOUS_SURGERIES, new ArrayList<>()));
            response.setPreviousIllnesses(groupedInfoResponses.getOrDefault(AdditionalInfoType.PREVIOUS_ILLNESSES, new ArrayList<>()));
            response.setAllergies(groupedInfoResponses.getOrDefault(AdditionalInfoType.ALLERGIES, new ArrayList<>()));
            response.setFamilyHistoryOfIllnesses(groupedInfoResponses.getOrDefault(AdditionalInfoType.FAMILY_HISTORY_OF_ILLNESSES, new ArrayList<>()));

            return response;


        } else {
            throw new NotFoundException("Medical Record is Not Found");
        }
    }

    private MedicalRecordResponse mapToMedicalRecordResponse(MedicalRecord medicalRecord) {
        MedicalRecordResponse response = new MedicalRecordResponse();
        response.setId(medicalRecord.getId());
        response.setCoffee(medicalRecord.getCoffee());
        response.setAlcohol(medicalRecord.getAlcohol());
        response.setMarried(medicalRecord.getMarried());
        response.setSmoker(medicalRecord.getSmoker());
        response.setCovidVaccine(medicalRecord.getCovidVaccine());
        response.setHeight(medicalRecord.getHeight());
        response.setWeight(medicalRecord.getWeight());
        response.setBloodType(medicalRecord.getBloodType());
        response.setCreateTime(medicalRecord.getCreateTime());
        return response;
    }

    public List<MedicalRecord> getAllMedicalRecord(Long userId) {
        return medicalRecordRepository.findAllByUserId(userId);
    }

    // Create
    public String createMedicalRecord(@NotNull MedicalRecordRequest medicalRecordRequest,
                                      HttpHeaders httpHeaders) {

        User user = jwtService.extractUserFromToken(httpHeaders);
        MedicalRecord medicalRecord = new MedicalRecord();

        List<AdditionalRecordInfo> list = settingAdditionalRecordInfoEntityList(
                medicalRecordRequest.getPreviousSurgeries(),
                medicalRecordRequest.getPreviousIllnesses(),
                medicalRecordRequest.getAllergies(),
                medicalRecordRequest.getFamilyHistoryOfIllnesses(),
                medicalRecord
        );
        System.out.println(list);

        medicalRecord.setCoffee(medicalRecordRequest.getCoffee());
        medicalRecord.setAlcohol(medicalRecordRequest.getAlcohol());
        medicalRecord.setMarried(medicalRecordRequest.getMarried());
        medicalRecord.setSmoker(medicalRecordRequest.getSmoker());
        medicalRecord.setCovidVaccine(medicalRecordRequest.getCovidVaccine());
        medicalRecord.setHeight(medicalRecordRequest.getHeight());
        medicalRecord.setWeight(medicalRecordRequest.getWeight());
        medicalRecord.setBloodType(medicalRecordRequest.getBloodType());
        medicalRecord.setUser(user);
        medicalRecord.setCreateTime(new Date());
        medicalRecord.setAdditionalRecordInfo(list);

        medicalRecordRepository.save(medicalRecord);

        return "تم إضافة ملف طبي بنجاح";
    }

    private List<AdditionalRecordInfo> settingAdditionalRecordInfoEntityList(List<AdditionalRecordInfoRequest> previousSurgeries,
                                                                             List<AdditionalRecordInfoRequest> previousIllnesses,
                                                                             List<AdditionalRecordInfoRequest> allergies,
                                                                             List<AdditionalRecordInfoRequest> familyHistoryOfIllnesses,
                                                                             MedicalRecord medicalRecord) {
        List<AdditionalRecordInfo> list = new ArrayList<>();

        System.out.println("ENTERED HERE");
        for (AdditionalRecordInfoRequest request :
                previousSurgeries
        ) {
            AdditionalRecordInfo additionalRecordInfo = new AdditionalRecordInfo();
            additionalRecordInfo.setName(request.getName());
            additionalRecordInfo.setDescription(request.getDescription());
            additionalRecordInfo.setType(request.getType());
            additionalRecordInfo.setCreateTime(new Date());
            additionalRecordInfo.setMedicalRecord(medicalRecord);
            list.add(additionalRecordInfo);
        }

        // previous illnesses
        for (AdditionalRecordInfoRequest request :
                previousIllnesses
        ) {
            AdditionalRecordInfo additionalRecordInfo = new AdditionalRecordInfo();
            additionalRecordInfo.setName(request.getName());
            additionalRecordInfo.setDescription(request.getDescription());
            additionalRecordInfo.setType(request.getType());
            additionalRecordInfo.setCreateTime(new Date());
            additionalRecordInfo.setMedicalRecord(medicalRecord);
            list.add(additionalRecordInfo);
        }


        // allergies
        for (AdditionalRecordInfoRequest request :
                allergies
        ) {
            AdditionalRecordInfo additionalRecordInfo = new AdditionalRecordInfo();
            additionalRecordInfo.setName(request.getName());
            additionalRecordInfo.setDescription(request.getDescription());
            additionalRecordInfo.setType(request.getType());
            additionalRecordInfo.setCreateTime(new Date());
            additionalRecordInfo.setMedicalRecord(medicalRecord);
            list.add(additionalRecordInfo);
        }


        // family history
        for (AdditionalRecordInfoRequest request :
                familyHistoryOfIllnesses
        ) {
            AdditionalRecordInfo additionalRecordInfo = new AdditionalRecordInfo();
            additionalRecordInfo.setName(request.getName());
            additionalRecordInfo.setDescription(request.getDescription());
            additionalRecordInfo.setType(request.getType());
            additionalRecordInfo.setCreateTime(new Date());
            additionalRecordInfo.setMedicalRecord(medicalRecord);
            list.add(additionalRecordInfo);
        }
        System.out.println("ENTERED HERE");
        System.out.println(list);
        return list;
    }

    public MedicalRecordResponse getMedicalRecordByUserId(Long id) {
        // finding the user to get it's medical record
        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("beneficiary Id: " + id + " is not found"));
//        Optional<MedicalRecord> optionalMedicalRecord = medicalRecordRepository.findLastMedicalRecordByUser(user, PageRequest.of(0, 1));
        Optional<MedicalRecord> optionalMedicalRecord = medicalRecordRepository.findFirstByUserOrderByCreateTimeDesc(user);
        if (optionalMedicalRecord.isPresent()) {
            MedicalRecord medicalRecord = optionalMedicalRecord.get();
            // getting the additional record info as a AdditionalRecordInfoResponse object
            Map<AdditionalInfoType, List<AdditionalRecordInfoResponse>> groupedInfoResponses = medicalRecord.getAdditionalRecordInfo().stream().map(AdditionalRecordInfoResponse::new)
                    .collect(Collectors.groupingBy(AdditionalRecordInfoResponse::getType));

            MedicalRecordResponse response = mapToMedicalRecordResponse(medicalRecord);
            response.setPreviousSurgeries(groupedInfoResponses.getOrDefault(AdditionalInfoType.PREVIOUS_SURGERIES, new ArrayList<>()));
            response.setPreviousIllnesses(groupedInfoResponses.getOrDefault(AdditionalInfoType.PREVIOUS_ILLNESSES, new ArrayList<>()));
            response.setAllergies(groupedInfoResponses.getOrDefault(AdditionalInfoType.ALLERGIES, new ArrayList<>()));
            response.setFamilyHistoryOfIllnesses(groupedInfoResponses.getOrDefault(AdditionalInfoType.FAMILY_HISTORY_OF_ILLNESSES, new ArrayList<>()));

            return response;
        } else {
            throw new NotFoundException("Medical Record is Not Found");
        }
    }
}
