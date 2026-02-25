package np.edu.nast.payroll.Payroll.repository;
import np.edu.nast.payroll.Payroll.entity.Designation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DesignationRepository extends JpaRepository<Designation, Integer> {
    // Add this line to fix the error on line 52
    Optional<Designation> findByDesignationTitle(String designationTitle);
}
