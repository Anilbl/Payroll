package np.edu.nast.payroll.Payroll.service.impl;

import np.edu.nast.payroll.Payroll.entity.*;
import np.edu.nast.payroll.Payroll.repository.*;
import np.edu.nast.payroll.Payroll.service.PayrollService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PayrollServiceImpl implements PayrollService {

    @Autowired
    private PayrollRepository payrollRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PayGroupRepository payGroupRepository;

    @Autowired
    private PaymentMethodRepository paymentMethodRepository;

    @Autowired
    private BankAccountRepository bankAccountRepository;

    @Override
    public Payroll savePayroll(Payroll payroll) {

        /* =========================
           STRICT NULL CHECKS
           ========================= */

        if (payroll.getEmployee() == null || payroll.getEmployee().getEmpId() == null)
            throw new RuntimeException("Employee ID is required");

        if (payroll.getProcessedBy() == null || payroll.getProcessedBy().getUserId() == null)
            throw new RuntimeException("ProcessedBy User ID is required");

        if (payroll.getPaymentAccount() == null || payroll.getPaymentAccount().getAccountId() == null)
            throw new RuntimeException("Payment Bank Account ID is required");

        /* =========================
           LOAD MANAGED ENTITIES
           ========================= */

        // Employee (REQUIRED)
        Employee emp = employeeRepository.findById(
                payroll.getEmployee().getEmpId()
        ).orElseThrow(() -> new RuntimeException("Employee not found"));
        payroll.setEmployee(emp);

        // User who processed payroll (REQUIRED)
        User user = userRepository.findById(
                payroll.getProcessedBy().getUserId()
        ).orElseThrow(() -> new RuntimeException("User not found"));
        payroll.setProcessedBy(user);

        // Bank account for salary payment (REQUIRED)
        BankAccount account = bankAccountRepository.findById(
                payroll.getPaymentAccount().getAccountId()
        ).orElseThrow(() -> new RuntimeException("BankAccount not found"));
        payroll.setPaymentAccount(account);

        // Pay group (OPTIONAL)
        if (payroll.getPayGroup() != null && payroll.getPayGroup().getPayGroupId() != null) {
            PayGroup pg = payGroupRepository.findById(
                    payroll.getPayGroup().getPayGroupId()
            ).orElseThrow(() -> new RuntimeException("PayGroup not found"));
            payroll.setPayGroup(pg);
        }

        // Payment method (OPTIONAL)
        if (payroll.getPaymentMethod() != null && payroll.getPaymentMethod().getPaymentMethodId() != null) {
            PaymentMethod pm = paymentMethodRepository.findById(
                    payroll.getPaymentMethod().getPaymentMethodId()
            ).orElseThrow(() -> new RuntimeException("PaymentMethod not found"));
            payroll.setPaymentMethod(pm);
        }

        /* =========================
           SYSTEM-CONTROLLED FIELDS
           ========================= */

        // Ensure processed time is always set by backend
        payroll.setProcessedAt(LocalDateTime.now());

        // Default status if client forgets
        if (payroll.getStatus() == null) {
            payroll.setStatus("PROCESSED");
        }

        /* =========================
           SAVE PAYROLL
           ========================= */
        return payrollRepository.save(payroll);
    }

    @Override
    public List<Payroll> getAllPayrolls() {
        return payrollRepository.findAll();
    }

    @Override
    public Payroll getPayrollById(Integer id) {
        return payrollRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payroll not found"));
    }

    @Override
    public void deletePayroll(Integer id) {
        payrollRepository.deleteById(id);
    }
}
