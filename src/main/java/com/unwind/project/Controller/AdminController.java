package com.unwind.project.Controller;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.unwind.project.Model.AdminUser;
import com.unwind.project.Repository.AdminRepository;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "http://localhost:4200")
public class AdminController {

    @Autowired
    private AdminRepository adminRepository;

    @PostMapping("/login")
    public ResponseEntity<?> loginAdmin(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        String password = credentials.get("password");

        // Controllo di sicurezza prima di interrogare il DB
        if (email == null || password == null) {
            return ResponseEntity.status(400).body(Map.of("success", false, "message", "Email e password obbligatorie"));
        }

        Optional<AdminUser> adminOpt = adminRepository.findByEmail(email);
        
        // Usiamo getPasswordHash() anziché getPassword() per matchare la colonna password_hash del database
        if (adminOpt.isPresent() && adminOpt.get().getPasswordHash() != null && adminOpt.get().getPasswordHash().equals(password)) {
            return ResponseEntity.ok(Map.of("success", true, "message", "Accesso consentito"));
        }
        
        return ResponseEntity.status(401).body(Map.of("success", false, "message", "Credenziali errate"));
    }
}