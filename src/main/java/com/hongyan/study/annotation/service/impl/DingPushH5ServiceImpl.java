package com.hongyan.study.annotation.service.impl;

import com.hongyan.study.annotation.entity.DingPushH5DO;
import com.hongyan.study.annotation.mapper.DingPushH5Mapper;
import com.hongyan.study.annotation.service.DingPushH5Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DingPushH5ServiceImpl implements DingPushH5Service {
    @Autowired
    private DingPushH5Mapper dingPushH5Mapper;

    @Override
    public DingPushH5DO queryByUid(String uid) {
        return dingPushH5Mapper.queryByUid(uid);
    }
}
