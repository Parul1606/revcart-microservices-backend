package com.revature.gateway.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import reactor.core.publisher.Mono;

@Configuration
public class RateLimiterConfig {

    @Bean
    public KeyResolver ipKeyResolver() {
        return exchange -> {
            String ip = exchange.getRequest().getHeaders().getFirst("X-Forwarded-For");
            if (ip == null || ip.isEmpty()) {
                if (exchange.getRequest().getRemoteAddress() != null) {
                    ip = exchange.getRequest().getRemoteAddress().getAddress().getHostAddress();
                }
            }
            if (ip == null || ip.isEmpty()) {
                ip = "anonymous-client"; // Fallback to prevent NullPointerException
            }
            return Mono.just(ip);
        };
    }

    @Bean
    @Primary
    public RedisRateLimiter redisRateLimiter() {
        // replenishRate: 1 token/sec, burstCapacity: 25 tokens, requestedTokens: 5 tokens per request
        // This allows exactly 5 requests in a burst, and then takes 5 seconds to refill enough for another request.
        return new RedisRateLimiter(1, 25, 5);
    }
}
