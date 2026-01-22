package np.edu.nast.payroll.Payroll.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // Broadened mapping to /** ensures all endpoints (auth, employees, tax-slabs) are covered
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:5173")
                // Added PATCH to allow leave status updates and approval actions
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}