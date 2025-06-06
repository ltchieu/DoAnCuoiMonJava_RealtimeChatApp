package com.doancuoimon.realtimechat.config;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.doancuoimon.realtimechat.service.UserService;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@Configuration
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SecurityConfig {
    UserService userService;

    public SecurityConfig(@Lazy UserService userService) {
        this.userService = userService;
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return userService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Cấu hình chuỗi lọc bảo mật cho ứng dụng.
     * Cho phép truy cập không cần xác thực với các API đăng nhập, đăng ký (sign-in,
     * sign-up).
     * Các yêu cầu khác đều cần xác thực. Tắt CSRF để phù hợp với API REST.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(c -> c
                        .requestMatchers(HttpMethod.GET, "/", "/index.html").permitAll()
                        .requestMatchers(HttpMethod.GET, "/auth/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/login").permitAll()
                        .requestMatchers("/ws/**", "/chatapp/**", "/app/**", "/topic/**", "/user/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/sign-up").permitAll()
                        .anyRequest().authenticated())
                .formLogin(c -> c
                        .loginPage("/auth/sign-in.html")
                        .loginProcessingUrl("/login")
                        .failureHandler((request, response, exception) -> {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.setContentType("application/json");
                            response.setCharacterEncoding("UTF-8");
                            response.getWriter().write("{\"error\": \"Sai username hoặc mật khẩu\"}");
                        })
                        .permitAll())
                .csrf(AbstractHttpConfigurer::disable).logout(logout -> logout
                        .logoutUrl("/api/logout")
                        .logoutSuccessHandler((req, res, auth) -> res.setStatus(200))
                );

        return http.build();
    }

}
