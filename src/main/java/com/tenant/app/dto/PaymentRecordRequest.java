package com.tenant.app.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PaymentRecordRequest(
        String tenantName,
        String unitNumber,
        BigDecimal amount,
        LocalDate paymentDate,
        String referenceNumber) { }