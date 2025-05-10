package com.booking.service;

import com.booking.models.entities.Prescription;
import com.booking.request.PrescriptionRequest;


import java.util.List;
import java.util.Optional;

public interface PrescriptionService {
    Prescription createPrescription(Prescription prescription);
    List<Prescription> getAllPrescriptions();
    Optional<Prescription> getPrescriptionById(String id);
    Prescription updatePrescription(String id, Prescription prescription);
    void deletePrescription(String id);
    Prescription createPrescriptionWithDetails(PrescriptionRequest request);
}
