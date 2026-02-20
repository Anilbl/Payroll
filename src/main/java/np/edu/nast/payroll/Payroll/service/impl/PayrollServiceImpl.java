package np.edu.nast.payroll.Payroll.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import np.edu.nast.payroll.Payroll.dto.auth.PayrollDashboardDTO;
import np.edu.nast.payroll.Payroll.entity.*;
import np.edu.nast.payroll.Payroll.repository.*;
import np.edu.nast.payroll.Payroll.service.PayrollService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

@Slf4j
@Service
@RequiredArgsConstructor
public class PayrollServiceImpl implements PayrollService {

    private final PayrollRepository payrollRepo;
    private final EmployeeRepository employeeRepo;
    private final SalaryComponentRepository salaryComponentRepo;
    private final TaxSlabRepository taxSlabRepo;
    private final MonthlyInfoRepository monthlyInfoRepo;
    private final UserRepository userRepo;
    private final PayGroupRepository payGroupRepo;
    private final PaymentMethodRepository paymentMethodRepo;
    private final AttendanceRepository attendanceRepo;

    /**
     * DASHBOARD BATCH CALCULATION
     * Merged: Handles Month Names/Numbers and calculates earned salary.
     */
    @Override
    public List<PayrollDashboardDTO> getBatchCalculation(String month, int year) {
        List<Employee> employees = employeeRepo.findAll();
        int monthValue;
        try {
            if (month.matches("\\d+")) {
                monthValue = Integer.parseInt(month);
            } else {
                monthValue = java.time.Month.valueOf(month.toUpperCase()).getValue();
            }
        } catch (Exception e) {
            log.error("Invalid month provided: {}", month);
            return new ArrayList<>();
        }

        LocalDate periodStart = LocalDate.of(year, monthValue, 1);
        LocalDate periodEnd = periodStart.plusMonths(1);

        return employees.stream().map(emp -> {
            double workedHours = calculateHoursForPeriodInternal(emp.getEmpId(), periodStart, periodEnd);
            double standardTotalHours = 28.0 * 8.0;
            double baseSalary = (emp.getBasicSalary() != null && emp.getBasicSalary() > 0)
                    ? emp.getBasicSalary() : getFallbackBasicFromComponents();

            double hourlyRate = baseSalary / standardTotalHours;
            double actualEarned = (workedHours >= standardTotalHours) ? baseSalary : (workedHours * hourlyRate);

            return PayrollDashboardDTO.builder()
                    .empId(emp.getEmpId())
                    .fullName(emp.getFirstName() + " " + emp.getLastName())
                    .basicSalary(baseSalary)
                    .earnedSalary(Math.round(actualEarned * 100.0) / 100.0)
                    .totalWorkedHours(Math.round(workedHours * 100.0) / 100.0)
                    .maritalStatus(emp.getMaritalStatus())
                    .build();
        }).toList();
    }

    @Override
    public List<Payroll> getAllPayrolls() {
        return payrollRepo.findAll();
    }

