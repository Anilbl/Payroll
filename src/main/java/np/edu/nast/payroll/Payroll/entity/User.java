package np.edu.nast.payroll.Payroll.entity;

<<<<<<< HEAD
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
=======
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
>>>>>>> 3214be41b790e5d207ff8a4a5185d56a25676df5
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user")
<<<<<<< HEAD
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "employee"}) // Prevents recursion
=======
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
>>>>>>> 3214be41b790e5d207ff8a4a5185d56a25676df5
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
<<<<<<< HEAD
    @Column(name = "user_id")
=======
>>>>>>> 3214be41b790e5d207ff8a4a5185d56a25676df5
    private Integer userId;

    @Column(nullable = false, unique = true)
    private String username;

<<<<<<< HEAD
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(nullable = false)
=======
    @Column(nullable = false)
    @JsonIgnore // 🔐 NEVER expose passwords
>>>>>>> 3214be41b790e5d207ff8a4a5185d56a25676df5
    private String password;

    @Column(nullable = false, unique = true)
    private String email;

<<<<<<< HEAD
    @Column(name = "emp_id", nullable = true)
    private Integer empId;

    @OneToOne(mappedBy = "user")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Employee employee;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id", nullable = false)
=======
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id", nullable = false)
    @JsonIgnore // 🔥 FIX: prevents recursion & security leak
>>>>>>> 3214be41b790e5d207ff8a4a5185d56a25676df5
    private Role role;

    private String status;

    @Column(name = "reset_token")
<<<<<<< HEAD
    private String resetToken;

    @Column(name = "token_expiry")
    private LocalDateTime tokenExpiry;

    @Column(name = "created_at")
=======
    @JsonIgnore
    private String resetToken;

    @Column(name = "token_expiry")
    @JsonIgnore
    private LocalDateTime tokenExpiry;

>>>>>>> 3214be41b790e5d207ff8a4a5185d56a25676df5
    private LocalDateTime createdAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
<<<<<<< HEAD
        if (this.status == null) this.status = "ACTIVE";
    }
}
=======
    }
}
>>>>>>> 3214be41b790e5d207ff8a4a5185d56a25676df5
