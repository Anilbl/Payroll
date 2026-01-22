package np.edu.nast.payroll.Payroll.repository;

import np.edu.nast.payroll.Payroll.entity.DeductionHead;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DeductionHeadRepository extends JpaRepository<DeductionHead, Integer> {

    // This allows the service to check if a name already exists before saving
    Optional<DeductionHead> findByName(String name);
}