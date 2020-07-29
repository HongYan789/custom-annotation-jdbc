DROP TABLE IF EXISTS `ding_push_h5`;

CREATE TABLE `ding_push_h5` (
  `pk_id` int(11) NOT NULL AUTO_INCREMENT,
  `uid` varchar(32) NOT NULL DEFAULT '',
  `create_date` datetime DEFAULT NULL COMMENT '创建时间',
  `update_date` datetime DEFAULT NULL COMMENT '更新时间',
  `job_no` varchar(10) NOT NULL DEFAULT '' COMMENT '工号',
  `store` varchar(10) NOT NULL DEFAULT '' COMMENT '门店编号',
  `qr_code` varchar(100) NOT NULL DEFAULT '' COMMENT '二维码信息',
  `url` varchar(100) NOT NULL DEFAULT '' COMMENT '目标跳转地址',
  `detail` varchar(1000) NOT NULL DEFAULT '' COMMENT '详情',
  `is_read` int(2) NOT NULL DEFAULT '0' COMMENT '是否已读',
  `type` int(2) NOT NULL DEFAULT '0' COMMENT '业务类型',
  `status` int(2) NOT NULL DEFAULT '0' COMMENT '推送状态：0待推送，1推送成功，2推送失败',
  `response` varchar(1000) NOT NULL DEFAULT '' COMMENT '推送结果详情',
  PRIMARY KEY (`pk_id`)
) ;

