package np.edu.nast.payroll.Payroll.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "attendance")
@Getter
<<<<<<< HEAD
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
=======
@Setter @NoArgsConstructor @AllArgsConstructor @Builder
>>>>>>> 3214be41b790e5d207ff8a4a5185d56a25676df5
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long attendanceId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "emp_id", nullable = false)
<<<<<<< HEAD
    private Employee employee;
=======
    private Employee employee; // Points to your employee table
>>>>>>> 3214be41b790e5d207ff8a4a5185d56a25676df5

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime checkInTime;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime checkOutTime;

<<<<<<< HEAD
    // GPS Coordinates for the Map Link
    private Double inGpsLat;
    private Double inGpsLong;

    /**
     * FIX: Added workLocation field.
     * This field stores the string representation (e.g., "28.8475, 80.3160")
     * to prevent "No Location" displaying in the Admin Dashboard.
     */
    private String workLocation;

=======
    private Double inGpsLat;
    private Double inGpsLong;

>>>>>>> 3214be41b790e5d207ff8a4a5185d56a25676df5
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate attendanceDate;

    @Column(nullable = false)
    private String status; // PRESENT, ABSENT, LEAVE
<<<<<<< HEAD
}
=======
}

>>>>>>> 3214be41b790e5d207ff8a4a5185d56a25676df5
