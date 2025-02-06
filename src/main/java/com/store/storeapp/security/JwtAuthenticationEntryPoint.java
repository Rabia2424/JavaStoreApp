package com.store.storeapp.security;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;


import jakarta.servlet.http.HttpServletRequest.*;
import java.io.IOException;
import java.io.PrintWriter;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        response.setContentType("text/html");// Send to as html type
        PrintWriter writer = response.getWriter();
        writer.write("<html><body>");
        writer.write("<h2>Unauthorized Access</h2>");
        writer.write("<p>You need to <a href='/auth/login'>log in</a> to access this resource.</p>");
        writer.write("</body></html>");

    }

}

/*
This class handles unauthorized access in a Spring Security application.
 When an unauthenticated user tries to access a secured endpoint,
 it sends a 401 Unauthorized response with an error message.
 */