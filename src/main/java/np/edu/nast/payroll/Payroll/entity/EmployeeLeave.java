package np.edu.nast.payroll.Payroll.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "employee_leave")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeLeave {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Integer leaveId;

    @ManyToOne
    @JoinColumn(name = "emp_id", nullable = false)
    private Employee employee;

    @ManyToOne
    @JoinColumn(name = "leave_type_id", nullable = false)
    private LeaveType leaveType;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Column(nullable = false)
    private Integer totalDays;

    @Column(nullable = false)
    private String reason;

    @Column(nullable = false)
    private String status; // e.g., "Pending", "Approved", "Rejected"

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime requestedAt;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "approved_by_user_id", nullable = true)
    private User approvedBy;

    @Column(name = "approved_at", nullable = true)
    private LocalDateTime approvedAt;

    // --- MERGED CALLBACK METHOD ---
    @PrePersist
    public void handleBeforeInsert() {
        // 1. Calculate totalDays to prevent DataIntegrityViolationException
        if (this.startDate != null && this.endDate != null) {
            long days = ChronoUnit.DAYS.between(this.startDate, this.endDate) + 1;
            this.totalDays = (int) days;
        }

        // 2. Default status to "Pending" if not set
        if (this.status == null || this.status.isEmpty()) {
            this.status = "Pending";
        }
    }
}