package com.primetrade.config;

import com.primetrade.entity.User;
import com.primetrade.repository.UserRepository;
import io.swagger.v3.oas.models.*;
import io.swagger.v3.oas.models.info.*;
import io.swagger.v3.oas.models.security.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AppConfig {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AppConfig(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository; this.passwordEncoder = passwordEncoder;
    }

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info().title("PrimeTrade Task API").version("v1.0.0")
                        .description("JWT-secured REST API. Register → Login → Copy token → Click Authorize → Use APIs.")
                        .contact(new Contact().name("Rahul Yadav Bakkatatla").email("rahulbhaktala@gmail.com")))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new Components().addSecuritySchemes("bearerAuth",
                        new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")));
    }

    @Bean
    public CommandLineRunner seedData() {
        return args -> {
            if (!userRepository.existsByUsername("admin")) {
                userRepository.save(User.builder().username("admin").email("admin@primetrade.com")
                        .password(passwordEncoder.encode("admin123")).role(User.Role.ROLE_ADMIN).build());
                System.out.println("✅ Admin seeded: admin / admin123");
            }
        };
    }
}
