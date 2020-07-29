package com.hongyan.study.annotation.entity;

import lombok.Data;
import java.util.Date;

@Data
public class DingPushH5DO {

    private Integer pkId;
    private String uid;
    private String jobNo;
    private String store;
    private String qrCode;
    private String url;
    private String detail;
    private Integer isRead;
    private Date createDate;
    private Date updateDate;
    private Integer type;
    private Integer status;
    private String response;

}
