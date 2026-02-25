package np.edu.nast.payroll.Payroll.security;

import lombok.RequiredArgsConstructor;
import np.edu.nast.payroll.Payroll.entity.User;
import np.edu.nast.payroll.Payroll.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // This looks up the user in the database based on what was typed in the login box
        // ADD THIS LOG TO YOUR CONSOLE
        System.out.println("Spring Security is looking for: " + username);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        // Format the role for Spring Security (e.g., ADMIN -> ROLE_ADMIN)
        String roleName = user.getRole().getRoleName().toUpperCase();
        if (!roleName.startsWith("ROLE_")) {
            roleName = "ROLE_" + roleName;
        }

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority(roleName))
        );
    }
}