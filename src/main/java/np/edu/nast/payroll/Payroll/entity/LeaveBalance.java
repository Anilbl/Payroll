package np.edu.nast.payroll.Payroll.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.web.bind.annotation.CrossOrigin;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "leave_balance")
@CrossOrigin(origins = "http://localhost:5173")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class LeaveBalance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "balance_id") // Matches the column name from your MySQL table
    private Long id;
    @Column(nullable = false)
    private Integer balanceId;

    @ManyToOne
    @JoinColumn(name = "emp_id",  nullable = false)
    private Employee employee;

    @ManyToOne
    @JoinColumn(name = "leave_type_id", nullable = false)
    private LeaveType leaveType;

    @Column(name = "current_balance_days")
    @Column(nullable = false)
    private Double currentBalanceDays;

    @Column(name = "leave_taken")
    private Double leaveTaken;

    @Column(name = "year")
    @Column(nullable = false)
    private Integer year;
}
