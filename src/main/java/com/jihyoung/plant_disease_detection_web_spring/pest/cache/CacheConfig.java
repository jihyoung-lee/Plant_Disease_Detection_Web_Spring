package com.jihyoung.plant_disease_detection_web_spring.pest.cache;

import com.jihyoung.plant_disease_detection_web_spring.pest.dto.search.PestSearchResponse;
import com.jihyoung.plant_disease_detection_web_spring.pest.dto.info.PestInfoResponse;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

import java.time.Duration;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    // RedisConnectionFactory는 스프링이 자동으로 넣어주는 레디스와 연결하는 객체
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {

        //ObjectMapper는 자바 객체를 JSON으로 바꾸고, JSON을 다시 자바 객체로 바꾸는 도구
        ObjectMapper objectMapper = JsonMapper.builder()
                //findAndAddModules() record, 날짜 타입 같은 걸 잘 처리할 수 있도록 필요한 모듈을 자동으로 찾아 붙이는 역할
                .findAndAddModules()
                .build();

        //Redis에 저장할 때는 PestInfoResponse를 JSON으로 바꾸고, 꺼낼 때는 JSON을 다시 PestInfoResponse
        JacksonJsonRedisSerializer<PestInfoResponse> serializer =
                new JacksonJsonRedisSerializer<>(
                        objectMapper,
                        PestInfoResponse.class
                );
        // searchSerializer
        JacksonJsonRedisSerializer<PestSearchResponse> searchSerializer =
                new JacksonJsonRedisSerializer<>(
                        objectMapper,
                        PestSearchResponse.class
                );

        //Redis pestInfoCacheConfig 설정
        RedisCacheConfiguration pestInfoCacheConfig =
                RedisCacheConfiguration.defaultCacheConfig()
                        .entryTtl(Duration.ofHours(1))
                        .disableCachingNullValues()
                        .serializeKeysWith(
                                RedisSerializationContext.SerializationPair
                                        .fromSerializer(new StringRedisSerializer())
                        )
                        //Redis 값(value)을 JacksonJsonRedisSerializer<PestInfoResponse>로 저장
                        .serializeValuesWith(
                                RedisSerializationContext.SerializationPair
                                        .fromSerializer(serializer)
                        );

        //pestSearchCacheConfig
        RedisCacheConfiguration pestSearchCacheConfig =
                RedisCacheConfiguration.defaultCacheConfig()
                        .entryTtl(Duration.ofMinutes(30))
                        .disableCachingNullValues()
                        .serializeKeysWith(
                                RedisSerializationContext.SerializationPair
                                        .fromSerializer(new StringRedisSerializer())
                        )
                        .serializeValuesWith(
                                RedisSerializationContext.SerializationPair
                                        .fromSerializer(searchSerializer)
                        );

        return RedisCacheManager.builder(connectionFactory)
                .withCacheConfiguration("pestInfo", pestInfoCacheConfig)
                .withCacheConfiguration("pestSearch", pestSearchCacheConfig)
                .build();
    }
}