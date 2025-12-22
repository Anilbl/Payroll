package np.edu.nast.payroll.Payroll.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "grade_salary_component")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GradeSalaryComponent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long gscId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "grade_id", nullable = false)
    private SalaryGrade grade;

    @ManyToOne(optional = false)
    @JoinColumn(name = "component_id", nullable = false)
    private SalaryComponent component;

    @Column(nullable = false)
    private Double value;
}
