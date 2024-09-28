package com.grad.akemha.dto.medical_record;

import com.grad.akemha.entity.AdditionalRecordInfo;
import com.grad.akemha.entity.enums.AdditionalInfoType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdditionalRecordInfoResponse {
    private Long id;

    private String name;

    private String description;

    private AdditionalInfoType type;

    private Date createTime;

    public AdditionalRecordInfoResponse(AdditionalRecordInfo additionalRecordInfo){
        this.id = additionalRecordInfo.getId();
        this.name = additionalRecordInfo.getName();
        this.description = additionalRecordInfo.getDescription();
        this.type = additionalRecordInfo.getType();
        this.createTime = additionalRecordInfo.getCreateTime();
    }

}
