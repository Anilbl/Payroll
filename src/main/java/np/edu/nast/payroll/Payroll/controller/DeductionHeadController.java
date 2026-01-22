package np.edu.nast.payroll.Payroll.controller;

import np.edu.nast.payroll.Payroll.entity.DeductionHead;
import np.edu.nast.payroll.Payroll.service.DeductionHeadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/deduction-heads")
@CrossOrigin(origins = "http://localhost:5173")
public class DeductionHeadController {

    @Autowired
    private DeductionHeadService service;

    @PostMapping
    public DeductionHead createDeductionHead(@RequestBody DeductionHead head) {
        return service.saveDeductionHead(head);
    }

    // ADDED: This handles the PUT request from your frontend edit action
    @PutMapping("/{id}")
    public DeductionHead updateDeductionHead(@PathVariable Integer id, @RequestBody DeductionHead head) {
        head.setDeductionHeadId(id); // Ensure the ID from the URL is set into the object
        return service.saveDeductionHead(head);
    }

    @GetMapping
    public List<DeductionHead> getAllDeductionHeads() {
        return service.getAllDeductionHeads();
    }

    @GetMapping("/{id}")
    public DeductionHead getDeductionHead(@PathVariable Integer id) {
        return service.getDeductionHeadById(id);
    }

    @DeleteMapping("/{id}")
    public void deleteDeductionHead(@PathVariable Integer id) {
        service.deleteDeductionHead(id);
    }
}