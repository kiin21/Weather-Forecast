package org.example.worldcast.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.cache.CacheManagerCustomizer;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.support.CompositeCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.Arrays;

@Slf4j
@Configuration
@EnableCaching
public class CacheConfig implements CachingConfigurer {

    private final CustomCacheErrorHandler cacheErrorHandler;

    public CacheConfig(CustomCacheErrorHandler cacheErrorHandler) {
        this.cacheErrorHandler = cacheErrorHandler;
    }

    @Bean
    @Primary
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        // Create Redis cache manager with error handling
        RedisCacheManager redisCacheManager = createRedisCacheManager(redisConnectionFactory);

        // Create in-memory cache manager as fallback
        ConcurrentMapCacheManager inMemoryCacheManager = new ConcurrentMapCacheManager();
        inMemoryCacheManager.setAllowNullValues(false);

        // Composite cache manager - tries Redis first, then in-memory
        CompositeCacheManager compositeCacheManager = new CompositeCacheManager();
        compositeCacheManager.setCacheManagers(Arrays.asList(redisCacheManager, inMemoryCacheManager));
        compositeCacheManager.setFallbackToNoOpCache(false);

        log.info("Cache manager initialized with Redis primary and in-memory fallback");
        return compositeCacheManager;
    }

    private RedisCacheManager createRedisCacheManager(RedisConnectionFactory redisConnectionFactory) {
        RedisCacheConfiguration config =
                RedisCacheConfiguration.defaultCacheConfig()
                        .entryTtl(Duration.ofSeconds(300)) // 5 minutes TTL
                        .serializeKeysWith(
                                RedisSerializationContext.SerializationPair.fromSerializer(
                                        new StringRedisSerializer()))
                        .serializeValuesWith(
                                RedisSerializationContext.SerializationPair.fromSerializer(
                                        new GenericJackson2JsonRedisSerializer()))
                        .disableCachingNullValues();

        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(config)
                .transactionAware()
                .build();
    }

    @Bean
    public CacheManagerCustomizer<CompositeCacheManager> cacheManagerCustomizer() {
        return cacheManager -> {
            // Additional customization if needed
            log.info("Cache manager customization applied");
        };
    }

    @Override
    public CacheErrorHandler errorHandler() {
        return cacheErrorHandler;
    }
}
