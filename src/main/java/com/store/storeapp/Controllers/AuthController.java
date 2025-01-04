package com.store.storeapp.Controllers;
import com.store.storeapp.DTOs.JwtAuthResponse;
import com.store.storeapp.DTOs.LoginDto;
import com.store.storeapp.DTOs.RegisterDto;
import com.store.storeapp.Models.User;
import com.store.storeapp.Services.AuthService;
import com.store.storeapp.security.JwtTokenProvider;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@Controller
@RequestMapping("/auth")
public class AuthController {
    private AuthService authService;
    private JwtTokenProvider jwtTokenProvider;

    @GetMapping("/register")
    public String register(Model model){
        RegisterDto registerDto = new RegisterDto();
        model.addAttribute("user", registerDto);
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(RegisterDto registerDto,Model model){
        User registeredUser = authService.register(registerDto);
        model.addAttribute("user", registeredUser);
        return "redirect:/auth/login";
    }

    @GetMapping("/login")
    public String login(Model model){
        LoginDto loginDto = new LoginDto();
        model.addAttribute("user", loginDto);
        return "auth/login";
    }

    @PostMapping("/login")
    public String login(LoginDto loginDto){
        String token  = authService.login(loginDto);
        JwtAuthResponse jwtAuthResponse = new JwtAuthResponse();
        jwtAuthResponse.setAccessToken(token);
        jwtAuthResponse.setExpiresIn(jwtTokenProvider.getExpirationDate(token));

        return "redirect:/products/list";
    }

}
