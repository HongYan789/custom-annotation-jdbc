package com.hongyan.study.annotation;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/***
 * 该项目主要是为了实现自定义注解解析sql并返回结果数据到对应注解方法上
 */
@SpringBootApplication
@MapperScan("com.hongyan.study.annotation.mapper")
public class AnnotationApplication {

    public static void main(String[] args) {
        SpringApplication.run(AnnotationApplication.class, args);
    }

}
