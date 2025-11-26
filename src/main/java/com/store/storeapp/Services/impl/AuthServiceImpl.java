package com.store.storeapp.Services.impl;

import com.store.storeapp.DTOs.LoginDto;
import com.store.storeapp.DTOs.RegisterDto;
import com.store.storeapp.Models.Role;
import com.store.storeapp.Models.User;
import com.store.storeapp.Repositories.RoleRepository;
import com.store.storeapp.Repositories.UserRepository;
import com.store.storeapp.Services.AuthService;
import com.store.storeapp.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CookieValue;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

@Service
public class AuthServiceImpl implements AuthService {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEn;

    @Override
    public String login(LoginDto loginDto) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginDto.getUsernameOrEmail(),
                loginDto.getPassword()
        ));
        /*
        The authenticationManager.authenticate() call takes the username and password information
        and triggers Spring Security's automatic authentication mechanism.
        If the correct credentials are provided, the authentication is successful,
        and an authentication token is created.
        */

        SecurityContextHolder.getContext().setAuthentication(authentication);

        System.out.println(authentication);
        String token = jwtTokenProvider.generateToken(authentication);

        return token;
    }

    @Override
    public User register(RegisterDto registerDto) {
        User user = new User();
        user.setName(registerDto.getName());
        user.setSurname(registerDto.getSurname());
        user.setUsername(registerDto.getUsername());
        user.setEmail(registerDto.getEmail());
        user.setPassword(passwordEn.encode(registerDto.getPassword()));

        Role userRole = roleRepository.findByName("USER");
        user.setRoles(Arrays.asList(userRole));

        return userRepository.save(user);
    }

    //General function I have used in all crud operation.
    @Override
    public Long getUserIdFromToken(String token) {
        if(token == null && token.isEmpty()){
            throw new IllegalArgumentException("Jwt Token not found in cookie");
        }
        return jwtTokenProvider.getUserId(token);
    }

    public void clearAuthCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie("jwt", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }


}
