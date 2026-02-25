package np.edu.nast.payroll.Payroll.service;

import np.edu.nast.payroll.Payroll.entity.User;
import java.util.List;

public interface UserService {
    // Standard CRUD Operations
    User create(User user);
    List<User> getAll();
    User getById(Integer id);
    User update(Integer id, User user);
    void delete(Integer id);

    // Authentication & Password Recovery
    void initiatePasswordReset(String email);
    void resetPassword(String token, String newPassword);
    User getByEmail(String email);

    /**
     * NEW ONBOARDING CONCEPT:
     * Finalizes account setup by verifying the email token,
     * updating to a permanent username/password, and setting isFirstLogin to false.
     */
    void finalizeAccountSetup(String email, String newUsername, String newPassword, String token);

    /**
     * NEW ROLE SWITCH CONCEPT:
     * Validates if a user has the permission to switch to a specific role dashboard.
     */
    boolean canSwitchRole(User user, String roleName);

    // System & Utility Methods
    void sendOtpToAllUsers();
    User setupDefaultAccount(Integer empId);
}