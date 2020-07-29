package com.hongyan.study.annotation.aspect;

import com.alibaba.fastjson.JSONObject;
import com.hongyan.study.annotation.annotation.Convert;
import com.hongyan.study.annotation.util.DBUtil;
import com.hongyan.study.annotation.util.DateFormatUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 该项目主要是为了实现自定义注解解析sql并返回结果数据到对应注解方法上
 */
@Aspect
@Component
@Slf4j
public class ConvertAspect {
    @SuppressWarnings("unused")
    private static final Logger logger = LoggerFactory.getLogger(ConvertAspect.class);

    @Pointcut("@annotation(com.hongyan.study.annotation.annotation.Convert)")
    public void logPointCut() {
    }

    @Around("logPointCut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        long beginTime = System.currentTimeMillis();
        // 执行时长(毫秒)
        long time = System.currentTimeMillis() - beginTime;
        // 执行方法
        return convertSQL(point, time);
    }

    @Value("${convert.sql}")
    private String convert;

    private Object convertSQL(ProceedingJoinPoint joinPoint, long time) throws ClassNotFoundException {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        // 请求的方法名
        String className = joinPoint.getTarget().getClass().getName();
        String methodName = signature.getName();
        // 请求的参数
        Object[] args = joinPoint.getArgs();
        //判断是否包含该自定义注解
        if(method.isAnnotationPresent(Convert.class)){
            log.info("方法[{}]配置了Convert注解",methodName);
            JSONObject json = JSONObject.parseObject(convert);
            Convert convert = method.getAnnotation(Convert.class);
            if(convert != null){
                //获取自定义注解中属性名
                System.out.println("注解 value："+convert.value());
                String sql = convert.value();
                //正则匹配字符串中@{...} 的内容，例如：select * FROM ding_push_h5 WHERE uid=@{uid}
                String regex = "\\@\\{(.+?)\\}";
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(json.getString(sql));
                StringBuffer sb = new StringBuffer();
                //获取方法的返回值类型，例如： public DingPushH5DO queryByUid( String uid);返回值类型为DingPushH5DO
                String name = getReturnTypeName(method, methodName);
                //将@{uid}中参数uid替换为入参数据
                //则可组成完成的字符串为：select * FROM ding_push_h5 WHERE uid='123456'
                while (matcher.find()){
                    System.out.println(String.format(">>>>>>>>>>>>需要被替换的参数为:[%s]",matcher.group(1)));
                    matcher.appendReplacement(sb,getType(args[0]));
                    System.out.println(String.format(">>>>>>>>>>>>转换后的数据为:[%s]",sb.toString()));
                    //根据方法的返回值类型进行动态数据返回
                    return DBUtil.queryObject(sb.toString(), Class.forName(name));
                }
            }
        }
        return null;
    }

    /**
     * 获取方法返回值类型
     * @param method 反射的方法
     * @param methodName 方法名
     * @return
     */
    private String getReturnTypeName(Method method, String methodName) {
        //方法的返回类型
        Type genericReturnType = method.getGenericReturnType();
        //获取实际返回的参数名
        String returnTypeName = genericReturnType.getTypeName();
        System.out.println(">>>>>>>>>>>>"+methodName+"的返回参数是："+returnTypeName);
        String name = "";
        //判断是否是参数化类型
        if(genericReturnType instanceof ParameterizedType) {
            //如果是参数化类型,则强转
            ParameterizedType parameterizedType = (ParameterizedType) genericReturnType;
            //获取实际参数类型数组，比如List<User>，则获取的是数组[User]，Map<User,String> 则获取的是数组[User,String]
            Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
            for(Type type:actualTypeArguments) {
                //强转
                Class<?> actualTypeArgument = (Class<?>) type;
                //获取实际参数的类名
                name = actualTypeArgument.getName();
                System.out.println(">>>>>>>>>>>>"+methodName + "的返回值类型是参数化类型，其类型为：" + name);
            }
        }else{
            //不是参数化类型,直接获取返回值类型
            Class<?> returnType = method.getReturnType();
            //获取返回值类型的类名
            name = returnType.getName();
            System.out.println(">>>>>>>>>>>>"+methodName+"的返回值类型不是参数化类型其类型为："+name);

        }
        return name;
    }

    /**
     * 判断数据类型是否为数字或非数字，非数字则需左右增加单引号用于sql查询
     * @param obj
     * @return
     */
    public String getType(Object obj) {
        StringBuilder sb = new StringBuilder();
        if(obj instanceof String){
            return sb.append("'").append(obj).append("'").toString();
        }
        if(obj instanceof Integer || obj instanceof Long || obj instanceof Float || obj instanceof Double){
            return sb.append(obj).toString();
        }
        if(obj instanceof Date){
            try {
                return sb.append("'").append(DateFormatUtils.parsDate(String.valueOf(obj))).append("'").toString();
            } catch (ParseException e) {
                log.error("日期类型转换异常:",e);
            }
        }
        return sb.toString();
    }
}
