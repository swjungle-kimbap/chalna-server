package com.jungle.chalnaServer.global.common.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class DeviceInfoRepository {
    public final static String DEVICE_KEY_PREFIX = "fcm:device_id";

    private final ObjectMapper objectMapper;

    private final HashOperations<String,String,Object> deviceInfoHashOps;

    public void save(String deviceId,Long id){
        deviceInfoHashOps.put(DEVICE_KEY_PREFIX,deviceId, id);
    }
    public Long findById(String deviceId){
        return objectMapper.convertValue(deviceInfoHashOps.get(DEVICE_KEY_PREFIX, deviceId), Long.class);
    }
    public void delete(String deviceId){
        deviceInfoHashOps.delete(DEVICE_KEY_PREFIX, deviceId);
    }

}
