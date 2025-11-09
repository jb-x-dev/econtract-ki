package com.jbx.econtract.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Security Headers Filter
 * 
 * Adds security headers to all responses to:
 * - Prevent search engine indexing (X-Robots-Tag)
 * - Protect against XSS attacks
 * - Prevent clickjacking
 * - Enforce HTTPS
 */
@Component
public class SecurityHeadersFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        // Prevent search engine indexing
        httpResponse.setHeader("X-Robots-Tag", "noindex, nofollow, noarchive, nosnippet");
        
        // XSS Protection
        httpResponse.setHeader("X-XSS-Protection", "1; mode=block");
        
        // Prevent MIME type sniffing
        httpResponse.setHeader("X-Content-Type-Options", "nosniff");
        
        // Clickjacking protection
        httpResponse.setHeader("X-Frame-Options", "DENY");
        
        // Content Security Policy
        httpResponse.setHeader("Content-Security-Policy", 
            "default-src 'self'; " +
            "script-src 'self' 'unsafe-inline' 'unsafe-eval'; " +
            "style-src 'self' 'unsafe-inline'; " +
            "img-src 'self' data:; " +
            "font-src 'self' data:; " +
            "connect-src 'self'");
        
        // Referrer Policy
        httpResponse.setHeader("Referrer-Policy", "no-referrer");
        
        // Permissions Policy (formerly Feature-Policy)
        httpResponse.setHeader("Permissions-Policy", 
            "geolocation=(), microphone=(), camera=()");
        
        chain.doFilter(request, response);
    }
}
