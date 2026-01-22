package np.edu.nast.payroll.Payroll.service;

import np.edu.nast.payroll.Payroll.entity.User;
<<<<<<< HEAD
=======
import org.springframework.security.core.userdetails.UserDetailsService;
>>>>>>> 3214be41b790e5d207ff8a4a5185d56a25676df5
import java.util.List;

public interface UserService {
    User create(User user);
    List<User> getAll();
    void delete(Integer id);

<<<<<<< HEAD
    // 🔥 ADD THESE TWO LINES BELOW 🔥
    User getById(Integer id);
    User update(Integer id, User user);

    // Existing methods
    void initiatePasswordReset(String email);
    void resetPassword(String token, String newPassword);
    User getByEmail(String email);
=======
    // Auth & Password Reset Methods
    void initiatePasswordReset(String email);
    void resetPassword(String token, String newPassword);
    User getByEmail(String email);

    // Administration Methods
>>>>>>> 3214be41b790e5d207ff8a4a5185d56a25676df5
    void sendOtpToAllUsers();
    User setupDefaultAccount(Integer empId);
}