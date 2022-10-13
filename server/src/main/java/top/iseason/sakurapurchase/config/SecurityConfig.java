package top.iseason.sakurapurchase.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.authorizeRequests()
                .antMatchers("/api/**")
                .hasRole("API")
                .antMatchers("/static/**")
                .permitAll()
                .anyRequest()
                .hasRole("ADMIN")
                .and()
                .formLogin()
//                .loginProcessingUrl("/doLogin")
                .permitAll()
                .and()
                .csrf()
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());

        return http.build();
    }
}
