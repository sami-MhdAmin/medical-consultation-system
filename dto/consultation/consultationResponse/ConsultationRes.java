package com.grad.akemha.dto.consultation.consultationResponse;

import com.grad.akemha.entity.Consultation;
import com.grad.akemha.entity.Specialization;
import com.grad.akemha.entity.enums.ConsultationStatus;
import com.grad.akemha.entity.enums.ConsultationType;
import com.grad.akemha.entity.enums.Gender;
import com.grad.akemha.repository.ConsultationRepository;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConsultationRes {
    private Long id;
    private String consultationText;
    private String consultationAnswer;
    private List<String> images;
    private ConsultationStatus consultationStatus;
    private Specialization specialization;
    private UserInConsultationRes beneficiary;
    private UserInConsultationRes doctor;
    private ConsultationType consultationType;
    private Date createTime;
    private Date updateAnswerTime;
    private String title;
    private Double rating;

    private Boolean isChatOpen;

    public ConsultationRes(Consultation consultation) {
        this.rating = consultation.getRating();
        this.id = consultation.getId();
        this.consultationText = consultation.getConsultationText();
        this.consultationAnswer = consultation.getConsultationAnswer();
        this.images = consultation.getImages().stream().map(image -> new String(image.getImageUrl())).toList();
        this.consultationStatus = consultation.getConsultationStatus();
        this.consultationType = consultation.getConsultationType();
        this.createTime = consultation.getCreateTime();
        this.updateAnswerTime = consultation.getUpdateAnswerTime();
        this.specialization = consultation.getSpecialization();
        this.title = consultation.getTitle();
        this.isChatOpen = consultation.getIsChatOpen();
        if (consultation.getConsultationType() != ConsultationType.ANONYMOUS) {
            this.beneficiary = new UserInConsultationRes(consultation.getBeneficiary().getId(),consultation.getBeneficiary().getName(), consultation.getBeneficiary().getProfileImage(), consultation.getBeneficiary().getGender());
        }
        if (consultation.getDoctor() != null) {
            this.doctor = new UserInConsultationRes(consultation.getDoctor().getId(),consultation.getDoctor().getName(), consultation.getDoctor().getProfileImage(), consultation.getDoctor().getGender());
        }
//        this.specialization = consultation.getTextFiles().stream().map(file -> new FileResponse(file)).toList();
    }

    record UserInConsultationRes(Long id,String name, String profileImg, Gender gender) {
    }
}


//@Data
//@Builder
//@AllArgsConstructor
//@NoArgsConstructor
//class UserInConsultationRes{
//    private String name;
//    private String profileImg;
//    private Gender gender;
//    public UserInConsultationRes(User user) {
//        this.name = user.getName();
//        this.profileImg = user.getProfileImage();
//        this.gender = user.getGender();
//    }
//}