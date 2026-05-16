package com.besquiros.piercingbysamar.controller;

import com.besquiros.piercingbysamar.dto.request.SetupRequest;
import com.besquiros.piercingbysamar.entity.User;
import com.besquiros.piercingbysamar.exception.BadRequestException;
import com.besquiros.piercingbysamar.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/setup")
@RequiredArgsConstructor
public class SetupController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Crée le premier compte admin.
     * Se désactive automatiquement dès qu'un admin existe en base.
     * À appeler une seule fois au premier déploiement via curl.
     */
    @PostMapping
    public ResponseEntity<Map<String, String>> setup(@Valid @RequestBody SetupRequest request) {
        if (userRepository.count() > 0) {
            throw new BadRequestException("Setup déjà effectué — un administrateur existe déjà.");
        }

        User admin = User.builder()
                .email(request.email())
                .passwordHash(passwordEncoder.encode(request.password()))
                .build();

        userRepository.save(admin);
        log.info("Premier admin créé : {}", request.email());

        return ResponseEntity.ok(Map.of(
                "message", "Admin créé avec succès. Cet endpoint est maintenant désactivé.",
                "email", request.email()
        ));
    }
}
