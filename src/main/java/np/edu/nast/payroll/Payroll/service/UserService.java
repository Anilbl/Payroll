package np.edu.nast.payroll.Payroll.service;

import np.edu.nast.payroll.Payroll.entity.User;
import java.util.List;

public interface UserService {

    // Core CRUD Operations
    User create(User user);

    List<User> getAll();

    User getById(Integer id);

    User update(Integer id, User user);

    void delete(Integer id);

    // Auth & Password Reset Methods
    void initiatePasswordReset(String email);

    void resetPassword(String token, String newPassword);

    User getByEmail(String email);

    // Administration & System Methods
    void sendOtpToAllUsers();

    User setupDefaultAccount(Integer empId);
}