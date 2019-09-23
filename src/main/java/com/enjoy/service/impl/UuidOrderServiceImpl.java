package com.enjoy.service.impl;

import com.enjoy.service.OrderService;
import org.springframework.stereotype.Service;

import java.util.UUID;


@Service("uuidOrderServiceImpl")
public class UuidOrderServiceImpl implements OrderService {
    @Override
    public Object getOrderId() {
        return UUID.randomUUID();
    }
}
