package np.edu.nast.payroll.Payroll.service.impl;

import lombok.RequiredArgsConstructor;
import np.edu.nast.payroll.Payroll.dto.auth.PayrollRequest;
import lombok.extern.slf4j.Slf4j;
import np.edu.nast.payroll.Payroll.entity.*;
import np.edu.nast.payroll.Payroll.repository.*;
import np.edu.nast.payroll.Payroll.service.PayrollService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityNotFoundException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PayrollServiceImpl implements PayrollService {

    private final PayrollRepository payrollRepo;
    private final EmployeeRepository employeeRepo;
    private final UserRepository userRepo;
    private final BankAccountRepository bankAccountRepo;
    private final PaymentMethodRepository paymentMethodRepo;
    private final PayGroupRepository payGroupRepo; // Required to fix your error

    @Override
    public List<Payroll> getAllPayrolls() {
        // FIXED: Uses grouping logic to show only the most recent payroll per employee
        return payrollRepo.findLatestPayrollForEachEmployee();
    }

    @Override
    public List<Payroll> getEmployeeHistory(Integer empId) {
        return payrollRepo.findByEmployeeEmpIdOrderByPayDateDesc(empId);
        return payrollRepo.findAll();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Payroll processPayroll(Map<String, Object> payload) {
        log.info("Processing payroll for payload: {}", payload);
        try {
            // 1. Extract IDs from the Map (Matches your React keys: empId, accountId, payGroupId)
            Integer empId = Integer.valueOf(payload.get("empId").toString());
            Integer accountId = Integer.valueOf(payload.get("accountId").toString());
            Integer methodId = Integer.valueOf(payload.get("paymentMethodId").toString());
            Integer payGroupId = Integer.valueOf(payload.get("payGroupId").toString());

            // 2. Fetch Entities to satisfy database NOT NULL constraints
            Employee employee = employeeRepo.findById(empId)
                    .orElseThrow(() -> new RuntimeException("Employee not found"));

            BankAccount account = bankAccountRepo.findById(accountId)
                    .orElseThrow(() -> new RuntimeException("Bank Account not found"));

            PaymentMethod method = paymentMethodRepo.findById(methodId)
                    .orElseThrow(() -> new RuntimeException("Payment Method not found"));

            // FIX: This fetches the PayGroup that was causing the null error
            PayGroup payGroup = payGroupRepo.findById(payGroupId)
                    .orElseThrow(() -> new RuntimeException("Pay Group not found"));

            // Get first available user as admin for 'processedBy'
            User admin = userRepo.findAll().stream().findFirst()
                    .orElseThrow(() -> new RuntimeException("No Admin user found"));

            // 3. Set values on the Entity
            Payroll payroll = new Payroll();
            payroll.setEmployee(employee);
            payroll.setProcessedBy(admin);
            payroll.setPaymentAccount(account);
            payroll.setPaymentMethod(method);
            payroll.setPayGroup(payGroup); // CRITICAL: This satisfies the DB constraint

            payroll.setPayDate(LocalDate.now());
            payroll.setGrossSalary(Double.parseDouble(payload.get("grossSalary").toString()));
            payroll.setTotalAllowances(Double.parseDouble(payload.get("totalAllowances").toString()));
            payroll.setTotalDeductions(Double.parseDouble(payload.get("totalDeductions").toString()));
            payroll.setStatus("PROCESSED");

            // 4. Calculations (Nepal Labor Act 1% Tax Compliance)
            double grossPlusAllowances = payroll.getGrossSalary() + payroll.getTotalAllowances();
            double tax = grossPlusAllowances * 0.01;
            payroll.setTotalTax(tax);
            payroll.setNetSalary(grossPlusAllowances - (payroll.getTotalDeductions() + tax));

            // 5. Force save and flush
            return payrollRepo.saveAndFlush(payroll);

        } catch (Exception e) {
            log.error("Save failed: {}", e.getMessage());
            throw new RuntimeException("Error saving payroll: " + e.getMessage());
        }
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public Payroll voidPayroll(Integer id, String remarks) {
        Payroll payroll = payrollRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Payroll record not found ID: " + id));
        payroll.setVoided(true);
        payroll.setStatus("VOIDED");
        payroll.setRemarks(remarks);
        return payrollRepo.save(payroll);
    }

    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    public Payroll processPayrollRequest(PayrollRequest request) {
        Employee employee = employeeRepo.findById(request.getEmpId())
                .orElseThrow(() -> new RuntimeException("Employee not found ID: " + request.getEmpId()));

        List<EmployeeSalaryComponent> components = escRepo.findByEmployeeEmpIdAndIsActiveTrue(request.getEmpId());

        double basicSalary = 0.0;
        double totalAllowances = 0.0;
        double totalDeductions = 0.0;

        // Handle Manual Bonus (Festive Bonus)
        double festiveBonus = (request.getManualBonus() != null) ? request.getManualBonus() : 0.0;

        for (EmployeeSalaryComponent esc : components) {
            if (esc.getSalaryComponent().getComponentName().equalsIgnoreCase("Basic Salary")) {
                basicSalary = esc.getValue();
            }
        }

        for (EmployeeSalaryComponent esc : components) {
            SalaryComponent sc = esc.getSalaryComponent();
            double value = esc.getValue();
            double calculatedAmt = "percentage_of_basic".equalsIgnoreCase(sc.getCalculationMethod())
                    ? (value / 100) * basicSalary : value;

            if ("allowance".equalsIgnoreCase(sc.getComponentType().getName())) {
                totalAllowances += calculatedAmt;
            } else if ("deduction".equalsIgnoreCase(sc.getComponentType().getName())) {
                totalDeductions += calculatedAmt;
            }
        }

        double finalGross = basicSalary + totalAllowances + festiveBonus;
        double taxableIncomeMonthly = finalGross - totalDeductions;
        double monthlyTax = (taxableIncomeMonthly * 0.01);

        String refNo = "PAY-" + LocalDate.now().getYear() + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        Payroll payroll = Payroll.builder()
                .employee(employee)
                .payslipRef(refNo)
                .grossSalary(finalGross)
                .totalAllowances(totalAllowances + festiveBonus)
                .totalDeductions(totalDeductions)
                .totalTax(monthlyTax)
                .netSalary(finalGross - (totalDeductions + monthlyTax))
                .status("PAID")
                .currencyCode("NPR")
                .isVoided(false)
                .remarks(festiveBonus > 0 ? "FESTIVE BONUS: Rs. " + festiveBonus : "Regular")
                .payPeriodStart(LocalDate.now().withDayOfMonth(1))
                .payPeriodEnd(LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth()))
                .payDate(LocalDate.now())
                .processedAt(LocalDateTime.now())
                .build();

        // Standard Default IDs for NAST System
        payroll.setPaymentAccount(bankAccountRepo.findById(11).orElseThrow());
        payroll.setPaymentMethod(paymentMethodRepo.findById(1).orElseThrow());
        payroll.setProcessedBy(userRepo.findById(4).orElseThrow());

        return payrollRepo.save(payroll);
    }

    @Override public Payroll updateStatus(Integer id, String s) {
    public Payroll updateStatus(Integer id, String status) {
        Payroll p = payrollRepo.findById(id).orElseThrow();
        p.setStatus(status);
        p.setStatus(s);
        return payrollRepo.save(p);
    }
    @Override public Payroll savePayroll(Payroll p) { return payrollRepo.save(p); }
    @Override public Payroll getPayrollById(Integer id) { return payrollRepo.findById(id).orElse(null); }
    @Override public void deletePayroll(Integer id) { payrollRepo.deleteById(id); }
}