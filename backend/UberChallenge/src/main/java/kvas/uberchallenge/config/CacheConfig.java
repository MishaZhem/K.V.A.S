package kvas.uberchallenge.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

@Configuration
@RequiredArgsConstructor
public class CacheConfig {
    @Bean
    public RedisCacheConfiguration redisCacheConfig() {
        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(java.time.Duration.ofMinutes(10))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));
    }

    @Bean
    public RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer() {
        return (builder) -> builder
                .withCacheConfiguration("graphCache",
                        RedisCacheConfiguration.defaultCacheConfig().entryTtl(java.time.Duration.ofMinutes(60)))
                .withCacheConfiguration("jobCache",
                        RedisCacheConfiguration.defaultCacheConfig().entryTtl(java.time.Duration.ofMinutes(1)));
    }
}
