package com.harshal.treso.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import java.util.concurrent.TimeUnit;

@Component
public class IdempotencyInterceptor implements HandlerInterceptor {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!"POST".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String idempotencyKey = request.getHeader("Idempotency-Key");
        if (idempotencyKey == null || idempotencyKey.isEmpty()) {
            return true; // Optionally we could block requests without the key, but we'll allow for now.
        }

        String redisKey = "idemp:" + idempotencyKey;
        // setIfAbsent acts as a distributed lock/idempotency check
        Boolean isNew = redisTemplate.opsForValue().setIfAbsent(redisKey, "processed", 5, TimeUnit.MINUTES);

        if (Boolean.FALSE.equals(isNew)) {
            response.setStatus(409); // 409 Conflict
            response.getWriter().write("Duplicate request detected. Please wait or use a new Idempotency-Key.");
            return false; // Stop processing
        }
        return true;
    }
}
