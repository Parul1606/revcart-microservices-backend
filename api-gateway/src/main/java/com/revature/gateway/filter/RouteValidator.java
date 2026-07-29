package com.revature.gateway.filter;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.http.HttpMethod;

import java.util.List;
import java.util.function.Predicate;

@Component
public class RouteValidator {

    public static final List<String> openApiEndpoints = List.of(
            "/api/auth/send-otp",
            "/api/auth/verify-otp",
            "/api/auth/refresh-token",
            "/api/auth/login",
            "/api/auth/register",
            "/eureka"
    );

    public Predicate<ServerHttpRequest> isSecured =
            request -> {
                String path = request.getURI().getPath();
                HttpMethod method = request.getMethod();

                // Actuator or Health endpoints are public
                if (path.contains("/actuator") || path.endsWith("/health")) {
                    return false;
                }

                // Match paths in our open API endpoints list
                boolean isPublicPath = openApiEndpoints.stream().anyMatch(path::contains);
                if (isPublicPath) {
                    return false;
                }

                // GET requests on products and categories are public
                if (method == HttpMethod.GET && 
                    (path.startsWith("/api/products") || path.startsWith("/api/categories"))) {
                    return false;
                }

                return true;
            };
}
