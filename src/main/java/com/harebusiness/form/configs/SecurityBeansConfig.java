package com.harebusiness.form.configs;

import com.harebusiness.form.exceptions.ResourceNotFoundException;
import com.harebusiness.form.models.AuthenticatedUser;
import com.harebusiness.form.models.User;
import com.harebusiness.form.repositories.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

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
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));

            return new AuthenticatedUser(user);
        };
    }
}
