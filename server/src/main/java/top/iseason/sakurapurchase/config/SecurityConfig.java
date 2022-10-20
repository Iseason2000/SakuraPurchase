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
                //支付回调放行
                .antMatchers("/api/pay/notify")
                .permitAll()
                //基础访问权限配置
                .antMatchers("/api/**")
                .hasRole("API")
                .antMatchers("/static/**")
                .permitAll()
                .anyRequest()
                .authenticated()
                .and()
                //登录
                .formLogin()
                .loginPage("/login")
                .usernameParameter("username")
                .passwordParameter("password")
                .defaultSuccessUrl("/")
                .permitAll()
                .and()
                //记住我
                .rememberMe()
                .tokenValiditySeconds(604800)
                .and()
                //防止跨域攻击
                .csrf()
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());

        return http.build();
    }
}
