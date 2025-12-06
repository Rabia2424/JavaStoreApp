package com.store.storeapp.Controllers;
import com.store.storeapp.DTOs.JwtAuthResponse;
import com.store.storeapp.DTOs.LoginDto;
import com.store.storeapp.DTOs.RegisterDto;
import com.store.storeapp.Models.User;
import com.store.storeapp.Services.AuthService;
import com.store.storeapp.security.JwtTokenProvider;

import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

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
    public String login(LoginDto loginDto, HttpServletResponse response){
        String token  = authService.login(loginDto);

        // Token'ı bir HTTP-Only Cookie'ye ekle
        Cookie jwtCookie = new Cookie("jwt", token);
        jwtCookie.setHttpOnly(true);
        jwtCookie.setSecure(true); // HTTPS kullanıyorsanız true olmalı
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge((int) jwtTokenProvider.getExpirationDate(token).getTime() / 1000);
        response.addCookie(jwtCookie);

        var authentication = SecurityContextHolder.getContext().getAuthentication();

        boolean isAdmin = authentication != null &&
                authentication.getAuthorities().stream()
                        .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));

        if (isAdmin) {
            return "redirect:/admin/dashboard";
        } else {
            return "redirect:/";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpServletResponse response){
        authService.clearAuthCookie(response);
        return "redirect:/products/list";
    }

}
