package np.edu.nast.payroll.Payroll.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "designation")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Designation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "designation_id", nullable = false)
    private Integer designationId;

    @Column(name = "designation_title", nullable = false)
    private String designationTitle;

    /**
     * The starting salary for this specific position.
     * Useful for automatic salary calculation in the Employee service.
     */
    @Column(name = "base_salary", nullable = false)
    private Double baseSalary;
}