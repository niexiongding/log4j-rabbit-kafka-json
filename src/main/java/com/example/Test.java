package com.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 * @author Nie Xiongding
 * @since 2015年12月1日 下午5:50:35
 */
public class Test {
    static final Logger logger = LoggerFactory.getLogger("rabbitmqLogger");
    public static void main(String[] args) {
        for (int i = 0; i < 5; i++) {
            logger.info("num "+i);
            logger.error("num "+i);
        }
    }
    
}

