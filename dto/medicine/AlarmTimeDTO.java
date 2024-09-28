package com.grad.akemha.dto.medicine;

import com.grad.akemha.entity.AlarmTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AlarmTimeDTO {
    private LocalTime time;
    private boolean isTaken;

    public AlarmTimeDTO(AlarmTime alarmTime) {
        this.time = alarmTime.getTime();
        if (!alarmTime.getAlarmHistory().isEmpty()) {
            var temp = alarmTime.getAlarmHistory().get(alarmTime.getAlarmHistory().size() - 1).getTakeTime();
            LocalDate currentDate = LocalDate.now();
            LocalDate dateToCheck = temp.toLocalDate();
            if (dateToCheck.equals(currentDate)) {
                System.out.println("The date part of dateTimeToCheck is equal to today's date.");
                this.isTaken = true;
            } else {
                this.isTaken = false;
            }
        } else {
            // Handle the case where alarmHistory is empty
            System.out.println("No alarm history available.");
            this.isTaken = false;
        }
    }
}
