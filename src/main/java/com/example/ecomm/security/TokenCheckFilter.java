package com.example.ecomm.security;

import com.example.ecomm.clients.AuthClient;
import com.example.ecomm.dtos.AuthUserDto;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class TokenCheckFilter extends OncePerRequestFilter {

    private final AuthClient authClient;

    public TokenCheckFilter(AuthClient authClient) {
        this.authClient = authClient;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        if (request.getMethod().equals("GET")) {
            filterChain.doFilter(request, response);
            return;
        }

        String header = request.getHeader("Authorization");
        if (header == null) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return;
        }

        AuthUserDto user;
        try {
            user = authClient.validateToken(header);
        } catch (HttpClientErrorException.Unauthorized e) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return;
        } catch (RestClientException e) {
            response.setStatus(HttpStatus.SERVICE_UNAVAILABLE.value());
            return;
        }

        if (request.getMethod().equals("DELETE") && !"ADMIN".equals(user.getRole())) {
            response.setStatus(HttpStatus.FORBIDDEN.value());
            return;
        }

        filterChain.doFilter(request, response);
    }
}
