package org.example.worldcast.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CustomCacheErrorHandler implements CacheErrorHandler {

    @Override
    public void handleCacheGetError(RuntimeException exception, Cache cache, Object key) {
        log.warn(
                "Cache GET error for cache '{}' and key '{}': {}",
                cache.getName(),
                key,
                exception.getMessage());
        // Don't throw exception, let method execute normally
    }

    @Override
    public void handleCachePutError(
            RuntimeException exception, Cache cache, Object key, Object value) {
        log.warn(
                "Cache PUT error for cache '{}' and key '{}': {}",
                cache.getName(),
                key,
                exception.getMessage());
        // Don't throw exception, continue execution
    }

    @Override
    public void handleCacheEvictError(RuntimeException exception, Cache cache, Object key) {
        log.warn(
                "Cache EVICT error for cache '{}' and key '{}': {}",
                cache.getName(),
                key,
                exception.getMessage());
    }

    @Override
    public void handleCacheClearError(RuntimeException exception, Cache cache) {
        log.warn("Cache CLEAR error for cache '{}': {}", cache.getName(), exception.getMessage());
    }
}
