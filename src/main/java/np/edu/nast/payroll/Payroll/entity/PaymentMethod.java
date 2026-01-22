package np.edu.nast.payroll.Payroll.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "payment_method")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PaymentMethod {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_method_id")
    private Integer paymentMethodId;

    @Column(name = "method_name", nullable = false)
    private String methodName;

    @Column(name = "details", nullable = false)
    private String details;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true; // New field for soft delete
}