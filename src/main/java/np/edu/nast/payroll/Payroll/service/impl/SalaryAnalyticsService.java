package np.edu.nast.payroll.Payroll.service.impl;

import np.edu.nast.payroll.Payroll.entity.BankAccount;
import np.edu.nast.payroll.Payroll.entity.Employee;
import np.edu.nast.payroll.Payroll.entity.Payroll;
import np.edu.nast.payroll.Payroll.repository.BankAccountRepository;
import np.edu.nast.payroll.Payroll.repository.EmployeeRepository;
import np.edu.nast.payroll.Payroll.repository.PayrollRepository;
import np.edu.nast.payroll.Payroll.salaryDTO.SalaryAnalyticsResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SalaryAnalyticsService {
    @Autowired private EmployeeRepository employeeRepo;
    @Autowired private PayrollRepository payrollRepo;
    @Autowired private BankAccountRepository bankAccountRepo;

    public SalaryAnalyticsResponseDTO getSalaryDetailsByUsername(String loginName, String monthStr) {
        // 1. Find Employee by the actual login username (NOT email)
        // This must match the 'username' field in your User entity
        Employee emp = employeeRepo.findByUser_Username(loginName)
                .orElseThrow(() -> new RuntimeException("Employee profile not found for user: " + loginName));

        // 2. Parse "2026-01"
        String[] parts = monthStr.split("-");
        int year = Integer.parseInt(parts[0]);
        int month = Integer.parseInt(parts[1]);

        // 3. Find the Payroll (The Join)
        Payroll payroll = payrollRepo.findByEmployeeEmpIdAndMonth(emp.getEmpId(), year, month)
                .orElseThrow(() -> new RuntimeException("No payroll record found for " + monthStr));

        // 4. Find the Bank Details (The Join)
        BankAccount bankDetails = bankAccountRepo.findByEmployeeEmpIdAndIsPrimaryTrue(emp.getEmpId())
                .orElse(null);



        // 5. Perform Exact Calculations
        Double base = emp.getBasicSalary();             // 55000.0
        Double gross = payroll.getGrossSalary();        // 55000.0
        Double allowances = payroll.getTotalAllowances(); // 5000.0
        Double deductions = payroll.getTotalDeductions(); // 6050.0
        Double taxDeducted = payroll.getTotalTax();      // 2500.0

        // The calculation that results in 51,450
        Double netSalary = (gross + allowances) - (deductions + taxDeducted);
        // 6. Map to DTO (Matches image_36451f.jpg)
        return mapToDTO(emp, payroll, bankDetails);
    }

    private SalaryAnalyticsResponseDTO mapToDTO(Employee emp, Payroll payroll, BankAccount bank) {
        SalaryAnalyticsResponseDTO dto = new SalaryAnalyticsResponseDTO();
        dto.setEmployeeName(emp.getFirstName() + " " + emp.getLastName());
        dto.setDesignation(emp.getPosition() != null ? emp.getPosition().getDesignationTitle() : "N/A");
        dto.setEmploymentStatus(emp.getEmploymentStatus());

        if (bank != null && bank.getBank() != null) {
            dto.setBankName(bank.getBank().getBankName());
            dto.setBankAccount(bank.getAccountNumber());
        } else {
            dto.setBankName("N/A");
            dto.setBankAccount("N/A");
        }

        dto.setBaseSalary(emp.getBasicSalary());
        dto.setGrossSalary(payroll.getGrossSalary());
        dto.setTotalAllowances(payroll.getTotalAllowances());
        dto.setTotalDeductions(payroll.getTotalDeductions());
        dto.setTaxableAmount(payroll.getTotalTax());
        dto.setNetSalary(payroll.getNetSalary());
        return dto;
    }
}
