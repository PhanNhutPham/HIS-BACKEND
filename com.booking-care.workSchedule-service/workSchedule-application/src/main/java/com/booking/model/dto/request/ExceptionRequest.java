package com.booking.model.dto.request;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
@Getter
@Setter
@Data
public class ExceptionRequest {
    private String ecptStatus;
    private String ecptNote;
    private LocalDate ecptTime;
}
