package com.tenant.app.repository;

import com.tenant.app.entity.RentPaymentRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRecordRepository extends JpaRepository<RentPaymentRecord, Long> {

    List<RentPaymentRecord> findByUnitNumber(String unitNumber);

    boolean existsByReferenceNumber(String referenceNumber);

    boolean existsByReferenceNumberAndIdNot(String referenceNumber, Long id);
}