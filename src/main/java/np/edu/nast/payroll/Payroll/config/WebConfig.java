package np.edu.nast.payroll.Payroll.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
<<<<<<< HEAD
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:5173")
                // Ensure PATCH is listed here to allow approval actions
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
=======
        // This allows your frontend to access the tax-slab APIs
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:5173")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
>>>>>>> 3214be41b790e5d207ff8a4a5185d56a25676df5
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}