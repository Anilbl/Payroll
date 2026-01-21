package np.edu.nast.payroll.Payroll.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "payroll")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Payroll {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payroll_id")
    private Integer payrollId;

    @Column(name = "payslip_ref", unique = true)
    private String payslipRef;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "emp_id", nullable = false)
    private Employee employee;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "processed_by", nullable = false)
    private User processedBy;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "payment_account_id")
    private BankAccount paymentAccount;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "payment_method_id")
    private PaymentMethod paymentMethod;

    private LocalDate payPeriodStart;
    private LocalDate payPeriodEnd;
    private LocalDate payDate;

    private Double grossSalary = 0.0;
    private Double totalAllowances = 0.0;
    private Double totalDeductions = 0.0;
    private Double totalTax = 0.0;
    private Double netSalary = 0.0;

    private String status;

    @Column(name = "is_voided")
    private boolean isVoided = false;

    @Column(columnDefinition = "TEXT")
    private String remarks;

    @Column(name = "currency_code")
    private String currencyCode = "USD";

    @Column(name = "processed_at")
    private LocalDateTime processedAt = LocalDateTime.now();

    @JsonProperty("employeeName")
    public String getEmployeeName() {
        return this.employee != null ? this.employee.getFirstName() + " " + this.employee.getLastName() : "N/A";
    }

    @JsonProperty("id")
    public Integer getId() {
        return this.payrollId;
    }
}