package np.edu.nast.payroll.Payroll.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "system_config",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_system_config_key", columnNames = "key_name")
        }
)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class SystemConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "config_id")
    private Integer configId;

    @Column(name = "key_name", nullable = false, unique = true, length = 100)
    private String keyName;

    @Column(name = "value", nullable = false, columnDefinition = "TEXT")
    private String value;

    @Column(name = "description", length = 255)
    private String description;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "updated_by", nullable = false)
    @JsonIgnoreProperties({"password", "roles", "employee"}) // Prevent leaking sensitive info
    private User updatedBy;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    public void setTimestamp() {
        this.updatedAt = LocalDateTime.now();
    }
}