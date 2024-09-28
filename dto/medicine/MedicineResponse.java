package com.grad.akemha.dto.medicine;

import com.grad.akemha.entity.Medicine;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MedicineResponse {
    private Long id;
    private String name;
    private String description;

    public MedicineResponse(Medicine medicine) {
        this.id=medicine.getId();
        this.name=medicine.getName();
        this.description=medicine.getDescription();
    }
}
