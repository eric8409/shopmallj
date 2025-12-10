package com.eric.shopmall.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenUtil jwtTokenUtil;
    private final MyUserDetailsService myUserDetailsService;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    // 這些 URL 應該被完全忽略過濾器
    private static final List<String> SKIPPED_URLS = Arrays.asList(
            "/users/register",
            "/users/login",
            "/users/logout",
            "/api/currency/**"  // 使用通配符包含 currency 相關 API
    );

    public JwtAuthenticationFilter(JwtTokenUtil jwtTokenUtil, MyUserDetailsService myUserDetailsService) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.myUserDetailsService = myUserDetailsService;
    }

    // *** [重要] 實現 shouldNotFilter 方法 ***
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getServletPath();
        return SKIPPED_URLS.stream().anyMatch(pattern -> pathMatcher.match(pattern, path));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String userId = null;
        String jwt = null;

        // --- 從 Cookie 中獲取 Token ---
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("accessToken".equals(cookie.getName())) {
                    jwt = cookie.getValue();
                    break;
                }
            }
        }
        // ------------------------------------

        if (StringUtils.hasText(jwt)) {
            try {
                userId = jwtTokenUtil.getUserIdFromToken(jwt);
            } catch (Exception e) {
                logger.error("Error parsing JWT from cookie or JWT expired/invalid", e);
            }
        }


        if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // 使用 MyUserDetailsService 中的 loadUserByUserId 方法
            UserDetails userDetails = this.myUserDetailsService.loadUserByUserId(userId);

            if (jwtTokenUtil.validateToken(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }

        filterChain.doFilter(request, response);
    }


    // *** 新增方法：從 Cookie 或 Authorization Header 中提取 JWT ***
    private String extractTokenFromRequest(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("accessToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }
        return null;
    }


}
