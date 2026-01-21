package np.edu.nast.payroll.Payroll.service;

import np.edu.nast.payroll.Payroll.entity.User;
import java.util.List;

public interface UserService {
    User create(User user);
    List<User> getAll();
    void delete(Integer id);

    // 🔥 ADD THESE TWO LINES BELOW 🔥
    User getById(Integer id);
    User update(Integer id, User user);

    // Existing methods
    void initiatePasswordReset(String email);
    void resetPassword(String token, String newPassword);
    User getByEmail(String email);
    void sendOtpToAllUsers();
    User setupDefaultAccount(Integer empId);
}