package com.mengyo.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * 兼容是否携带 /api 前缀的请求，支持反向代理或直连两种访问方式
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ApiPrefixForwardFilter extends OncePerRequestFilter {

    private final String apiPrefix;

    public ApiPrefixForwardFilter(@Value("${mengyo.api.prefix:/api}") String apiPrefix) {
        this.apiPrefix = normalizePrefix(apiPrefix);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String contextPath = request.getContextPath();
        String requestUri = request.getRequestURI();
        String pathWithinApp = requestUri.substring(contextPath.length());

        if (shouldForward(pathWithinApp)) {
            String forwardPath = pathWithinApp.substring(apiPrefix.length());
            if (!forwardPath.startsWith("/")) {
                forwardPath = "/" + forwardPath;
            }
            if (forwardPath.isEmpty()) {
                forwardPath = "/";
            }
            String queryString = request.getQueryString();
            String target = forwardPath + (StringUtils.hasText(queryString) ? "?" + queryString : "");
            request.getRequestDispatcher(target).forward(request, response);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean shouldForward(String pathWithinApp) {
        if (!pathWithinApp.startsWith(apiPrefix)) {
            return false;
        }
        if (pathWithinApp.length() == apiPrefix.length()) {
            return true;
        }
        return pathWithinApp.charAt(apiPrefix.length()) == '/';
    }

    private String normalizePrefix(String prefix) {
        if (!StringUtils.hasText(prefix)) {
            return "/api";
        }
        String normalized = prefix.trim();
        if (!normalized.startsWith("/")) {
            normalized = "/" + normalized;
        }
        if (normalized.endsWith("/")) {
            normalized = normalized.substring(0, normalized.length() - 1);
        }
        return normalized;
    }
}
