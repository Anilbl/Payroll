package np.edu.nast.payroll.Payroll.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "employee")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "emp_id")
    private Integer empId;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    @JsonIgnore // 🔥 Prevents infinite JSON recursion (Fixes 400 Bad Request)
    @OneToOne(fetch = FetchType.EAGER, optional = true)
    @JoinColumn(name = "user_id", referencedColumnName = "userId", nullable = true)
    @JsonIgnore // 🔥 CRITICAL FIX
    private User user;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String contact;

    @Column(nullable = false)
    private String maritalStatus;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "position_id", nullable = false)
    private Designation position;

    @Column(nullable = false)
    private String education;

    @Column(nullable = false)
    private String employmentStatus;

    @Column(nullable = false)
    private LocalDate joiningDate;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private Double basicSalary;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "dept_id", nullable = false)
    private Department department;

    @Column(nullable = true)
    @Column(nullable = false)
    private Boolean isActive;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Transient
    private String password;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.joiningDate == null) this.joiningDate = LocalDate.now();
        if (this.isActive == null) this.isActive = true;
        if (this.maritalStatus == null) this.maritalStatus = "SINGLE";
        if (this.employmentStatus == null) this.employmentStatus = "FULL_TIME";
        if (this.basicSalary == null) this.basicSalary = 0.0;
        if (this.allowances == null) this.allowances = 0.0;
        if (this.deductions == null) this.deductions = 0.0;
    }

}
