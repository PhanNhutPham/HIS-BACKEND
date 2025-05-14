package com.booking.infrastructure.kafka.event;

import com.booking.domain.models.enums.WorkScheduleStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateWorkScheduleEvent {
    private String doctorId;
    @JsonProperty("wScheduleStatus")
    private WorkScheduleStatus wScheduleStatus = WorkScheduleStatus.ACTIVE;
    @JsonProperty("wScheduleHours")
    private List<String> wScheduleHours = new ArrayList<>();
    private String createdByStaffId;
    private LocalDate endTime;
    private RegularScheduleDTO regularSchedule;
    private List<ExceptionDTO> exceptions;

}
