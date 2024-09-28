package com.grad.akemha.dto.medicine;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AddMedicineRequest {
    @NotNull(message = "name can't be Null")
    private String medicamentName;

//    @NotNull(message = "description can't be Null")
//    private String description; //TODO

    @NotNull(message = "startDate can't be Null")
    private LocalDate startDate;

//    @NotNull(message = "endDate can't be Null")
    private LocalDate  endDate;

    @NotNull(message = "alarmTime can't be Null")
    private List<LocalTime> alarmTimes;

    private String medicamentType;

    private String alarmRoutine;

    private String alarmRoutineType;

    private Integer selectedDayInMonth;

    private Long id;

    private String alarmWeekDay;
}
