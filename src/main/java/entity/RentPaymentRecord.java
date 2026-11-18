package entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * One rent payment record: one row in the rent_payment_record table (Chapter 4.4).
 * TENANT only keeps records. It never moves money.
 */
@Entity
@Table(name = "rent_payment_record")
public class RentPaymentRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // the database creates the id
    private Long id;

    @Column(nullable = false, length = 100)
    private String tenantName;

    @Column(nullable = false, length = 20)
    private String unitNumber;

    // BigDecimal gives exact decimal math; double can round money wrongly.
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false)
    private LocalDate paymentDate;

    // unique = true is the last line of defense; the friendly 409 comes from the service.
    @Column(nullable = false, unique = true, length = 50)
    private String referenceNumber;

    // JPA needs a no-argument constructor to build objects when it reads rows.
    protected RentPaymentRecord() { }

    public RentPaymentRecord(String tenantName, String unitNumber, BigDecimal amount,
                             LocalDate paymentDate, String referenceNumber) {
        this.tenantName = tenantName;
        this.unitNumber = unitNumber;
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.referenceNumber = referenceNumber;
    }

    // No setId on purpose: the id must never change.
    public Long getId() {
        return id;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    public String getUnitNumber() {
        return unitNumber;
    }

    public void setUnitNumber(String unitNumber) {
        this.unitNumber = unitNumber;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }
}