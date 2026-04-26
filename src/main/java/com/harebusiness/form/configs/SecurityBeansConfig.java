package com.harebusiness.form.configs;

import com.harebusiness.form.exceptions.UserNotFoundException;
import com.harebusiness.form.models.User;
import com.harebusiness.form.repositories.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.Optional;

@Configuration
public class SecurityBeansConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(UserRepository userRepository) {
        return id -> {
            User user = userRepository.findByIdAndIsDeletedFalse(Long.parseLong(id))
                    .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + id));

            return new org.springframework.security.core.userdetails.User(
                    user.getId().toString(),
                    user.getPassword(),
                    new ArrayList<>()
            );
        };
    }
}
