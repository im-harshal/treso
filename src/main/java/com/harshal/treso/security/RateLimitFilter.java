package com.harshal.treso.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final int MAX_REQUESTS_PER_MINUTE = 20;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        String username = null;
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            username = SecurityContextHolder.getContext().getAuthentication().getName();
        }

        // Apply rate limit only to authenticated users
        if (username != null && !username.equals("anonymousUser")) {
            String key = "ratelimit:" + username;
            
            Long currentRequests = redisTemplate.opsForValue().increment(key);
            if (currentRequests != null && currentRequests == 1) {
                // First request in this window, set expiration to 1 minute
                redisTemplate.expire(key, 1, TimeUnit.MINUTES);
            }

            if (currentRequests != null && currentRequests > MAX_REQUESTS_PER_MINUTE) {
                response.setStatus(429); // 429 Too Many Requests
                response.getWriter().write("Too many requests. Please try again later.");
                return; // Block request
            }
        }
        
        filterChain.doFilter(request, response);
    }
}
