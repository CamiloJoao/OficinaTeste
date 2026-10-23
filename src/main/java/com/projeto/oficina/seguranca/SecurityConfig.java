package com.projeto.oficina.seguranca;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // Gera e confere as senhas com BCrypt
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // CSS, JS e página de erro ficam liberados; o resto exige login
    @Bean
    public SecurityFilterChain filtroSeguranca(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(acesso -> acesso
                .requestMatchers("/css/**", "/js/**", "/error").permitAll()
                .anyRequest().authenticated())
            .formLogin(login -> login
                .defaultSuccessUrl("/", true));

        return http.build();
    }
}
