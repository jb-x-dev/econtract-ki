package com.jbx.econtract.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * IP Whitelist Filter
 * 
 * Restricts access to the application to specific IP addresses.
 * 
 * Configuration:
 * - Set app.security.ip-whitelist.enabled=true in application.yml
 * - Set app.security.ip-whitelist.allowed-ips=1.2.3.4,5.6.7.8
 * 
 * Note: This is disabled by default. Enable only if you need IP-based access control.
 */
@Component
@Slf4j
public class IPWhitelistFilter implements Filter {

    @Value("${app.security.ip-whitelist.enabled:false}")
    private boolean enabled;

    @Value("${app.security.ip-whitelist.allowed-ips:}")
    private String allowedIpsString;

    private List<String> allowedIps;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        if (enabled && allowedIpsString != null && !allowedIpsString.isEmpty()) {
            allowedIps = Arrays.asList(allowedIpsString.split(","));
            log.info("IP Whitelist enabled. Allowed IPs: {}", allowedIps);
        } else {
            log.info("IP Whitelist disabled");
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        if (!enabled) {
            chain.doFilter(request, response);
            return;
        }

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        String clientIP = getClientIP(httpRequest);
        
        // Allow health check endpoint without IP restriction
        String requestURI = httpRequest.getRequestURI();
        if (requestURI.endsWith("/health") || requestURI.endsWith("/api/public/health")) {
            chain.doFilter(request, response);
            return;
        }
        
        // Check if IP is whitelisted
        if (allowedIps != null && allowedIps.contains(clientIP)) {
            log.debug("Access granted for IP: {}", clientIP);
            chain.doFilter(request, response);
        } else {
            log.warn("Access denied for IP: {} (not in whitelist)", clientIP);
            httpResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
            httpResponse.getWriter().write("Access Denied: Your IP address is not authorized to access this application.");
        }
    }

    /**
     * Get client IP address, considering proxy headers
     */
    private String getClientIP(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // If multiple IPs in X-Forwarded-For, take the first one
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}
