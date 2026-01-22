package np.edu.nast.payroll.Payroll.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "bank")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Bank {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bank_id")
    private Integer bankId;

    @Column(name = "bank_name", nullable = false)
    private String bankName;

    @Column(name = "branch_name", nullable = false)
    private String branchName;

    @Column(name = "branch_code", nullable = false)
    private String branchCode;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "contact_number", nullable = false)
    private String contactNumber;
}