package com.demo.weixin.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.demo.weixin.constant.Constants;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@AllArgsConstructor
public class LimitCountServiceImpl implements ILimitCountService {

    private RedisTemplate<String, String> stringRedisTemplate;

    @Override
    public int getLimitCount(String ip, int type) {
        String limitKey = Constants.LIMIT_COUNT + ":" + type + ":" + ip;
        String limitRes = stringRedisTemplate.opsForValue().get(limitKey);
        return Objects.isNull(limitRes) ? 0 : Integer.parseInt(limitRes);
    }

    @Override
    public void updateLimitCount(String ip, int type, int times) {
        String limitKey = Constants.LIMIT_COUNT + ":" + type + ":" + ip;
        stringRedisTemplate.opsForValue().set(limitKey, times + "", Constants.LIMIT_FREQUENT_HOUR_INSTANCE, TimeUnit.HOURS);
    }

    @Override
    public void deleteCountLimit(String ip, int type) {
        String limitKey = Constants.LIMIT_COUNT + ":" + type + ":" + ip;
        stringRedisTemplate.delete(limitKey);
    }
}
