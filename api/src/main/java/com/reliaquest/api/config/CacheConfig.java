package com.reliaquest.api.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for enabling caching in the application.
 * Uses Caffeine as the cache provider for efficient in-memory caching.
 */
@Configuration
@EnableCaching
public class CacheConfig {
}
