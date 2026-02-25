package np.edu.nast.payroll.Payroll.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "password", nullable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @Column(nullable = false, unique = true)
    private String email;

    // Primary Role (Sent from Frontend)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id", nullable = false)
    @JsonIgnoreProperties("users")
    private Role role;

    private String status;

    @OneToOne(mappedBy = "user")
    @JsonIgnore
    private Employee employee;

    @Column(name = "is_first_login")
    @Builder.Default
    private boolean isFirstLogin = true;

    @Column(name = "reset_token")
    @JsonIgnore
    private String resetToken;

    @Column(name = "token_expiry")
    @JsonIgnore
    private LocalDateTime tokenExpiry;

    // 🔥 AUTOMATED MULTI-ROLE FLAGS
    @Column(name = "is_admin")
    private boolean isAdmin =false;

    @Column(name = "is_accountant")
    private boolean isAccountant = false;

    @Column(name = "has_employee_role")
    private boolean hasEmployeeRole = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    /**
     * Logic to automatically set boolean flags based on the selected Role.
     * Triggers before Saving (Persist) and before Updating.
     */
    @PrePersist
    @PreUpdate
    public void syncRoleFlagsAndTimestamp() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }

        if (this.role != null && this.role.getRoleName() != null) {
            String roleName = this.role.getRoleName().toUpperCase();

            switch (roleName) {
                case "ADMIN":
                    this.isAdmin = true;
                    this.isAccountant = true;
                    this.hasEmployeeRole = true;
                    break;

                case "ACCOUNTANT":
                    this.isAdmin = false;
                    this.isAccountant = true;
                    this.hasEmployeeRole = true;
                    break;

                case "EMPLOYEE":
                default:
                    this.isAdmin = false;
                    this.isAccountant = false;
                    this.hasEmployeeRole = true;
                    break;
            }
        }
    }
}