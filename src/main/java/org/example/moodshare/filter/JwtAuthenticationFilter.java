package org.example.moodshare.filter;

import org.example.moodshare.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.lang.NonNull;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        boolean shouldNotFilter = path.startsWith("/api/auth/") || 
               path.equals("/") ||
               path.equals("/api") ||
               path.contains("/swagger-ui/") || 
               path.contains("/v3/api-docs/") ||
               "OPTIONS".equals(request.getMethod());
               
        if (shouldNotFilter) {
            logger.debug("不过滤请求路径: {}", path);
        }
        
        return shouldNotFilter;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        try {
            String path = request.getRequestURI();
            logger.debug("处理请求: {}", path);

            String jwt = getJwtFromRequest(request);
            
            if (jwt == null) {
                logger.debug("JWT令牌不存在: {}", path);
            } else {
                logger.debug("获取到JWT令牌: {}", jwt.substring(0, Math.min(10, jwt.length())) + "...");
                
                // 验证JWT并设置认证
                if (jwtUtil.validateToken(jwt)) {
                    String username = jwtUtil.getUsernameFromToken(jwt);
                    logger.debug("JWT令牌有效，用户名: {}", username);
                    
                    try {
                        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
    
                        UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
    
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        logger.debug("已设置认证信息，用户: {}", username);
                    } catch (UsernameNotFoundException e) {
                        logger.error("JWT令牌中的用户不存在: {}", username);
                    }
                } else {
                    logger.debug("JWT令牌无效");
                }
            }
        } catch (Exception ex) {
            logger.error("无法设置用户认证: {}", ex.getMessage(), ex);
        }

        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}