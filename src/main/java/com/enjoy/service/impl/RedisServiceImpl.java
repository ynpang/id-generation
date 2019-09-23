package com.enjoy.service.impl;

import com.enjoy.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;

@Service("redisServiceImpl")
public class RedisServiceImpl implements OrderService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public String getOrderId() {
        String key = "orderId" ;
        int timeout = 1000;
        String prefix = getPrefix(new Date());
        String orderId = null;
        try{
            long redisId = redisTemplate.opsForValue().increment(key,timeout);
            orderId = prefix + String.format("%1$05d", redisId);
            System.out.println("redis生成id:" + orderId);
        }catch (Exception e){
            System.out.println("生成订单号失败");
            e.printStackTrace();
        }

        return orderId;
    }

    private String getPrefix(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        int second = c.get(Calendar.SECOND);

        //补两位,因为一年最多三位数
        String monthFormat = String.format("%1$02d", month+1);
        //补两位，因为日最多两位数
        String dayFormat = String.format("%1$02d", day);
        //补两位，因为小时最多两位数
        String hourFormat = String.format("%1$02d", hour);

        String minuteFormat = String.format("%1$02d", minute);
        String secondFormat = String.format("%1$02d", second);
        return year + monthFormat + dayFormat+hourFormat + minuteFormat + secondFormat;

    }

}
