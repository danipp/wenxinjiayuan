package com.demo.weixin.service;

public interface ILimitCountService {

    int getLimitCount(String ip, int type);

    void updateLimitCount(String ip, int type, int times);

    void deleteCountLimit(String ip, int type);
}
