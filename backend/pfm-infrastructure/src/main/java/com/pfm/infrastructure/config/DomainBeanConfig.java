package com.pfm.infrastructure.config;

import com.pfm.domain.user.repository.UserRepository;
import com.pfm.domain.user.service.UserDomainService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DomainBeanConfig {

    @Bean
    public UserDomainService userDomainService(UserRepository userRepository) {
        return new UserDomainService(userRepository);
    }
}