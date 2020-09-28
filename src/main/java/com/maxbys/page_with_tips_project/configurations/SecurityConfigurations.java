package com.maxbys.page_with_tips_project.configurations;

import com.maxbys.page_with_tips_project.users.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
public class SecurityConfigurations extends WebSecurityConfigurerAdapter {
    private final UsersService usersService;

    @Autowired
    public SecurityConfigurations(UsersService usersService) {
        this.usersService = usersService;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(usersService);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/h2-console/**").permitAll()
                .antMatchers("/css/**").permitAll()
                .antMatchers("/js/**").permitAll()
                .antMatchers("/images/**").permitAll()
                .antMatchers("/static/**").permitAll()
                .antMatchers("/").permitAll()
                .antMatchers("/register").permitAll()
                .antMatchers("/forgot-password").permitAll()
                .antMatchers("/change-password/**").permitAll()
                .antMatchers("/random-fact/**").permitAll()
                .antMatchers(HttpMethod.GET, "/categories/**").permitAll()
                .antMatchers(HttpMethod.GET, "/category/**").permitAll()
                .antMatchers(HttpMethod.GET, "/question/**").permitAll()
                .antMatchers(HttpMethod.GET, "/questions/**").permitAll()
                .antMatchers(HttpMethod.GET, "/answer/**").permitAll()
                .antMatchers(HttpMethod.GET, "/users-ranking").permitAll()
                .antMatchers(HttpMethod.GET, "/user/**").permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .formLogin()
                .loginPage("/login")
                .defaultSuccessUrl("/")
                .permitAll()
                .and()
                .logout().permitAll();
        http.headers().frameOptions().disable();
    }
}
