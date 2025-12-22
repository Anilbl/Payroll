package np.edu.nast.payroll.Payroll.service.impl;

import np.edu.nast.payroll.Payroll.entity.Designation;
import np.edu.nast.payroll.Payroll.repository.DesignationRepository;
import np.edu.nast.payroll.Payroll.service.DesignationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DesignationServiceImpl implements DesignationService {

    @Autowired
    private DesignationRepository designationRepository;

    @Override
    public Designation saveDesignation(Designation designation) {
        // Save to database, ID will be auto-generated
        return designationRepository.save(designation);
    }

    @Override
    public Designation updateDesignation(Designation designation) {
        // Save also works for update if ID exists
        return designationRepository.save(designation);
    }

    @Override
    public void deleteDesignation(Integer designationId) {
        designationRepository.deleteById(designationId);
    }

    @Override
    public Designation getDesignationById(Integer designationId) {
        return designationRepository.findById(designationId).orElse(null);
    }

    @Override
    public List<Designation> getAllDesignations() {
        return designationRepository.findAll();
    }
}
