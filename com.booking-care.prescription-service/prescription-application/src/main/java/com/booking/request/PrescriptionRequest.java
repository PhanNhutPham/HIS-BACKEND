package com.booking.request;

import lombok.*;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PrescriptionRequest {
    private String doctorId;
    private String patientId;
    private String paymentStatus;
    private String presDiagnosis;
    private Date presDate;
    private Date presExpiryDate;
    private List<PrescriptionDetailRequest> prescriptionDetails;
}
