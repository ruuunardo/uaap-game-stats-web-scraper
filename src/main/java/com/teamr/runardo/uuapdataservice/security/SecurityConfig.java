package com.teamr.runardo.uuapdataservice.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import javax.sql.DataSource;

@Configuration
public class SecurityConfig {

    @Bean
    public UserDetailsManager userDetailsManager(DataSource dataSource) {
        JdbcUserDetailsManager userDetailsManager = new JdbcUserDetailsManager(dataSource);

        userDetailsManager
                .setUsersByUsernameQuery("select username, password, active from users where username = ?");
        userDetailsManager
                .setAuthoritiesByUsernameQuery("select username, role from roles where username = ?");

        return userDetailsManager;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .authorizeHttpRequests(configurer
                        -> configurer
                        .requestMatchers("/uaap-games", "/uaap-games/gamelist/**", "/uaap-games/images/**", "/uaap-games/update/**", "/uaap-games/export-to-csv").permitAll()
                        .requestMatchers("/uaap-games/delete/**", "/uaap-games/edit/**", "/uaap-games/show-form").hasRole("ADMIN")
                        .requestMatchers("/uaap-games/checkUrl/**").hasRole("ADMIN")
                        .anyRequest()
                        .authenticated())
                .formLogin(form
                        -> form.loginPage("/showLoginPage")
                        .loginProcessingUrl("/authenticateTheUser")
                        .permitAll())
                .logout(LogoutConfigurer::permitAll)
                .exceptionHandling(config -> config.accessDeniedPage("/access-denied"))
        ;

        return httpSecurity.build();
    }
}
