package com.generali.configuration;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement(proxyTargetClass = true)
@Order(Ordered.HIGHEST_PRECEDENCE)
@EnableCaching(proxyTargetClass = true)
@EnableAutoConfiguration(exclude = {JacksonAutoConfiguration.class})
public class ApplicationConfiguration {
}
