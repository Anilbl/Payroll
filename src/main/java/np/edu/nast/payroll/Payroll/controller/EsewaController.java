package np.edu.nast.payroll.Payroll.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import np.edu.nast.payroll.Payroll.config.EsewaConfig;
import np.edu.nast.payroll.Payroll.entity.PayoutInfo;
import np.edu.nast.payroll.Payroll.entity.Payroll;
import np.edu.nast.payroll.Payroll.repository.PayoutInfoRepository;
import np.edu.nast.payroll.Payroll.service.EmailService;
import np.edu.nast.payroll.Payroll.service.impl.PayrollServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Map;

@Controller
@RequestMapping("/api/esewa")
@CrossOrigin(origins = "http://localhost:5173")
@Slf4j
public class EsewaController {

    @Autowired
    private PayrollServiceImpl payrollService;

    @Autowired
    private PayoutInfoRepository payoutInfoRepository;

    @Autowired
    private EmailService emailService;

    @GetMapping("/initiate/{id}")
    @ResponseBody
    public ResponseEntity<?> initiatePayment(@PathVariable String id) {
        try {
            if (id == null || id.equalsIgnoreCase("undefined")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("message", "Invalid Payroll ID."));
            }

            Integer payrollId = Integer.parseInt(id);
            Payroll payroll = payrollService.getPayrollById(payrollId);

            if ("PAID".equalsIgnoreCase(payroll.getStatus())) {
                return ResponseEntity.badRequest().body(Map.of("message", "This payroll is already PAID."));
            }

            // Detect Role to embed in the UUID
            String roleTag = "ADMIN";
            if (payroll.getProcessedBy() != null &&
                    payroll.getProcessedBy().getRole().getRoleName().toUpperCase().contains("ACCOUNTANT")) {
                roleTag = "ACCOUNTANT";
            }

            String totalAmount = String.format("%.2f", payroll.getNetSalary());

            // New UUID Format: NAST-PAY-[ID]-[ROLE]-[TIMESTAMP]
            // This ensures the role is "remembered" even if the payment fails or is cancelled
            String transactionUuid = "NAST-PAY-" + payrollId + "-" + roleTag + "-" + System.currentTimeMillis();

            String productCode = EsewaConfig.PRODUCT_CODE;
            String signature = generateSignature(totalAmount, transactionUuid, productCode);

            return ResponseEntity.ok(Map.of(
                    "amount", totalAmount,
                    "tax_amount", "0",
                    "total_amount", totalAmount,
                    "transaction_uuid", transactionUuid,
                    "product_code", productCode,
                    "signature", signature,
                    "esewa_url", EsewaConfig.ESEWA_GATEWAY_URL
            ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Initiation Failed: " + e.getMessage()));
        }
    }

    @GetMapping("/success")
    @Transactional
    public String handleSuccess(@RequestParam("data") String base64Data) {
        try {
            String decodedString = new String(Base64.getDecoder().decode(base64Data));
            JsonNode response = new ObjectMapper().readTree(decodedString);

            String status = response.get("status").asText();
            String transactionUuid = response.get("transaction_uuid").asText();
            String esewaRefId = response.has("transaction_code") ? response.get("transaction_code").asText() : "N/A";

            // Split based on our new format: parts[2] is ID, parts[3] is Role
            String[] parts = transactionUuid.split("-");
            Integer payrollId = Integer.parseInt(parts[2]);
            String roleTag = parts[3];

            if ("COMPLETE".equalsIgnoreCase(status)) {
                payrollService.finalizePayroll(payrollId, esewaRefId);
                Payroll payroll = payrollService.getPayrollById(payrollId);

                String dashboardPath = roleTag.equalsIgnoreCase("ACCOUNTANT") ? "accountant/payroll-processing" : "admin/payroll";

                PayoutInfo payout = PayoutInfo.builder()
                        .payroll(payroll)
                        .employee(payroll.getEmployee())
                        .monthlyInfo(payroll.getMonthlyInfo())
                        .paymentDate(LocalDate.now())
                        .paymentMethod(payroll.getPaymentMethod())
                        .bankAccount(payroll.getEmployee().getPrimaryBankAccount())
                        .amount(payroll.getNetSalary())
                        .paymentStatus("SUCCESS")
                        .transactionReference(esewaRefId)
                        .createdAt(LocalDateTime.now())
                        .build();
                payoutInfoRepository.save(payout);

                try {
                    emailService.generateAndSendPayslip(payroll, esewaRefId);
                } catch (Exception e) {
                    log.error("Email Error: {}", e.getMessage());
                }

                return "redirect:http://localhost:5173/" + dashboardPath + "?status=success";
            }
            return "redirect:http://localhost:5173/login?status=error";
        } catch (Exception e) {
            log.error("Success Handler Error: {}", e.getMessage());
            return "redirect:http://localhost:5173/login?status=critical_error";
        }
    }

    @GetMapping("/failure")
    @Transactional
    public String handleFailure(@RequestParam(value = "data", required = false) String base64Data) {
        // Default to accountant if data is missing, as per your current requirement
        String dashboardPath = "accountant/payroll-processing";

        // If data is null, user manually clicked "Back" or "Cancel" on eSewa
        if (base64Data == null || base64Data.isEmpty()) {
            log.info("Manual cancellation detected. Redirecting to {}", dashboardPath);
            return "redirect:http://localhost:5173/" + dashboardPath + "?status=cancelled";
        }

        try {
            String decodedString = new String(Base64.getDecoder().decode(base64Data));
            JsonNode response = new ObjectMapper().readTree(decodedString);

            String transactionUuid = response.get("transaction_uuid").asText();
            String[] parts = transactionUuid.split("-");

            if (parts.length >= 4) {
                Integer payrollId = Integer.parseInt(parts[2]);
                String roleTag = parts[3];

                dashboardPath = roleTag.equalsIgnoreCase("ACCOUNTANT") ? "accountant/payroll-processing" : "admin/payroll";

                log.info("Rolling back for Payroll ID: {} for Role: {}", payrollId, roleTag);
                payoutInfoRepository.deleteByPayroll_PayrollId(payrollId);
                payoutInfoRepository.flush();
                payrollService.rollbackPayroll(payrollId);
            }
        } catch (Exception e) {
            log.error("Failure Handler Error: {}", e.getMessage());
        }

        return "redirect:http://localhost:5173/" + dashboardPath + "?status=failed";
    }

    private String generateSignature(String totalAmount, String uuid, String productCode) {
        try {
            String message = "total_amount=" + totalAmount + ",transaction_uuid=" + uuid + ",product_code=" + productCode;
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(EsewaConfig.SECRET_KEY.getBytes(), "HmacSHA256");
            sha256_HMAC.init(secret_key);
            byte[] hash = sha256_HMAC.doFinal(message.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("Signature Failed: " + e.getMessage());
        }
    }
}