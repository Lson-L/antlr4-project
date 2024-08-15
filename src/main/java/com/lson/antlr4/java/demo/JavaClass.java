package com.lson.antlr4.java.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 *
 * @author lson
 *
 * @date 2024/8/13
 **/
public class JavaClass {
    private static final Logger LOG =
            LoggerFactory.getLogger(JavaClass.class);
    public void testMethod(){
        LOG.info(t());
        if(true){
            System.out.println(1);
        }
    }

    public String t(){
        return "1";
    }
}