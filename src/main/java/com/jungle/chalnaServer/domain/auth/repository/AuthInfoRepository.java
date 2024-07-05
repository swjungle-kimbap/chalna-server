package com.jungle.chalnaServer.domain.auth.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jungle.chalnaServer.domain.auth.domain.entity.AuthInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@Slf4j
public class AuthInfoRepository {
    public static final String AUTH_KEY_PREFIX = "auth:info";
    private final ObjectMapper objectMapper;
    private final HashOperations<String, String, Object> authInfoHashOps;

    public AuthInfoRepository(RedisTemplate<String, Object> redisTemplate,ObjectMapper objectMapper) {
        this.authInfoHashOps = redisTemplate.opsForHash();
        this.objectMapper = objectMapper;
    }

    public void save(AuthInfo authInfo) {
        authInfoHashOps.put(AUTH_KEY_PREFIX, authInfo.id().toString(),authInfo);
    }

    public AuthInfo findById(final Long id){
        return objectMapper.convertValue(authInfoHashOps.get(AUTH_KEY_PREFIX,id.toString()),AuthInfo.class);
    }

    public void delete(final Long id){
        authInfoHashOps.delete(AUTH_KEY_PREFIX,id.toString());
    }
}
