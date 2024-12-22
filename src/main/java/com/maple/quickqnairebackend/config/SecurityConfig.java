package com.maple.quickqnairebackend.config;

/**
 * Created by zong chang on 2024/11/30 20:26
 *
 * @author : Maple-se
 * @version : 1.0
 * @description :
 */

import com.maple.quickqnairebackend.service.CustomUserDetailsService;
import com.maple.quickqnairebackend.util.JwtTokenFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final CustomUserDetailsService userDetailsService;  // 自动注入自定义的 UserDetailsService

    private final JwtTokenFilter jwtTokenFilter;


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests()
                .antMatchers(
                        "/v1/api/get-token",
                        "/swagger-ui.html",
                        "/swagger-ui/*",
                        "/v3/api-docs/**",
                        "/swagger-resources/**",
                        "/webjars/**").permitAll()
                .antMatchers("/quickqnaire/login", "/quickqnaire/register").permitAll()  // 注册和登录接口无需认证
                .antMatchers("/quickqnaire/detail/**").permitAll()  // 让所有 /quickqnaire/** 路径的请求都不需要认证
                .antMatchers("/admin/**").hasRole("ADMIN")  // 只有管理员可以访问 /admin/** 路径
                .antMatchers("/user/**").hasRole("USER")  // 只有普通用户可以访问 /user/** 路径
                .anyRequest().authenticated()  //其他接口需要认证
                .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and().addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);  // JWT 过滤器
    }


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // 设置 AuthenticationManager 来验证用户
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
