package com.nabil.example.service.impl;

import com.nabil.example.service.SimpleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class SimpleServiceImpl implements SimpleService {

    private static final Logger logger = LoggerFactory.getLogger(SimpleServiceImpl.class);

    @Override
    public void test() {
    logger.info(new Date().toString());
    }
}
