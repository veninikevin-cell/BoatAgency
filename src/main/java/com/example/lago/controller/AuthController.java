package com.example.lago.controller;

import com.example.lago.dto.RegisterDTO;
import com.example.lago.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

// registrazione + pagina login. il login vero lo fa Spring Security
// (SecurityConfig), qui serve solo la view
@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/register")
    public String showRegister(Model model) {
        model.addAttribute("dto", new RegisterDTO());
        return "auth/register";
    }

    @PostMapping("/register")
    public String handleRegister(@ModelAttribute RegisterDTO dto, Model model) {
        try {
            userService.register(dto);
            return "redirect:/login?registered";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("dto", dto);  
            return "auth/register";
        }
    }

    @GetMapping("/login")
    public String showLogin() {
        return "auth/login";
    }
}