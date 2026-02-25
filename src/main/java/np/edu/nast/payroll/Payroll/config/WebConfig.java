package np.edu.nast.payroll.Payroll.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // Allows frontend (Vite/React) to communicate with the Backend API
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:5173")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        /**
         * This maps the URL path http://localhost:8080/photos/filename.jpg
         * to the physical folder 'uploads/photos/' in your project directory.
         * * The 'file:' prefix is critical as it tells Spring to look outside
         * the classpath (the target folder) and into the actual file system.
         */
        registry.addResourceHandler("/photos/**")
                .addResourceLocations("file:uploads/photos/");
    }
}