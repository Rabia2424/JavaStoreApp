package com.store.storeapp.security;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.store.storeapp.Models.Role;
import com.store.storeapp.Models.User;
import com.store.storeapp.Services.impl.UserService;
import com.store.storeapp.Utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;

// Execute Before Executing Spring Security Filters
// Validate the JWT Token and Provides user details to Spring Security for Authentication
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private JwtTokenProvider jwtTokenProvider;

    private UserDetailsService userDetailsService;
    private JwtUtils jwtUtils;
    private UserService userService;

    @Autowired
    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider, UserDetailsService userDetailsService, JwtUtils jwtUtils, UserService userService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userDetailsService = userDetailsService;
        this.jwtUtils = jwtUtils;
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String servletPath = request.getServletPath();

        if (servletPath.equals("/") ||
                servletPath.equals("/auth/login") ||
                servletPath.equals("/auth/register") ||
                servletPath.equals("/auth/logout") ||
                servletPath.equals("/products/list") ||
                servletPath.startsWith("/assets/") ||
                servletPath.startsWith("/css/") ||
                servletPath.startsWith("/js/") ||
                servletPath.startsWith("/images/") ||
                servletPath.equals("/errorPage")) {
            try {
                isAuthenticatedWithJwtFromCookie(request);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            filterChain.doFilter(request, response);
            return;
        }

        if (!isPathValid(servletPath) ) {
            // 302 Redirect ile başka bir URL'ye yönlendir
            response.sendRedirect("/errorPage"); // İstediğiniz URL'yi burada belirleyin
            return; // Sonlandırıyoruz, token doğrulamasına geçmiyoruz
        }

        System.out.println("JwtAuthenticationFilter çalıştı");
        // Get JWT token from HTTP request
        String token = getTokenFromCookie(request, response);

        // Validate Token
        if(StringUtils.hasText(token) && jwtTokenProvider.validateToken(token)){
            // get username from token
            String username = jwtTokenProvider.getUsername(token);

            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities()
            );

            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }else {
            throw new AuthenticationException("Unauthorized"){};
        }

        try {
            isAuthenticatedWithJwtFromCookie(request);//This is for checking isAuthenticated actually with control they have claims in jwt.
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        filterChain.doFilter(request, response);
    }

    private String getTokenFromCookie(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("jwt".equals(cookie.getName())) {
                    String token = cookie.getValue();
                    System.out.println("Cookie found");
                    if(isTokenExpired(token)){
                        System.out.println("Token expired, deleting cookie.");
                        deleteCookie(response);
                        return null;
                    }
                    return token;
                }
            }
        }
        return null;
    }

    private void isAuthenticatedWithJwtFromCookie(HttpServletRequest request) throws Exception {
        Cookie[] cookies = request.getCookies();
        String jwt = null;
        String username = null;
        User user = null;
        boolean containsAdminRole = false;
        if(cookies != null){
            for(Cookie cookie: cookies){
                if("jwt".equals(cookie.getName())){
                    jwt = cookie.getValue();
                    break;
                }
            }
        }

        if(jwt != null && jwtUtils.isAuthenticated(jwt)){
            username = jwtTokenProvider.getUsername(jwt);
            user = userService.getUserById(jwtTokenProvider.getUserId(jwt)).orElseThrow(()-> new Exception("Error happened!"));
            for(Role role : user.getRoles()){
                if(role.getName().equals("ROLE_ADMIN")){
                    containsAdminRole = true;
                    break;
                }else{
                    containsAdminRole = false;
                }
            }

            request.setAttribute("isAuthenticated", true);
            request.setAttribute("username", username);
            request.setAttribute("hasAdminRole", containsAdminRole);
        }else{
            request.setAttribute("isAuthenticated", false);
        }
    }

//    private String getTokenFromRequest(HttpServletRequest request){
//        String bearerToken = request.getHeader("Authorization");
//
//        if(StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")){
//            return bearerToken.substring(7, bearerToken.length());
//        }
//
//        return null;
//    }

    private boolean isPathValid(String path) {
        // Controller'larınızı kontrol etmek için burada gerekli mantığı uygulayın
        return path.matches("/auth/.*|/products/.*|/category/.*|/cart/.*|/order/.*|/admin/.*|/payment/.*");
    }

    private boolean isTokenExpired(String token){
        long expireTime = jwtTokenProvider.getExpirationDate(token).getTime(); // Millisaniye cinsinden
        System.out.println(expireTime);
        long currentTime = System.currentTimeMillis(); // Geçerli zamanı alıyoruz (millisaniye cinsinden)
        return expireTime < currentTime;
    }

    // Cookie'yi silme işlemi
    private void deleteCookie(HttpServletResponse response) {
        System.out.println("Deleting cookie");
        Cookie cookie = new Cookie("jwt", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);  // HTTPS kullanıyorsanız true olmalı
        cookie.setPath("/"); // Cookie'nin geçerli olduğu path
        cookie.setMaxAge(0); // Cookie'yi hemen silmek için Max-Age'i 0 yapıyoruz
        response.addCookie(cookie);
    }

}