package com.grad.akemha.dto.medicine;

import com.grad.akemha.entity.AlarmTime;
import com.grad.akemha.entity.Medicine;
import com.grad.akemha.entity.enums.AlarmRoutine;
import com.grad.akemha.entity.enums.AlarmRoutineType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MedicineResDTO {
    private Long id;
    private String name;
    private String description;
    private Long localId;
    private List<AlarmTimeDTO> alarmTimes;
//    private List<AlarmTimeDTO> alarmTimes;
    private LocalDate startDate;
    private LocalDate endDate;
    private AlarmRoutine alarmRoutine;
    private AlarmRoutineType alarmRoutineType;
//    private String alarmWeekDay;
//    private Integer selectedDayInMonth;

    public MedicineResDTO(Medicine medicine) {
        this.id = medicine.getId();
        this.name = medicine.getName();
        this.description = medicine.getDescription();
        this.localId = medicine.getLocalId();
        this.startDate = medicine.getStartDate();
        this.endDate = medicine.getEndDate();
        this.alarmRoutine = medicine.getAlarmRoutine();
        this.alarmRoutineType = medicine.getAlarmRoutineType();
        this.alarmTimes = medicine.getAlarmTimes().stream().map(AlarmTimeDTO::new).toList();

//        if (!medicine.getAlarmWeekDay().isEmpty()) {
//            this.alarmWeekDay = medicine.getAlarmWeekDay();
//        }
//        if (medicine.getSelectedDayInMonth() != null) {
//            this.selectedDayInMonth = medicine.getSelectedDayInMonth();
//        }
    }

}
