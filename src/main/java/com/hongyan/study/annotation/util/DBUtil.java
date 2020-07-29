package com.hongyan.study.annotation.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class DBUtil {

    @Value("${spring.datasource.driver-class-name}")
    private String driverClass;

    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;


    public static String DRIVER = "";
    //链接地址，设置编码可用且为utf-8
    public static String URL="";
    //数据库用户名
    public static String USER="";
    //数据库密码
    public static String PWD="";

    @PostConstruct
    public void init(){
        DRIVER = driverClass;
        URL = url;
        USER = username;
        PWD = password;
    }



    /*
     *进行数据库的链接
     */
    public static Connection getConnection(){
        Connection con=null;
        try {
            //加载驱动
            Class.forName(DRIVER);
            //创建链接
            con= DriverManager.getConnection(URL, USER, PWD);
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        //返回连接
        return con;
    }

    /*
     *数据库关闭
     */
    public static void Close(Connection con, PreparedStatement pstmt, ResultSet rs){
        try {
            //判断是否被操作
            if(rs!=null){
                rs.close();
            }
            if(pstmt!=null){
                pstmt.close();
            }
            if(con!=null){
                con.close();
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /*
     *创建数据库执行操作，返回受影响的行数
     *@param String sql
     *@param Object params
     *@return int result
     */
    public static int executeUpdate(String sql,Object... params){
        //创建链接
        Connection con=getConnection();
        PreparedStatement pstmt=null;
        int result=0;
        try {
            //预编译sql语句，防止sql注入
            pstmt=con.prepareStatement(sql);
            //传递参数，如果参数存在
            if(params!=null){
                //进行循环传参
                for(int i=0;i<params.length;i++){
                    pstmt.setObject(i+1, params[i]);
                }
            }
            //执行sql语句，返回受影响行数
            result=pstmt.executeUpdate();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }finally{
            Close(con,pstmt,null);
        }
        return result;
    }
    /*
     *进行数据的查询，通过自建泛型RowMap，进行数据类型的定义
     *@param String sql
     *@param RowMap T
     *@param Object params
     *@return list<T>
     */
    public static <T> List<T> executeQuery(String sql,RowMap<T> rowMap,Object... params){
        //创建泛型List
        List<T> list=new ArrayList<>();
        //创建链接
        Connection con=getConnection();
        PreparedStatement pstmt=null;
        ResultSet rs=null;
        try {
            //绑定sql语句
            pstmt=con.prepareStatement(sql);
            //循环穿参
            if(params!=null){
                for(int i=0;i<params.length;i++){
                    pstmt.setObject(i+1, params[i]);
                }
            }
            //执行语句，用结果集接收
            rs=pstmt.executeQuery();
            while(rs.next()){
                //利用自建泛型实现数组的添加
                T t=rowMap.rowMapping(rs);
                list.add(t);
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }finally{
            Close(con,pstmt,rs);
        }
        return list;
    }


    /**
     *查询多条记录
     *
     * @param sql  查询语句
     * @param clazz 返回对象的class
     * @param objects 需要的参数，必须跟sql占位符的位置一一对应
     * @param <T>   泛型返回
     *
     * @return list
     */
    public static <T> List<T> queryForList(String sql, Class<T> clazz, Object... objects) {
        Connection con = getConnection();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        List<T> list = new ArrayList<>();
        try {
            preparedStatement=con.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                //调用 invokeObject方法，把一条记录封装成一个对象，添加到list中
                list.add(invokeObject(resultSet, clazz));
            }
        } catch (SQLException | IllegalAccessException | InstantiationException
                | NoSuchFieldException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
        }  finally {
            Close(con,preparedStatement,resultSet);
        }
        return list.size() > 0 ? list : new ArrayList<>();

    }

    public static <T> Object queryObject(String sql, Class<T> clazz, Object... objects) {
        Connection con = getConnection();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Object obj = new Object();
        try {
            preparedStatement=con.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                //调用 invokeObject方法，把一条记录封装成一个对象，添加到list中
                obj = invokeObject(resultSet, clazz);
            }
        } catch (SQLException | IllegalAccessException | InstantiationException
                | NoSuchFieldException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
        }  finally {
            Close(con,preparedStatement,resultSet);
        }
        return obj;

    }

    /**
     * 把数据库中的一条记录通过反射包装成相应的Bean
     * @param resultSet
     * @param clazz
     * @param <T>
     * @return
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws SQLException
     * @throws NoSuchFieldException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     */
    private static <T> T invokeObject(ResultSet resultSet, Class<T> clazz) throws IllegalAccessException, InstantiationException,
            SQLException, NoSuchFieldException, NoSuchMethodException, InvocationTargetException {
        T object = clazz.getDeclaredConstructor().newInstance();
        ResultSetMetaData metaData = resultSet.getMetaData();
        for (int i = 0, count = metaData.getColumnCount(); i < count; i++) {
            String columnName = metaData.getColumnName(i + 1);     //数据库返回结果的列名
            String fieldName = lineToHump(columnName); //去掉列名中的下划线“_”并转为驼峰命名
            Field field = clazz.getDeclaredField(fieldName);            //根据字段名获取field
            String methName = setMethodName(fieldName,field);         //拼set方法名
            Class type = field.getType();                       //获取字段类型
            Method setMethod = clazz.getDeclaredMethod(methName, field.getType());
            Object value = resultSet.getObject(i + 1);            //获取字段值
            setMethod.invoke(object, type.cast(value));       //强转并且赋值
        }
        return object;
    }

    private static Pattern linePattern = Pattern.compile("_(\\w)");
    /** 下划线转驼峰 */
    public static String lineToHump(String str) {
        str = str.toLowerCase();
        Matcher matcher = linePattern.matcher(str);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, matcher.group(1).toUpperCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    /**
     * set方法
     * @param name
     * @param field
     * @return
     */
    public static String setMethodName(String name, Field field){
        String methodName = "set";
        //通过反射，得到对象的get方法和数据格式
        return methodName + name.substring(0,1).toUpperCase() + name.substring(1);
    }

    /**
     * get方法
     * @param name
     * @param field
     * @return
     */
    public static String getMethodName(String name, Field field){
        String fieldType = field.getGenericType().toString();//通过反射获取键的数据类型
        String methodName = "get";
        if (("boolean").equals(fieldType)) {//布尔类型的是特殊的
            methodName = "is";
        }
        //通过反射，得到对象的get方法和数据格式
        return methodName + name.substring(0,1).toUpperCase() + name.substring(1);
    }

    //使用示例函数
    public static int update(){
        return executeUpdate("insert into student(name,age,sex) values(?,?,?)","姓名",12,"男");
   }

}

//自建泛型，实现rowMappping返回T类型
 interface RowMap<T> {
    public T rowMapping(ResultSet rs);
}