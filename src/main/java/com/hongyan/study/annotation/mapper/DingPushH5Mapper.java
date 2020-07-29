package com.hongyan.study.annotation.mapper;

import com.hongyan.study.annotation.annotation.Convert;
import com.hongyan.study.annotation.entity.DingPushH5DO;
import org.springframework.data.repository.query.Param;

public interface DingPushH5Mapper {

    @Convert(value = "queryByUid")
    DingPushH5DO queryByUid(@Param("uid") String uid);
}
