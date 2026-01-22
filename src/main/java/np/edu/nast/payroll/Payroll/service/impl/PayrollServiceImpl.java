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
    private final PayGroupRepository payGroupRepo;
    // Assuming escRepo is needed for the processPayrollRequest logic
    private final EmployeeSalaryComponentRepository escRepo;

    @Override
    public List<Payroll> getAllPayrolls() {
        return payrollRepo.findLatestPayrollForEachEmployee();
    }

    @Override
    public List<Payroll> getEmployeeHistory(Integer empId) {
        return payrollRepo.findByEmployeeEmpIdOrderByPayDateDesc(empId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Payroll processPayroll(Map<String, Object> payload) {
        log.info("Processing payroll for payload: {}", payload);
        try {
            Integer empId = Integer.valueOf(payload.get("empId").toString());
            Integer accountId = Integer.valueOf(payload.get("accountId").toString());
            Integer methodId = Integer.valueOf(payload.get("paymentMethodId").toString());
            Integer payGroupId = Integer.valueOf(payload.get("payGroupId").toString());

            Employee employee = employeeRepo.findById(empId)
                    .orElseThrow(() -> new RuntimeException("Employee not found"));

            BankAccount account = bankAccountRepo.findById(accountId)
                    .orElseThrow(() -> new RuntimeException("Bank Account not found"));

            PaymentMethod method = paymentMethodRepo.findById(methodId)
                    .orElseThrow(() -> new RuntimeException("Payment Method not found"));

            PayGroup payGroup = payGroupRepo.findById(payGroupId)
                    .orElseThrow(() -> new RuntimeException("Pay Group not found"));

            User admin = userRepo.findAll().stream().findFirst()
                    .orElseThrow(() -> new RuntimeException("No Admin user found"));

            Payroll payroll = new Payroll();
            payroll.setEmployee(employee);
            payroll.setProcessedBy(admin);
            payroll.setPaymentAccount(account);
            payroll.setPaymentMethod(method);
            payroll.setPayGroup(payGroup);

            payroll.setPayDate(LocalDate.now());
            payroll.setGrossSalary(Double.parseDouble(payload.get("grossSalary").toString()));
            payroll.setTotalAllowances(Double.parseDouble(payload.get("totalAllowances").toString()));
            payroll.setTotalDeductions(Double.parseDouble(payload.get("totalDeductions").toString()));
            payroll.setStatus("PROCESSED");

            double grossPlusAllowances = payroll.getGrossSalary() + payroll.getTotalAllowances();
            double tax = grossPlusAllowances * 0.01;
            payroll.setTotalTax(tax);
            payroll.setNetSalary(grossPlusAllowances - (payroll.getTotalDeductions() + tax));

            return payrollRepo.saveAndFlush(payroll);

        } catch (Exception e) {
            log.error("Save failed: {}", e.getMessage());
            throw new RuntimeException("Error saving payroll: " + e.getMessage());
        }
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

        payroll.setPaymentAccount(bankAccountRepo.findById(11).orElseThrow());
        payroll.setPaymentMethod(paymentMethodRepo.findById(1).orElseThrow());
        payroll.setProcessedBy(userRepo.findById(4).orElseThrow());

        return payrollRepo.save(payroll);
    }

    @Override
    public Payroll updateStatus(Integer id, String status) {
        Payroll p = payrollRepo.findById(id).orElseThrow();
        p.setStatus(status);
        return payrollRepo.save(p);
    }

    @Override public Payroll savePayroll(Payroll p) { return payrollRepo.save(p); }
    @Override public Payroll getPayrollById(Integer id) { return payrollRepo.findById(id).orElse(null); }
    @Override public void deletePayroll(Integer id) { payrollRepo.deleteById(id); }
}