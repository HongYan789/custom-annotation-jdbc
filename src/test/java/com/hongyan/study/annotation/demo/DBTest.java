package com.hongyan.study.annotation.demo;

import com.hongyan.study.annotation.AnnotationApplicationTests;
import com.hongyan.study.annotation.entity.DingPushH5DO;
import com.hongyan.study.annotation.service.DingPushH5Service;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class DBTest extends AnnotationApplicationTests {

    @Autowired
    private DingPushH5Service dingPushH5Service;

    @Test
    public void dbTest(){
        DingPushH5DO dingPushH5DO = dingPushH5Service.queryByUid("6ec73006828e4d80b867b34f2c956913");
        System.out.println("hahahah>>>>>"+dingPushH5DO);
    }
}
