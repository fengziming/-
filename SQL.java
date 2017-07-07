package com.fzm.jdbc;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

/**
 * Created by admin on 2017/7/3 0003.
 */
public interface SQL {
    public String table_name = null;

    /**
     * 返回使用本接口的实体的SQl语句中的“增”的SQl语句；
     * @return String SQL语句
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    default public String insertSQL() {
        Class<?> c=this.getClass();//将实现本接口的类转换成Class 进行反射操作；
        Object obj=this;//实例化
        Field[] fields=c.getDeclaredFields();//获得属性数组，头属性必须是table_name；
        StringBuffer sql1=null,sql2=null;
        try {
            sql1 = new StringBuffer("INSERT INTO ").append(fields[0].get(obj)).append(" (");
            sql2 = new StringBuffer(") VALUES ('");
            for (int i=1;i<fields.length;i++){
                fields[i].setAccessible(true);// 添加访问权限，才能访问私有属性， 不然会报错；
                if (fields[i].get(obj)!=null){//判断属性值是否为null；
                    sql1.append(fields[i].getName());//属性名；
                    sql2.append(fields[i].get(obj));//属性值；
                    if (i<fields.length-1){
                        sql1.append(",");
                        sql2.append("','");
                    }
                }
            }
            sql2.append("')");
        } catch (Exception e) {
            e.printStackTrace();

        }
        return sql1.toString()+sql2.toString();//连接返回SQL语句；
    }

    /**
     * 传入修改后的类，返回对数据库的UPDATA操作的SQL语句；
     * @param newObj 更新的类；
     * @return String SQl语句
     * @throws IllegalAccessException
     */
    default public String updateSQL(Object newObj) throws IllegalAccessException {
        Class<?> c=this.getClass();//将实现本接口的类转换成Class 进行反射操作；
        Object obj=this;//实例化
        Field[] fields=c.getDeclaredFields();//获得属性数组，头属性必须是table_name；
        Field[] newFields=newObj.getClass().getDeclaredFields();
        if (fields.length!=newFields.length){//判断是否为同一个类
            return "无效";
        }
        StringBuffer sql1=new StringBuffer("UPDATE ").append(fields[0].get(obj)).append(" SET ");
        StringBuffer sql2=new StringBuffer(" WHERE ");
        for (int i=1;i<fields.length;i++){
            fields[i].setAccessible(true);// 添加访问权限，才能访问私有属性， 不然会报错；
            newFields[i].setAccessible(true);

            if (fields[i].get(obj)==newFields[i].get(newObj)){//判断属性值是否相同，如果不同进行修改语句生成，相同进行判断语句生成
                sql2.append(fields[i].getName()).append(" = '").append(fields[i].get(obj)).append("' AND ");
            }else {
                sql1.append(newFields[i].getName()).append(" = '").append(newFields[i].get(newObj)).append("' , ");
            }
        }
        sql1.delete(sql1.length()-2,sql1.length());
        sql2.delete(sql2.length()-4,sql2.length());
        return sql1.toString()+sql2.toString();//连接返回SQL语句；
    }

    /**
     * 返回删除的SQL语句
     * @return String
     * @throws IllegalAccessException
     */
    default public String deleteSQL() throws IllegalAccessException {
        Class<?> c=this.getClass();//将实现本接口的类转换成Class 进行反射操作；
        Object obj=this;//实例化
        Field[] fields=c.getDeclaredFields();//获得属性数组，头属性必须是table_name；
        StringBuffer sql=new StringBuffer("DELETE FROM ").append(fields[0].get(obj)).append(" WHERE ");
        for (int i=1;i<fields.length;i++){
            fields[i].setAccessible(true);// 添加访问权限，才能访问私有属性， 不然会报错；
            if (fields[i].get(obj)!=null) {//判断属性值是否为null；
                sql.append(fields[i].getName()).append(" = '").append(fields[i].get(obj)).append("' AND ");
            }
        } sql.delete(sql.length()-4,sql.length());
    return sql.toString();
    }

}
