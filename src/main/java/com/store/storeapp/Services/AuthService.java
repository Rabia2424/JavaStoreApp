package com.store.storeapp.Services;

import com.store.storeapp.DTOs.LoginDto;
import com.store.storeapp.DTOs.RegisterDto;
import com.store.storeapp.Models.User;

public interface AuthService {
    String login(LoginDto loginDto);
    User register(RegisterDto registerDto);
    Long getUserIdFromToken(String token);
}
