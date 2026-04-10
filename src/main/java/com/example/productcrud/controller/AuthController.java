package com.example.productcrud.controller;


import com.example.productcrud.dto.RegisterRequest;
import com.example.productcrud.model.User;
import com.example.productcrud.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("registerRequest", new RegisterRequest());
        return "register";
    }

    @PostMapping("/register")
    public String process(@ModelAttribute RegisterRequest registerRequest, RedirectAttributes redirectAttributes) {
        //Validasi username tidak boleh kosong
        if (registerRequest.getUsername() == null || registerRequest.getUsername().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Username tidak boleh kosong");
            return "redirect:/register";
        }

        //validasi: password tidak boleh kosong
        if (registerRequest.getPassword() == null || registerRequest.getPassword().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Password tidak boleh kosong");
            return "redirect:/register";
        }

        //validasi: password harus cocok
        if (!registerRequest.getPassword().equals(registerRequest.getConfirmPassword())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Password tidak cock");
            return "redirect:/register";
        }

        //validasi: username belum terdaftar
        if (userRepository.findByUsername(registerRequest.getUsername()). isPresent()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Username sudah terdaftar");
            return "redirect:/register";
        }

        //simpan user baru dengan password ter-encode
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        userRepository.save(user);

        redirectAttributes.addFlashAttribute("successMessage", "Registrasi berhasil! Silahkan login.");
        return "redirect:/login";
    }
}