    @Override
    public Payroll calculatePreview(Map<String, Object> payload) {
        log.info("--- START ATTENDANCE-BASED PAYROLL CALCULATION ---");

        Object empIdObj = payload.get("empId");
        if (empIdObj == null && payload.get("employee") instanceof Map) {
            Map<?, ?> empMap = (Map<?, ?>) payload.get("employee");
            empIdObj = empMap.get("empId");
        }

        if (empIdObj == null || empIdObj.toString().equalsIgnoreCase("undefined")) {
            throw new RuntimeException("Validation Error: Employee ID is missing.");
        }

        Integer empId = Double.valueOf(empIdObj.toString()).intValue();
        Employee employee = employeeRepo.findById(empId)
                .orElseThrow(() -> new RuntimeException("Employee not found for ID: " + empId));

        LocalDate periodStart = LocalDate.now().withDayOfMonth(1);
        LocalDate periodEnd = periodStart.plusMonths(1).withDayOfMonth(1);

        // CHECK FOR EXISTING PAID/PROCESSING RECORDS
        boolean alreadyExists = payrollRepo.findByEmployeeEmpId(empId).stream()
                .anyMatch(p -> !"VOIDED".equals(p.getStatus())
                        && p.getPayPeriodStart().equals(periodStart)
                        && ("PAID".equals(p.getStatus()) || "PROCESSING".equals(p.getStatus())));

        if (alreadyExists) {
            throw new RuntimeException("Payroll already processed for this period.");
        }

        List<Attendance> attendanceList = attendanceRepo
                .findByEmployee_EmpIdAndAttendanceDateGreaterThanEqualAndAttendanceDateLessThan(empId, periodStart, periodEnd);

        double totalWorkedHours = 0.0;
        for (Attendance attendance : attendanceList) {
            if (attendance.getCheckInTime() != null && attendance.getCheckOutTime() != null) {
                Duration duration = Duration.between(attendance.getCheckInTime(), attendance.getCheckOutTime());
                totalWorkedHours += duration.toMinutes() / 60.0;
            }
        }

        double standardTotalHours = 28.0 * 8.0;
        List<SalaryComponent> components = salaryComponentRepo.findAll();
        double baseSalaryFromConfig = (employee.getBasicSalary() != null && employee.getBasicSalary() > 0)
                ? employee.getBasicSalary() : getFallbackBasicFromComponents();

        if (baseSalaryFromConfig <= 0) {
            throw new RuntimeException("Error: No Basic Salary defined for Employee ID: " + empId);
        }

        double hourlyRate = baseSalaryFromConfig / standardTotalHours;
        double overtimeHours = 0.0;
        double overtimePay = 0.0;
        double actualBasicEarned = 0.0;

        if (totalWorkedHours > standardTotalHours) {
            overtimeHours = totalWorkedHours - standardTotalHours;
            overtimePay = overtimeHours * hourlyRate;
            actualBasicEarned = baseSalaryFromConfig;
        } else {
            actualBasicEarned = totalWorkedHours * hourlyRate;
        }

        double dearnessAmt = components.stream()
                .filter(c -> c.getComponentName().equalsIgnoreCase("Dearness Allowance"))
                .mapToDouble(SalaryComponent::getDefaultValue).findFirst().orElse(7380.0);

        double hraPercentage = components.stream()
                .filter(c -> c.getComponentName().equalsIgnoreCase("House Rent Allowance"))
                .mapToDouble(SalaryComponent::getDefaultValue).findFirst().orElse(15.0);

        double ssfPercentage = components.stream()
                .filter(c -> c.getComponentName().toLowerCase().contains("ssf"))
                .mapToDouble(SalaryComponent::getDefaultValue).findFirst().orElse(11.0);

        double festivalBonus = Double.parseDouble(payload.getOrDefault("festivalBonus", "0").toString());
        double otherBonuses = Double.parseDouble(payload.getOrDefault("bonuses", "0").toString());
        double citContribution = Double.parseDouble(payload.getOrDefault("citContribution", "0").toString());

        double totalAllowances = dearnessAmt + (actualBasicEarned * (hraPercentage / 100.0));
        double ssfContribution = actualBasicEarned * (ssfPercentage / 100.0);

        double monthlyGross = actualBasicEarned + totalAllowances + festivalBonus + otherBonuses + overtimePay;
        double taxableMonthly = monthlyGross - (ssfContribution + citContribution);

        double annualTax = calculateNepalTax(taxableMonthly * 12, employee.getMaritalStatus(), ssfContribution > 0);
        double monthlyTax = annualTax / 12;

        return Payroll.builder()
                .employee(employee)
                .payGroup(employee.getPayGroup() != null ? employee.getPayGroup() : fetchDefaultPayGroup())
                .basicSalary(actualBasicEarned)
                .totalAllowances(totalAllowances)
                .festivalBonus(festivalBonus)
                .otherBonuses(otherBonuses)
                .overtimePay(overtimePay)
                .ssfContribution(ssfContribution)
                .citContribution(citContribution)
                .grossSalary(monthlyGross)
                .taxableIncome(taxableMonthly)
                .totalTax(monthlyTax)
                .totalDeductions(ssfContribution + citContribution + monthlyTax)
                .netSalary(monthlyGross - (ssfContribution + citContribution + monthlyTax))
                .payPeriodStart(periodStart)
                .payPeriodEnd(periodEnd)
                .remarks("Worked: " + String.format("%.2f", totalWorkedHours) + " hrs.")
                .status("PREVIEW")
                .currencyCode("NPR")
                .isVoided(false)
                .build();
    }

