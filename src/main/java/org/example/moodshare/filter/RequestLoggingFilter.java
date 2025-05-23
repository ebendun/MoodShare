package org.example.moodshare.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * 请求日志过滤器
 * 记录所有HTTP请求和响应的详细信息，用于调试和监控
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)//优先级最高
public class RequestLoggingFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(RequestLoggingFilter.class);

    // 敏感头信息列表，这些头信息不会被完整记录
    private static final Set<String> SENSITIVE_HEADERS = Collections.unmodifiableSet(
            new HashSet<>(Arrays.asList("authorization", "cookie", "set-cookie")));

    // 不记录请求体的路径
    private static final Set<String> IGNORE_CONTENT_PATHS = Collections.unmodifiableSet(
            new HashSet<>(Arrays.asList("/api/login", "/api/register")));


    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        logger.info("REQUEST: {} {} from {}", 
            httpRequest.getMethod(), 
            httpRequest.getRequestURI(),
            httpRequest.getRemoteAddr());
        
        // Log request headers
        httpRequest.getHeaderNames().asIterator().forEachRemaining(headerName -> {
            logger.info("HEADER: {} = {}", headerName, httpRequest.getHeader(headerName));
        });
        
        chain.doFilter(request, response);
        
        logger.info("RESPONSE: {} {}", 
            httpResponse.getStatus(),
            httpRequest.getRequestURI());
    }
}
