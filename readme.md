# 该项目为demo样例，主要实现自定义注解的sql查询及数据返回
该项目存在的目的，为了解决springboot集成的mybatis项目中采用注解方式实现的弊端，
因为直接在java文件上增加@Select等注解时查看该方法太费眼，还不如吧这个操作放入properties文件中，
这样的话如果集成一些配置中心之类的工具则可无需重启并动态生效，这样不是很爽吗！！！！

##实现原理：
1、定义自定义注解Convert

2、在任意方法上使用@Convert(value="唯一标识名")

3、在application.properties文件中定义需实现的sql
例如：convert.sql={"queryByUid":"SELECT * FROM ding_push_h5 WHERE uid=@{uid}"}

4、定义切面文件ConvertAspect，用于实现自定义注解解析、采用jdbc调用数据库操作、将结果数据返回至调用方法



暂时实现了一个查询的方法的自定义注解调用及数据返回，其他方式可根据DBUtil中动态调用并返回
为了方便测试，数据库采用的h2，数据已提前准备，只需调用test下的junit中DBTest类下的方法即可


实现效果：
控制台打印：

注解 value：queryByUid
queryByUid的返回参数是：com.hongyan.study.annotation.entity.DingPushH5DO
queryByUid的返回值类型不是参数化类型其类型为：com.hongyan.study.annotation.entity.DingPushH5DO
需要被替换的参数为:[uid]
转换后的数据为:[SELECT pk_id as pkId , uid, job_no as jobNo, store, qr_code as qrCode, url, detail, is_read as isRead, create_date as createDate, update_date as updateDate, type, status,type, response FROM ding_push_h5 WHERE uid='6ec73006828e4d80b867b34f2c956913']
hahahah>>>>>DingPushH5DO(pkId=66, uid=6ec73006828e4d80b867b34f2c956913, jobNo=20000022, store=人力资源本部, qrCode=https://open.work.weixin.qq.com/wwopen/userQRCode?vcode=vce898ad074cd3ebb9, url=https://www.baidu.com?jobNo=20000022, detail={\"alias\":\"\",\"avatar\":\"\",\"department\":[50000510],\"email\":\"\",\"enable\":1,\"extattr\":{\"attrs\":[]},\"gender\":\"0\",\"hide_mobile\":0,\"is_leader_in_dept\":[0],\"isleader\":0,\"main_department\":50000510,\"mobile\":\"15902748812\",\"name\":\"肖\",\"order\":[0],\"position\":\"房县步行街店店长\",\"qr_code\":\"https://open.work.weixin.qq.com/wwopen/userQRCode?vcode=vce898ad074cd3ebb9\",\"status\":4,\"telephone\":\"\",\"thumb_avatar\":\"\",\"userid\":\"20000022\"}, isRead=0, createDate=2020-07-28 19:07:29.0, updateDate=2020-07-28 19:07:28.0, type=1, status=1, response={\"code\":200,\"cost\":0,\"data\":{\"errcode\":0,\"request_id\":\"10iy2iq4zumoa\",\"task_id\":238543235127},\"msg\":\"success\"})



