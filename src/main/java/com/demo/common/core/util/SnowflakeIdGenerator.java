package com.demo.common.core.util;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SnowflakeIdGenerator {
    // 起始的时间戳
    private final static long START_TIMESTAMP = 1753343175920L; // 2025-07-24 00:00:00
 
    // 调整后的位数分配
    private final static long SEQUENCE_BIT = 8;   // 序列号占 11 位   改为8位；
    private final static long MACHINE_BIT = 3;     // 机器 ID 占 5 位；改为3位；
    private final static long DATACENTER_BIT = 1;   // 数据中心 ID 占 5 位；改为1位；

    // 计算最大值
    private final static long MAX_DATACENTER_NUM = -1L ^ (-1L << DATACENTER_BIT);
    private final static long MAX_MACHINE_NUM = -1L ^ (-1L << MACHINE_BIT);
    private final static long MAX_SEQUENCE = -1L ^ (-1L << SEQUENCE_BIT);

    // 计算位移量
    private final static long MACHINE_LEFT = SEQUENCE_BIT;
    private final static long DATACENTER_LEFT = SEQUENCE_BIT + MACHINE_BIT;
    private final static long TIMESTAMP_LEFT = DATACENTER_LEFT + DATACENTER_BIT;

    private long datacenterId;  // 数据中心 ID
    private long machineId;     // 机器 ID
    private long sequence = 0L; // 序列号
    private long lastTimestamp = -1L; // 上次生成 ID 的时间戳
 
    private SnowflakeIdGenerator() {}
    
    private static final SnowflakeIdGenerator instance = new SnowflakeIdGenerator();
    
    public static SnowflakeIdGenerator getInstance() {
        return instance;
    }
    
    public void init(long datacenterId, long machineId) {
        if (datacenterId > MAX_DATACENTER_NUM || datacenterId < 0) {
            throw new IllegalArgumentException("Datacenter ID can't be greater than " + MAX_DATACENTER_NUM + " or less than 0");
        }
        if (machineId > MAX_MACHINE_NUM || machineId < 0) {
            throw new IllegalArgumentException("Machine ID can't be greater than " + MAX_MACHINE_NUM + " or less than 0");
        }
        this.datacenterId = datacenterId;
        this.machineId = machineId;
    }
 
    /**
     * 生成ID
     */
    public synchronized long nextId() {
        long timestamp = timeGen();
 
        if (timestamp < lastTimestamp) {
            throw new RuntimeException("Clock moved backwards. Refusing to generate id");
        }
 
        if (timestamp == lastTimestamp) {
            sequence = (sequence + 1) & MAX_SEQUENCE;
            if (sequence == 0) {
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0L;
        }
 
        lastTimestamp = timestamp;
 
        return ((timestamp - START_TIMESTAMP) << TIMESTAMP_LEFT)
                | (datacenterId << DATACENTER_LEFT)
                | (machineId << MACHINE_LEFT)
                | sequence;
    }
 
    /**
     * 解析id
     */
    public Map<String, Long> parse(long id) {
        // 计算各部分掩码
        long sequenceMask = -1L ^ (-1L << SEQUENCE_BIT);
        long machineMask = -1L ^ (-1L << MACHINE_BIT);
        long datacenterMask = -1L ^ (-1L << DATACENTER_BIT);
        
        Map<String, Long> result = new HashMap<>();
        result.put("timestamp", (id >>> TIMESTAMP_LEFT) + START_TIMESTAMP);
        result.put("datacenterId", (id >>> DATACENTER_LEFT) & datacenterMask);
        result.put("machineId", (id >>> MACHINE_LEFT) & machineMask);
        result.put("sequence", id & sequenceMask);
 
        return result;
    }
 
    private long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }
 
    private long timeGen() {
        return System.currentTimeMillis();
    }
    
    public static void main(String args[]) {
        SnowflakeIdGenerator generator = SnowflakeIdGenerator.getInstance();
        generator.init(0, 0); // 设置数据中心ID和机器ID
        
        for(int i = 0; i < 10; i++) {
            long id = generator.nextId();
            System.out.println("生成的ID: " + id);
            
            Map<String, Long> parsed = generator.parse(id);
            System.out.println("解析结果: " + parsed);
            
            System.out.println("时间戳: " + new Date(parsed.get("timestamp")));
            System.out.println("数据中心ID: " + parsed.get("datacenterId"));
            System.out.println("机器ID: " + parsed.get("machineId"));
            System.out.println("序列号: " + parsed.get("sequence"));
            System.out.println("----------------------");
        }
        
        
        long maxTimestampDiff = 65L * 365 * 24 * 60 * 60 * 1000; // 65 年的毫秒数
        long maxDatacenterId = 7; // 2^3 - 1
        long maxMachineId = 1;    // 2^1 - 1
        long maxSequence = 255;   // 2^8 - 1

        long maxId = (maxTimestampDiff << TIMESTAMP_LEFT) 
                   | (maxDatacenterId << DATACENTER_LEFT) 
                   | (maxMachineId << MACHINE_LEFT) 
                   | maxSequence;

        System.out.println("Max ID: " + maxId);
        System.out.println("是否超过 JS 安全整数: " + (maxId > 9007199254740991L));

    }
}