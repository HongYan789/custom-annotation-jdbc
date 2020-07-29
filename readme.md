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


