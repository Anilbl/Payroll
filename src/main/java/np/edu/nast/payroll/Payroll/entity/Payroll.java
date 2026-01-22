package np.edu.nast.payroll.Payroll.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "payroll")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Table(
        name = "payroll",
        indexes = {
                @Index(name = "idx_payroll_emp", columnList = "emp_id"),
                @Index(name = "idx_payroll_pay_date", columnList = "pay_date")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payroll {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payroll_id")
    private Integer payrollId;

    @Column(name = "payslip_ref", unique = true)
    private String payslipRef;

    @ManyToOne(fetch = FetchType.EAGER)
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "emp_id", nullable = false)
    private Employee employee;

    @ManyToOne(fetch = FetchType.EAGER)
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "processed_by", nullable = false)
    private User processedBy;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "payment_account_id")
    /* =========================================================
       FIX: Changed "account_id" to "payment_account_id"
       to match your MySQL table's mandatory column name.
       ========================================================= */
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "payment_account_id", nullable = false)
    private BankAccount paymentAccount;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "payment_method_id")
    @JoinColumn(name = "pay_group_id", nullable = true)
    private PayGroup payGroup;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "payment_method_id", nullable = false)
    private PaymentMethod paymentMethod;

    @Column(nullable = true)
    private LocalDate payPeriodStart;

    @Column(nullable = true)
    private LocalDate payPeriodEnd;

    @Column(nullable = true)
    private LocalDate payDate;

    @Column(nullable = false)
    private Double grossSalary = 0.0;

    @Column(nullable = false)
    private Double totalAllowances = 0.0;
    private Double totalDeductions = 0.0;
    private Double totalTax = 0.0;

    @Column(nullable = false)
    private Double netSalary = 0.0;

    @Column(nullable = false, length = 20)
    private String status;

    @Column(name = "is_voided")
    private boolean isVoided = false;

    @Column(columnDefinition = "TEXT")
    private String remarks;

    @Column(name = "currency_code")
    private String currencyCode = "USD";

    @Column(name = "processed_at")
    @Builder.Default
    @Column(nullable = false, updatable = false)
    private LocalDateTime processedAt = LocalDateTime.now();

    @PrePersist
    protected void onCreate() {
        if (this.processedAt == null) {
            this.processedAt = LocalDateTime.now();
        }
    @JsonProperty("employeeName")
    public String getEmployeeName() {
        return this.employee != null ? this.employee.getFirstName() + " " + this.employee.getLastName() : "N/A";
    }

    @JsonProperty("id")
    public Integer getId() {
        return this.payrollId;
    }
}