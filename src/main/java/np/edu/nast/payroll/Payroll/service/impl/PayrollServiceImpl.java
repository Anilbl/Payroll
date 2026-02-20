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
import java.time.temporal.ChronoUnit;
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
    private final EmployeeLeaveRepository employeeLeaveRepo; // Added

    /**
     * DASHBOARD BATCH CALCULATION
     * Updated: Now reflects total payable hours (Attendance + Paid Leave).
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
            // Calculate both physical work and paid leave credit
            double physicalHours = calculateHoursForPeriodInternal(emp.getEmpId(), periodStart, periodEnd);
            double paidLeaveHours = calculatePaidLeaveHoursInternal(emp.getEmpId(), periodStart, periodEnd);
            double combinedHours = physicalHours + paidLeaveHours;

            double standardTotalHours = 28.0 * 8.0;
            double baseSalary = (emp.getBasicSalary() != null && emp.getBasicSalary() > 0)
                    ? emp.getBasicSalary() : getFallbackBasicFromComponents();

            double hourlyRate = baseSalary / standardTotalHours;
            double actualEarned = (combinedHours >= standardTotalHours) ? baseSalary : (combinedHours * hourlyRate);

            return PayrollDashboardDTO.builder()
                    .empId(emp.getEmpId())
                    .fullName(emp.getFirstName() + " " + emp.getLastName())
                    .basicSalary(baseSalary)
                    .earnedSalary(Math.round(actualEarned * 100.0) / 100.0)
                    .totalWorkedHours(Math.round(combinedHours * 100.0) / 100.0)
                    .maritalStatus(emp.getMaritalStatus())
                    .build();
        }).toList();
    }

    /**
     * CORE CALCULATION METHOD
     * Deeply Integrated: Determines actual hours by combining physical attendance and approved leaves.
     */
    @Override
    public Payroll calculatePreview(Map<String, Object> payload) {
        log.info("--- START INTEGRATED PAYROLL CALCULATION ---");

        Integer empId = resolveEmpId(payload);
        Employee employee = employeeRepo.findById(empId)
                .orElseThrow(() -> new RuntimeException("Employee not found for ID: " + empId));

        LocalDate periodStart = LocalDate.now().withDayOfMonth(1);
        LocalDate periodEnd = periodStart.plusMonths(1).withDayOfMonth(1);

        // 1. VALIDATION
        validatePayrollPeriod(empId, periodStart);

        // 2. CALCULATION OF TOTAL HOURS (Attendance + Paid Leave)
        double physicalWorkedHours = calculateHoursForPeriodInternal(empId, periodStart, periodEnd);
        double paidLeaveHours = calculatePaidLeaveHoursInternal(empId, periodStart, periodEnd);
        double totalPayableHours = physicalWorkedHours + paidLeaveHours;

        // 3. BASE SALARY CONFIG
        double standardTotalHours = 28.0 * 8.0;
        List<SalaryComponent> components = salaryComponentRepo.findAll();
        double baseSalaryFromConfig = (employee.getBasicSalary() != null && employee.getBasicSalary() > 0)
                ? employee.getBasicSalary() : getFallbackBasicFromComponents();

        if (baseSalaryFromConfig <= 0) {
            throw new RuntimeException("Error: No Basic Salary defined for Employee ID: " + empId);
        }

        // 4. EARNINGS & OVERTIME
        double hourlyRate = baseSalaryFromConfig / standardTotalHours;
        double overtimeHours = 0.0;
        double overtimePay = 0.0;
        double actualBasicEarned = 0.0;

        if (totalPayableHours > standardTotalHours) {
            overtimeHours = totalPayableHours - standardTotalHours;
            overtimePay = overtimeHours * hourlyRate;
            actualBasicEarned = baseSalaryFromConfig;
        } else {
            actualBasicEarned = totalPayableHours * hourlyRate;
        }

        // 5. ALLOWANCES & DEDUCTIONS
        double dearnessAmt = getComponentDefault(components, "Dearness Allowance", 7380.0);
        double hraPercentage = getComponentDefault(components, "House Rent Allowance", 15.0);
        double ssfPercentage = getComponentDefault(components, "ssf", 11.0);

        double festivalBonus = parseDouble(payload, "festivalBonus");
        double otherBonuses = parseDouble(payload, "bonuses");
        double citContribution = parseDouble(payload, "citContribution");

        double totalAllowances = dearnessAmt + (actualBasicEarned * (hraPercentage / 100.0));
        double ssfContribution = actualBasicEarned * (ssfPercentage / 100.0);

        // 6. TAXATION
        double monthlyGross = actualBasicEarned + totalAllowances + festivalBonus + otherBonuses + overtimePay;
        double taxableMonthly = monthlyGross - (ssfContribution + citContribution);
        double annualTax = calculateNepalTax(taxableMonthly * 12, employee.getMaritalStatus(), ssfContribution > 0);
        double monthlyTax = annualTax / 12;

        // 7. BUILD RESULT
        return Payroll.builder()
                .employee(employee)
                .payGroup(employee.getPayGroup() != null ? employee.getPayGroup() : fetchDefaultPayGroup())
                .basicSalary(round(actualBasicEarned))
                .totalAllowances(round(totalAllowances))
                .festivalBonus(festivalBonus)
                .otherBonuses(otherBonuses)
                .overtimePay(round(overtimePay))
                .ssfContribution(round(ssfContribution))
                .citContribution(citContribution)
                .grossSalary(round(monthlyGross))
                .taxableIncome(round(taxableMonthly))
                .totalTax(round(monthlyTax))
                .totalDeductions(round(ssfContribution + citContribution + monthlyTax))
                .netSalary(round(monthlyGross - (ssfContribution + citContribution + monthlyTax)))
                .payPeriodStart(periodStart)
                .payPeriodEnd(periodEnd)
                .remarks(String.format("Worked: %.2f hrs | Paid Leave: %.2f hrs", physicalWorkedHours, paidLeaveHours))
                .status("PREVIEW")
                .currencyCode("NPR")
                .isVoided(false)
                .build();
    }

    /**
     * Calculates virtual attendance hours from approved leaves.
     * Uses optimized Repository query to ignore historical data.
     */
    private double calculatePaidLeaveHoursInternal(Integer empId, LocalDate start, LocalDate end) {
        LocalDate actualEnd = end.minusDays(1);

        // Only fetch leaves touching this specific month range
        List<EmployeeLeave> leaves = employeeLeaveRepo.findRelevantLeaves(
                empId, "Approved", start, actualEnd);

        double totalPaidLeaveHours = 0.0;

        for (EmployeeLeave leave : leaves) {
            // Only process if Leave Type is marked as PAID in the system
            if (leave.getLeaveType() != null && Boolean.TRUE.equals(leave.getLeaveType().getPaid())) {

                // Calculate overlap to handle leaves spanning across month boundaries
                LocalDate overlapStart = leave.getStartDate().isBefore(start) ? start : leave.getStartDate();
                LocalDate overlapEnd = leave.getEndDate().isAfter(actualEnd) ? actualEnd : leave.getEndDate();

                if (!overlapStart.isAfter(overlapEnd)) {
                    long days = ChronoUnit.DAYS.between(overlapStart, overlapEnd) + 1;
                    totalPaidLeaveHours += (days * 8.0);
                }
            }
        }
        return totalPaidLeaveHours;
    }

    // --- REPOSITORY ACCESS & HELPERS ---

    private double calculateHoursForPeriodInternal(Integer empId, LocalDate start, LocalDate end) {
        return attendanceRepo.findByEmployee_EmpIdAndAttendanceDateGreaterThanEqualAndAttendanceDateLessThan(empId, start, end)
                .stream()
                .filter(a -> a.getCheckInTime() != null && a.getCheckOutTime() != null)
                .mapToDouble(a -> Duration.between(a.getCheckInTime(), a.getCheckOutTime()).toMinutes() / 60.0)
                .sum();
    }

    private Integer resolveEmpId(Map<String, Object> payload) {
        Object empIdObj = payload.get("empId");
        if (empIdObj == null && payload.get("employee") instanceof Map) {
            empIdObj = ((Map<?, ?>) payload.get("employee")).get("empId");
        }
        if (empIdObj == null || empIdObj.toString().equalsIgnoreCase("undefined")) {
            throw new RuntimeException("Validation Error: Employee ID is missing.");
        }
        return Double.valueOf(empIdObj.toString()).intValue();
    }

    private void validatePayrollPeriod(Integer empId, LocalDate start) {
        boolean exists = payrollRepo.findByEmployeeEmpId(empId).stream()
                .anyMatch(p -> !"VOIDED".equals(p.getStatus())
                        && p.getPayPeriodStart().equals(start)
                        && ("PAID".equals(p.getStatus()) || "PROCESSING".equals(p.getStatus())));
        if (exists) throw new RuntimeException("Payroll already processed for this period.");
    }

    private double getComponentDefault(List<SalaryComponent> components, String name, double fallback) {
        return components.stream()
                .filter(c -> c.getComponentName().toLowerCase().contains(name.toLowerCase()))
                .mapToDouble(SalaryComponent::getDefaultValue).findFirst().orElse(fallback);
    }

    private double parseDouble(Map<String, Object> payload, String key) {
        return Double.parseDouble(payload.getOrDefault(key, "0").toString());
    }

    private double round(double val) {
        return Math.round(val * 100.0) / 100.0;
    }

    // --- TRANSACTIONAL METHODS (Unchanged Features) ---

    @Override
    @Transactional
    public Payroll processPayroll(Map<String, Object> payload) {
        Payroll payroll = calculatePreview(payload);
        Employee employee = payroll.getEmployee();

        payrollRepo.findByEmployeeEmpId(employee.getEmpId()).stream()
                .filter(p -> "PENDING_PAYMENT".equals(p.getStatus()) && p.getPayPeriodStart().equals(payroll.getPayPeriodStart()))
                .forEach(p -> payrollRepo.delete(p));

        BankAccount paymentAccount = employee.getPrimaryBankAccount();
        if (paymentAccount == null && !employee.getBankAccount().isEmpty()) paymentAccount = employee.getBankAccount().get(0);
        if (paymentAccount == null) throw new RuntimeException("Bank Account missing for " + employee.getFirstName());

        var auth = SecurityContextHolder.getContext().getAuthentication();
        String principalName = (auth != null) ? auth.getName() : "system";
        User loggedInUser = userRepo.findByEmail(principalName).or(() -> userRepo.findByUsername(principalName)).orElseThrow();

        LocalDate now = LocalDate.now();
        MonthlyInfo summary = monthlyInfoRepo.findByMonthNameAndStatus(now.getMonth().name(), "PROCESSING").stream()
                .filter(m -> m.getPayGroup().getPayGroupId().equals(employee.getPayGroup().getPayGroupId())).findFirst()
                .orElseGet(() -> createNewMonthlyBatch(employee, now, loggedInUser));

        PaymentMethod selectedMethod = paymentMethodRepo.findById(Integer.valueOf(payload.get("paymentMethodId").toString())).orElseThrow();

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
    public void finalizePayroll(Integer id, String ref) {
        Payroll p = payrollRepo.findById(id).orElseThrow();
        if ("PAID".equals(p.getStatus())) return;
        p.setStatus("PAID");
        p.setTransactionRef(ref);
        p.setProcessedAt(LocalDateTime.now());
        updateMonthlyTotals(p.getMonthlyInfo(), p);
        payrollRepo.save(p);
    }

    private double calculateNepalTax(double taxableIncome, String status, boolean isSsfEnrolled) {
        if (taxableIncome <= 0) return 0.0;
        List<TaxSlab> slabs = taxSlabRepo.findByTaxpayerStatusOrderByMinAmountAsc(status);
        double totalTax = 0.0;
        for (TaxSlab slab : slabs) {
            double prevLimit = slab.getPreviousLimit();
            if (taxableIncome > prevLimit) {
                double bucket = Math.min(taxableIncome, slab.getMaxAmount()) - prevLimit;
                if (bucket > 0) {
                    double rate = (slab.getMinAmount() == 0 && isSsfEnrolled) ? 0.0 : (slab.getRatePercentage() / 100.0);
                    totalTax += bucket * rate;
                }
            }
        }
        return round(totalTax);
    }

    private MonthlyInfo createNewMonthlyBatch(Employee emp, LocalDate date, User creator) {
        return monthlyInfoRepo.save(MonthlyInfo.builder().monthName(date.getMonth().name()).monthStart(date.withDayOfMonth(1))
                .monthEnd(date.withDayOfMonth(date.lengthOfMonth())).payGroup(emp.getPayGroup()).totalEmployeesProcessed(0)
                .totalGrossSalary(0.0).totalAllowances(0.0).totalDeductions(0.0).totalTax(0.0).totalNetSalary(0.0)
                .currency("NPR").status("PROCESSING").generatedBy(creator).generatedAt(LocalDateTime.now()).build());
    }

    private void updateMonthlyTotals(MonthlyInfo summary, Payroll p) {
        summary.setTotalEmployeesProcessed((summary.getTotalEmployeesProcessed() == null ? 0 : summary.getTotalEmployeesProcessed()) + 1);
        summary.setTotalGrossSalary((summary.getTotalGrossSalary() == null ? 0.0 : summary.getTotalGrossSalary()) + p.getGrossSalary());
        summary.setTotalNetSalary((summary.getTotalNetSalary() == null ? 0.0 : summary.getTotalNetSalary()) + p.getNetSalary());
        monthlyInfoRepo.save(summary);
    }

    @Override public List<Payroll> getAllPayrolls() { return payrollRepo.findAll(); }
    @Override public List<Payroll> getPayrollByEmployeeId(Integer id) { return payrollRepo.findByEmployeeEmpId(id); }
    @Override public Payroll updateStatus(Integer id, String status) { Payroll p = getPayrollById(id); p.setStatus(status); if ("VOIDED".equals(status)) p.setIsVoided(true); return payrollRepo.save(p); }
    @Override public Payroll voidPayroll(Integer id) { return updateStatus(id, "VOIDED"); }
    @Override public Payroll getPayrollById(Integer id) { return payrollRepo.findById(id).orElseThrow(); }
    @Override @Transactional public void rollbackPayroll(Integer id) { payrollRepo.deleteById(id); }
    private double getFallbackBasicFromComponents() { return salaryComponentRepo.findAll().stream().filter(c -> c.getComponentName().equalsIgnoreCase("Basic Salary")).mapToDouble(SalaryComponent::getDefaultValue).findFirst().orElse(0.0); }
    private PayGroup fetchDefaultPayGroup() { return payGroupRepo.findById(4).orElseThrow(); }
}