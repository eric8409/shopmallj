package com.eric.shopmall.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.util.AntPathMatcher; // 導入 AntPathMatcher

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenUtil jwtTokenUtil;
    private final MyUserDetailsService myUserDetailsService;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    // 產生 jwt簽章
    private static final List<String> SKIPPED_URLS = Arrays.asList(
            "/users/register",
            "/users/login"

    );

    public JwtAuthenticationFilter(JwtTokenUtil jwtTokenUtil, MyUserDetailsService myUserDetailsService) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.myUserDetailsService = myUserDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        final String authorizationHeader = request.getHeader("Authorization");

        // 變數名稱從 username 改為 userId
        String userId = null;
        String jwt = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            // 方法呼叫從 getUsernameFromToken 改為 getUserIdFromToken
            userId = jwtTokenUtil.getUserIdFromToken(jwt);
        }

        if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // 呼叫專門的 loadUserByUserId 方法
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

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        return SKIPPED_URLS.stream().anyMatch(pattern -> pathMatcher.match(pattern, path));
    }
}
