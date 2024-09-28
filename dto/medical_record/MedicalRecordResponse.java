package com.grad.akemha.dto.medical_record;

import com.grad.akemha.entity.MedicalRecord;
import com.grad.akemha.entity.enums.BloodType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedicalRecordResponse {
    private Long id;

    private Boolean coffee;

    private Boolean alcohol;

    private Boolean married;

    private Boolean smoker;

    private Boolean covidVaccine;

    private Double height;

    private Double weight;

    private BloodType bloodType;

    private Date createTime; // for beneficiary and doctor
//
//    private List<AdditionalRecordInfoResponse> additionalRecordInfoResponse;

    private List<AdditionalRecordInfoResponse> previousSurgeries;
    private List<AdditionalRecordInfoResponse> previousIllnesses;
    private List<AdditionalRecordInfoResponse> allergies;
    private List<AdditionalRecordInfoResponse> familyHistoryOfIllnesses;

    public MedicalRecordResponse(MedicalRecord medicalRecord) {
        this.id = medicalRecord.getId();
        this.createTime = medicalRecord.getCreateTime();
        this.coffee = medicalRecord.getCoffee();
        this.alcohol = medicalRecord.getAlcohol();
        this.married = medicalRecord.getMarried();
        this.smoker = medicalRecord.getSmoker();
        this.covidVaccine = medicalRecord.getCovidVaccine();
        this.height = medicalRecord.getHeight();
        this.weight = medicalRecord.getWeight();
        this.bloodType = medicalRecord.getBloodType();
        this.previousSurgeries = medicalRecord.getAdditionalRecordInfo().stream().map(AdditionalRecordInfoResponse::new).toList();
        this.previousIllnesses = medicalRecord.getAdditionalRecordInfo().stream().map(AdditionalRecordInfoResponse::new).toList();
        this.allergies = medicalRecord.getAdditionalRecordInfo().stream().map(AdditionalRecordInfoResponse::new).toList();
        this.familyHistoryOfIllnesses = medicalRecord.getAdditionalRecordInfo().stream().map(AdditionalRecordInfoResponse::new).toList();
    }

}
