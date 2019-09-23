package com.enjoy.service.impl;

import com.enjoy.service.OrderService;
import org.springframework.stereotype.Service;

@Service("snowFlakeOrderServiceImpl")
public class SnowflakeOrderServiceImpl implements OrderService {

    //开始时间戳(2015-01-01)
    private final long twepoch = 1420041600000L;

    //机器id所占的位数
    private final long workerIdBits = 5L;

    //数据表示id所占的位数
    private final long datacenterIdBits = 5L;

    //支持最大机器id,结果是31(这个移位算法可以很快的计算出几位二进制数所能表示的最大十进制数)
    private final long maxWorkerId = -1L ^(-1L << workerIdBits);

    private final long maxDatcenterId = -1L ^(-1L << datacenterIdBits);

    //序列号在id中占的位数
    private final long sequenceBits = 12L;

    //机器ID向左移动12位
    private final long workerIdShift = sequenceBits;

    //数字标识ID向左移动17位(12+5)
    private final long datacenterIdShift = sequenceBits + workerIdBits;

    //时间戳ID向左移动22位(5+5+12)
    private final long timestampLeftShift = sequenceBits + workerIdBits + datacenterIdBits ;

    //序列号最大值
    private final long sequenceMask = -1L ^ (-1L << sequenceBits);

    //工作机器id(0^31)
    private long workerId = 0l;

    //工作机器id(0^31)
    private long datacenterId = 0l;

    private long sequence = 0L;

    private long lastTimestamp = -1L;

//    public SnowflakeOrderServiceImpl(long workerId, long datacenterId){
//        if(workerId > maxWorkerId || workerId <0){
//            throw new IllegalArgumentException(String.format("worker id can't be greater than %d or less than 0",workerId));
//        }
//
//        if(datacenterId > maxDatcenterId || datacenterId <0){
//            throw new IllegalArgumentException(String.format("datacenter id can't be greater than %d or less than 0",datacenterId));
//        }
//
//        this.workerId = workerId;
//        this.datacenterId = datacenterId;
//    }

    public synchronized long nextId(){
        long timestamp = timeGen(); //得到当前时间 毫秒

        //如果当前时间小雨上一次id生成的时间戳，说明系统时钟回退过这个时候应当抛出异常 时钟回拨
        if(timestamp < lastTimestamp){
            throw new RuntimeException(
                    String.format("Clock moved backwards. Refusing to generate id for %d millseconds", lastTimestamp)
            );
        }
        //如果是同一时间生成，则进行毫秒内序列
        if(lastTimestamp == timestamp){
            //&算法是怎么回事 为什么+1还不够
            sequence = (sequence + 1) & sequenceMask;
            if(sequence == 0){
                timestamp = tilNextMills(lastTimestamp);
            }
        }
        //时间戳改变，毫秒内存列重置
        else{
            sequence = 0L;
        }
        //上次生成ID的时间戳
        lastTimestamp = timestamp;

        //移位并通过或运算平道一起组成64位的id
        return ((timestamp - twepoch)) << timestampLeftShift
                | (datacenterId << datacenterIdShift)
                | (workerId << workerIdShift)
                | sequence;


    }

    /**
     * 阻塞到下一个毫秒，知道获得新的时间戳
     * @param lastTimestamp 上次生成id的时间戳
     * @return 当前时间戳
     */
    private long tilNextMills(long lastTimestamp) {
        long timestamp = timeGen();
        while(timestamp == lastTimestamp){
            timestamp = timeGen();
        }
        return timestamp;
    }

    private long timeGen() {
        return System.currentTimeMillis();
    }


    @Override
    public Object getOrderId() {
        long id = nextId();
        System.out.println("snowflake 生成id:" + id) ;
        return id;
    }
}
