package com.tenant.app.service;

import com.tenant.app.repository.PaymentRecordRepository;
import org.springframework.stereotype.Service;

@Service
public class PaymentRecordService {

    private final PaymentRecordRepository repository;

    public PaymentRecordService(PaymentRecordRepository repository) {
        this.repository = repository;
    }
}
import com.tenant.app.dto.PaymentRecordRequest;
import com.tenant.app.dto.PaymentRecordResponse;
import com.tenant.app.entity.RentPaymentRecord;
import com.tenant.app.exception.DuplicateReferenceException;
public PaymentRecordResponse create(PaymentRecordRequest req) {
    if (repository.existsByReferenceNumber(req.referenceNumber())) {
        throw new DuplicateReferenceException(req.referenceNumber());
    }
    RentPaymentRecord saved = repository.save(new RentPaymentRecord(
            req.tenantName(), req.unitNumber(), req.amount(),
            req.paymentDate(), req.referenceNumber()));
    return toResponse(saved);
}

private PaymentRecordResponse toResponse(RentPaymentRecord r) {
    return new PaymentRecordResponse(r.getId(), r.getTenantName(), r.getUnitNumber(),
            r.getAmount(), r.getPaymentDate(), r.getReferenceNumber());
}