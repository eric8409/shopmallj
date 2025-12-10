package com.eric.shopmall.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class MySecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public MySecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                // 設定為 STATELESS，不使用 Session
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // 禁用 CSRF 保護
                .csrf(AbstractHttpConfigurer::disable)

                // 設定 CORS 跨域
                .cors(cors -> cors
                        .configurationSource(corsConfigurationSource())
                )

                .authorizeHttpRequests(request -> request


                        // 允許所有 OPTIONS 請求，預請求解決 post保護問題
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        // 允許訪問 WebSocket 端點
                        .requestMatchers("/ws/**").permitAll()
                         // 各國匯率
                        .requestMatchers("/api/currency/**").permitAll()



                        // 帳號功能 產生jwt簽章
                        .requestMatchers("/users/register", "/users/login", "/users/logout").permitAll()
                        .requestMatchers("/users/status").authenticated()


                        // 商品功能需要特定角色，使用jwt簽章
                        .requestMatchers(HttpMethod.GET, "/products").permitAll()
                        .requestMatchers(HttpMethod.GET, "/products/*").permitAll()
                        .requestMatchers(HttpMethod.POST, "/products").hasAnyAuthority("ROLE_MANAGER", "ROLE_ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/products/*").hasAnyAuthority("ROLE_MANAGER", "ROLE_ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/products/*").hasAnyAuthority("ROLE_MANAGER", "ROLE_ADMIN")

                        // 訂單功能需要特定角色，使用jwt簽章
                        .requestMatchers(HttpMethod.GET, "/totalqty/orders").permitAll()
                        .requestMatchers(HttpMethod.POST, "/users/*/orders").hasAnyAuthority("ROLE_NORMAL_MEMBER")
                        .requestMatchers(HttpMethod.GET, "/users/*/orders").hasAnyAuthority("ROLE_NORMAL_MEMBER")
                        .requestMatchers(HttpMethod.GET, "/admin/orders").hasAnyAuthority("ROLE_MANAGER", "ROLE_ADMIN")


                        // 任何其他請求都需要身份驗證 (要求 JWT 令牌)
                        .anyRequest().authenticated()
                );

        // 在標準的 UsernamePasswordAuthenticationFilter 之前加入您的 JWT 過濾器
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    
    // 配置 CORS 允許跨域請求
    @Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin("https://eric8409.github.io");
//        configuration.addAllowedOrigin("http://localhost:4200");
        configuration.addAllowedMethod("*");
        configuration.addAllowedHeader("*");
        configuration.setAllowCredentials(true); // 允許發送 Cookie
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}