    @Override
    @Transactional
    public Payroll processPayroll(Map<String, Object> payload) {
        Payroll payroll = calculatePreview(payload);
        Employee employee = payroll.getEmployee();

        // 1. CLEANUP PREVIOUS PENDING ATTEMPTS (From version 1)
        payrollRepo.findByEmployeeEmpId(employee.getEmpId()).stream()
                .filter(p -> "PENDING_PAYMENT".equals(p.getStatus())
                        && p.getPayPeriodStart().equals(payroll.getPayPeriodStart()))
                .forEach(p -> payrollRepo.delete(p));

        // 2. RESOLVE BANK ACCOUNT (The Fix)
        BankAccount paymentAccount = employee.getPrimaryBankAccount();
        if (paymentAccount == null && !employee.getBankAccount().isEmpty()) {
            paymentAccount = employee.getBankAccount().get(0);
        }
        if (paymentAccount == null) {
            throw new RuntimeException("Bank Account or eSewa ID missing for " + employee.getFirstName());
        }

        // 3. AUTH & USER
        var auth = SecurityContextHolder.getContext().getAuthentication();
        String principalName = (auth != null) ? auth.getName() : "system";
        User loggedInUser = userRepo.findByEmail(principalName)
                .or(() -> userRepo.findByUsername(principalName))
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 4. BATCH INFO
        LocalDate now = LocalDate.now();
        MonthlyInfo summary = monthlyInfoRepo.findByMonthNameAndStatus(now.getMonth().name(), "PROCESSING")
                .stream()
                .filter(m -> m.getPayGroup().getPayGroupId().equals(employee.getPayGroup().getPayGroupId()))
                .findFirst()
                .orElseGet(() -> createNewMonthlyBatch(employee, now, loggedInUser));

        Integer methodId = Integer.valueOf(payload.get("paymentMethodId").toString());
        PaymentMethod selectedMethod = paymentMethodRepo.findById(methodId)
                .orElseThrow(() -> new RuntimeException("Payment Method not found."));

        // 5. SET FINAL FIELDS
        payroll.setMonthlyInfo(summary);
        payroll.setStatus("PENDING_PAYMENT");
        payroll.setProcessedBy(loggedInUser);
        payroll.setPaymentAccount(paymentAccount);
        payroll.setPaymentMethod(selectedMethod);
        payroll.setPayDate(now);

        return payrollRepo.save(payroll);
    }

    @Override
    @Transactional
    public void finalizePayroll(Integer payrollId, String transactionRef) {
        Payroll payroll = payrollRepo.findById(payrollId).orElseThrow();
        if ("PAID".equals(payroll.getStatus())) return;

        payroll.setStatus("PAID");
        payroll.setTransactionRef(transactionRef);
        payroll.setProcessedAt(LocalDateTime.now());
        updateMonthlyTotals(payroll.getMonthlyInfo(), payroll);
        payrollRepo.save(payroll);
    }

    @Override @Transactional public void rollbackPayroll(Integer id) { payrollRepo.deleteById(id); }

