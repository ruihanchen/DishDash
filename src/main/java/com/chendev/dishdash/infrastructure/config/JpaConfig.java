package com.chendev.dishdash.infrastructure.config;

import com.chendev.dishdash.infrastructure.security.AuditorAwareImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorAwareImpl")
public class JpaConfig {

    @Bean
    public AuditorAware<Long> auditorAwareImpl() {
        return new AuditorAwareImpl();
    }
}
