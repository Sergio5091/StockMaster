package com.backend.stockmaster.core.config;

import com.backend.stockmaster.core.security.Role;
import com.backend.stockmaster.user.domain.User;
import com.backend.stockmaster.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        seedUsers();
    }

    private void seedUsers() {
        createUserIfNotExists("admin", "admin@stockmaster.com", "Admin Dupont", "Admin1234!", Role.ADMINISTRATEUR);
        createUserIfNotExists("manager", "manager@stockmaster.com", "Bernard Martin", "Manager1234!", Role.GESTIONNAIRE);
        createUserIfNotExists("operator", "operator@stockmaster.com", "Claire Leroy", "Operator1234!", Role.MAGASINIER);
        createUserIfNotExists("auditor", "auditor@stockmaster.com", "David Moreau", "Auditor1234!", Role.AUDITEUR);
        log.info("✅ Seed utilisateurs terminé");
    }

    private void createUserIfNotExists(String username, String email, String fullName, String password, Role role) {
        if (!userRepository.existsByUsername(username)) {
            User user = User.builder()
                    .username(username)
                    .email(email)
                    .fullName(fullName)
                    .password(passwordEncoder.encode(password))
                    .role(role)
                    .active(true)
                    .build();
            userRepository.save(user);
            log.info("👤 Utilisateur créé : {} ({})", username, role);
        }
    }
}