    private double calculateNepalTax(double taxableIncome, String status, boolean isSsfEnrolled) {
        if (taxableIncome <= 0) return 0.0;
        List<TaxSlab> slabs = taxSlabRepo.findByTaxpayerStatusOrderByMinAmountAsc(status);
        double totalTax = 0.0;
        for (TaxSlab slab : slabs) {
            double previousSlabEnd = slab.getPreviousLimit();
            if (taxableIncome > previousSlabEnd) {
                double amountInThisBucket = Math.min(taxableIncome, slab.getMaxAmount()) - previousSlabEnd;
                if (amountInThisBucket > 0) {
                    double rate = (slab.getMinAmount() == 0 && isSsfEnrolled) ? 0.0 : (slab.getRatePercentage() / 100.0);
                    totalTax += amountInThisBucket * rate;
                }
            }
        }
        return Math.round(totalTax * 100.0) / 100.0;
    }

    private MonthlyInfo createNewMonthlyBatch(Employee emp, LocalDate date, User creator) {
        // Correctly initializing totals to 0.0 to prevent NullPointer during updates
        return monthlyInfoRepo.save(MonthlyInfo.builder()
                .monthName(date.getMonth().name())
                .monthStart(date.withDayOfMonth(1))
                .monthEnd(date.withDayOfMonth(date.lengthOfMonth()))
                .payGroup(emp.getPayGroup())
                .totalEmployeesProcessed(0)
                .totalGrossSalary(0.0)
                .totalAllowances(0.0)
                .totalDeductions(0.0)
                .totalTax(0.0)
                .totalNetSalary(0.0)
                .currency("NPR")
                .status("PROCESSING")
                .generatedBy(creator)
                .generatedAt(LocalDateTime.now())
                .build());
    }

    private void updateMonthlyTotals(MonthlyInfo summary, Payroll p) {
        summary.setTotalEmployeesProcessed((summary.getTotalEmployeesProcessed() == null ? 0 : summary.getTotalEmployeesProcessed()) + 1);
        summary.setTotalGrossSalary((summary.getTotalGrossSalary() == null ? 0.0 : summary.getTotalGrossSalary()) + p.getGrossSalary());
        summary.setTotalNetSalary((summary.getTotalNetSalary() == null ? 0.0 : summary.getTotalNetSalary()) + p.getNetSalary());
        monthlyInfoRepo.save(summary);
    }

    @Override public List<Payroll> getPayrollByEmployeeId(Integer empId) { return payrollRepo.findByEmployeeEmpId(empId); }
    @Override public Payroll updateStatus(Integer id, String status) {
        Payroll p = getPayrollById(id);
        p.setStatus(status);
        if ("VOIDED".equals(status)) p.setIsVoided(true);
        return payrollRepo.save(p);
    }
    @Override public Payroll voidPayroll(Integer id) { return updateStatus(id, "VOIDED"); }
    @Override public Payroll getPayrollById(Integer id) { return payrollRepo.findById(id).orElseThrow(); }

    private double getFallbackBasicFromComponents() {
        return salaryComponentRepo.findAll().stream()
                .filter(c -> c.getComponentName().equalsIgnoreCase("Basic Salary"))
                .mapToDouble(SalaryComponent::getDefaultValue).findFirst().orElse(0.0);
    }

    private double calculateHoursForPeriodInternal(Integer empId, LocalDate start, LocalDate end) {
        return attendanceRepo.findByEmployee_EmpIdAndAttendanceDateGreaterThanEqualAndAttendanceDateLessThan(empId, start, end)
                .stream()
                .filter(a -> a.getCheckInTime() != null && a.getCheckOutTime() != null)
                .mapToDouble(a -> Duration.between(a.getCheckInTime(), a.getCheckOutTime()).toMinutes() / 60.0)
                .sum();
    }

    private PayGroup fetchDefaultPayGroup() {
        return payGroupRepo.findById(4).orElseThrow(() -> new RuntimeException("Default PayGroup 4 missing."));
    }
}