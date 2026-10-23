package com.unwind.backend.controller; // Sostituisci con il tuo package effettivo

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "Unwind Backend is running successfully!";
    }
}