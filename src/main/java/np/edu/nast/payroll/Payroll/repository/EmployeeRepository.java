package np.edu.nast.payroll.Payroll.repository;

import np.edu.nast.payroll.Payroll.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Integer> {

    /**
     * Finds an employee by their email address.
     * Crucial for authentication and attendance validation.
     */
    Optional<Employee> findByEmail(String email);

    /**
     * Maps a Security User ID to an Employee Profile.
     * Used for the "My Profile" section in the React frontend.
     */
    Optional<Employee> findByUser_UserId(Integer userId);

    /**
     * Prevents duplicate email registration during employee onboarding.
     */
    boolean existsByEmail(String email);

    /**
     * Data for Dashboard Charts.
     * Counts how many employees joined per month.
     */
    @Query("SELECT FUNCTION('MONTH', e.joiningDate) as month, COUNT(e) " +
            "FROM Employee e " +
            "WHERE e.isActive = true " +
            "GROUP BY FUNCTION('MONTH', e.joiningDate)")
    List<Object[]> countActiveEmployeesPerMonth();

    /**
     * Flexible search for the Employee Directory.
     * Matches against ID, First Name, or Last Name (Case-Insensitive).
     */
    @Query("SELECT e FROM Employee e WHERE " +
            "CAST(e.empId AS string) = :query OR " +
            "LOWER(e.firstName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(e.lastName) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Employee> searchByIdOrName(@Param("query") String query);
}