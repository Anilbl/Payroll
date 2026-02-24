package np.edu.nast.payroll.Payroll.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "payroll_extra_components", indexes = {
        @Index(name = "idx_extra_comp_payroll", columnList = "payroll_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PayrollExtraComponent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "extra_component_id")
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "payroll_id", nullable = false)
    @JsonBackReference // Prevents infinite loops when converting to JSON
    private Payroll payroll;

    @Column(name = "component_name", nullable = false)
    private String componentName;

    @Column(name = "amount", nullable = false)
    @Builder.Default
    private Double amount = 0.0;

    /**
     * Categorizes the component.
     * Values: EARNING, DEDUCTION, STATUTORY_DEDUCTION, REIMBURSEMENT
     */
    @Column(name = "type", length = 30, nullable = true)
    private String type;

    @Column(name = "description", columnDefinition = "TEXT", nullable = true)
    private String description;

    @Column(name = "calculation_type", nullable = true)
    private String calculationType; // e.g., FIXED, PERCENTAGE, FORMULA
}