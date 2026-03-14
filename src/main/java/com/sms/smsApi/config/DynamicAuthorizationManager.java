package com.sms.smsApi.config;

import com.sms.smsApi.model.Permission;
import com.sms.smsApi.repository.RequestMapRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

@Component
public class DynamicAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {

    private final RequestMapRepository permissionRepository;

    public DynamicAuthorizationManager(RequestMapRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    @Override
    public AuthorizationDecision check(
            Supplier<Authentication> authSupplier,
            RequestAuthorizationContext context) {

        HttpServletRequest request = context.getRequest();
        String requestUrl = request.getRequestURI();
        String requestMethod = request.getMethod();

        Authentication auth = authSupplier.get();
        if (auth == null || !auth.isAuthenticated()) {
            return new AuthorizationDecision(false);
        }

        // Load active permissions from DB
        List<Permission> permissions = permissionRepository.findAll();

        // Find matching permission for this URL + Method
        boolean hasAccess = permissions.stream()
                .filter(p -> p.getHttpMethod().equalsIgnoreCase(requestMethod))
                .filter(p -> pathMatches(p.getUrl(), requestUrl))
                .anyMatch(p -> {
                    List<String> allowedRoles = Arrays.asList(p.getRoles().split(","));
                    return auth.getAuthorities().stream()
                            .anyMatch(a -> allowedRoles.stream()
                                    .anyMatch(role -> a.getAuthority().equals("ROLE_" + role.trim()))
                            );
                });

        return new AuthorizationDecision(hasAccess);
    }

    // Support wildcards like /api/v1/users/**
    private boolean pathMatches(String pattern, String requestUrl) {
        AntPathMatcher matcher = new AntPathMatcher();
        return matcher.match(pattern, requestUrl);
    }
}