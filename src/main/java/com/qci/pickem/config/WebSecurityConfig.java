package com.qci.pickem.config;

import com.qci.pickem.service.PickemUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired private HttpAuthenticationEntryPoint httpAuthenticationEntryPoint;
    @Autowired private AuthSuccessHandler authSuccessHandler;
    @Autowired private PickemUserDetailsService userDetailsService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
//        http
//            .csrf().disable()
//            .authenticationProvider(authenticationProvider())
//            .exceptionHandling()
//            .authenticationEntryPoint(httpAuthenticationEntryPoint)
//                .and()
//                .formLogin()
//                .permitAll().loginProcessingUrl("/login")
//                .usernameParameter("username")
//                .passwordParameter("password")
//                .successHandler(authSuccessHandler)
//                .failureHandler(new SimpleUrlAuthenticationFailureHandler() {
//                    @Override
//                    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
//                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//                    }
//                })
//                .and()
//                .logout().permitAll()
//                .logoutRequestMatcher(new AntPathRequestMatcher("/login", "DELETE"))
//                .logoutSuccessHandler((request, response, authentication) -> {
//                    response.setStatus(HttpServletResponse.SC_OK);
//                    response.getWriter().flush();
//                })
//                .and()
//                .sessionManagement()
//                .maximumSessions(1);
//
//        http.authorizeRequests().anyRequest().authenticated();
        http
            .csrf().disable()
            .authorizeRequests().anyRequest().permitAll();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider());
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(new ShaPasswordEncoder());

        return authenticationProvider;
    }


    @Override
    protected AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }

    @Bean
    @Override
    public UserDetailsService userDetailsServiceBean() throws Exception {
        return super.userDetailsServiceBean();
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}