package tacos.security;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.SecurityFilterChain;
import tacos.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import tacos.data.UserRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(UserRepository userRepo){
        return username -> {
            User user = userRepo.findByUsername(username);

            if(user != null){
                List<SimpleGrantedAuthority> authorities = Arrays.asList(
                        new SimpleGrantedAuthority("ROLE_USER"));
                return new org.springframework.security.core.userdetails.User(user.getUsername(),
                        user.getPassword(), authorities);
            }
            throw new UsernameNotFoundException("User '" + username + "' not found");
        };
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeRequests()
                .requestMatchers("/design").access("hasRole('USER')")
                .requestMatchers("/","/**").access("permitAll()")

                .and()
                    .formLogin()
                        .loginPage("/login")
                    .defaultSuccessUrl("/design", true) //lodgin 성공시 이동, 두번째 매개변수는 전 페이지가 어떤 곳이든 design으로 이동
                .and()
                    .logout().logoutSuccessUrl("/")

                .and()
                .build();
    }


}
