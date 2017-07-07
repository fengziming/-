package com.fzm.jdbc;



import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by admin on 2017/7/2 0002.
 */
public class JDBC {
    private final static String url = "jdbc:mysql://localhost:3306/学生管理系统" ;
    private final static String username = "1111" ;
    private final static String password = "1111" ;
    Connection con=null;
    private Connection conn(){
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("找不到驱动程序类 ，加载驱动失败！");
            e.printStackTrace() ;
        }
        try{
         con = DriverManager.getConnection(url , username , password ) ;
        }catch(SQLException se){
            System.out.println("数据库连接失败！");
            se.printStackTrace() ;
        }
        System.out.println("数据库连接成功！");
        return con;
    }
    public List<SQL> select(SQL s,String tb,String sqlLike){
        List<SQL> list=new ArrayList<>();
        con=conn();
        ResultSet resultSet=null;
        try{
            StringBuffer sql=new StringBuffer("SELECT * FROM ")
                    .append(tb).append(" WHERE ")
                    .append(sqlLike);
            PreparedStatement ps=con.prepareStatement(sql.toString());
            resultSet=ps.executeQuery();
            list=instance(s,resultSet);
            ps.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        close(con);
        return list;
    }
     public List<SQL> instance(SQL obj,ResultSet rs){
        List<SQL> list=new ArrayList<>();
        try {
            ResultSetMetaData rsmd = rs.getMetaData();// 得到记录集，元素对象。
            // 通过此对象可以得到表的结构，包括，列名，列的个数，列数据类型
            Class<?> c=obj.getClass();//将实现本接口的类转换成Class 进行反射操作；
            while (rs.next()) {
                SQL s= (SQL) c.newInstance();
                for (int i = 0; i < rsmd.getColumnCount(); i++) {
                    String col_name = rsmd.getColumnName(i + 1);// 获取列名
                    Object value = rs.getObject(col_name);//获取列对应的值。
                    Field field = c.getDeclaredField(col_name);//获取对象对应的名称属性，然后给属性赋值
                    field.setAccessible(true);// 让可以访问私有属性
                    field.set(s, value);// 给对象私有属性赋值
                }
                list.add(s);
            }
        }catch (Exception e){
            e.printStackTrace();
        }return list;
    }
    public List<SQL> selectAll(SQL s,String tb){
        con=conn();
        List<SQL> list=new ArrayList<>();
        ResultSet resultSet=null;
        try{
            StringBuffer sql=new StringBuffer("SELECT * FROM ")
                    .append(tb);
            PreparedStatement ps=con.prepareStatement(sql.toString());
            resultSet=ps.executeQuery();
            list=instance(s,resultSet);
            ps.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        close(con);
        return list;
    }
    public void insert(SQL... sqls) {
        con=conn();
        int sum=0;
        try {
            for (SQL sql:sqls){
                String insertSql=sql.insertSQL();
                PreparedStatement ps=con.prepareStatement(insertSql);
                int x=ps.executeUpdate();
                sum+=x;
                ps.close();
            }
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("成功:"+sum+",第"+ ++sum +"条失败。");
        }
        close(con);
    }
    public int update(SQL[] sqls,SQL[] newSqls){
        con=conn();
        int sum=0;
        try {
           for (int i=0;i<sqls.length;i++){
               if (!sqls[i].equals(newSqls[i])){
                   String insertSql=sqls[i].updateSQL(newSqls[i]);
                   PreparedStatement ps=con.prepareStatement(insertSql);
                   int x=ps.executeUpdate();
                   sum+=x;
                   ps.close();
               }
            }
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("成功:"+sum+",第"+ ++sum +"条失败。");
            return sum;
        }
        close(con);
        return sum;
    }
    public int delete(SQL... sqls){
        con=conn();
        int sum=0;
        try {
            for (SQL sql:sqls){
                String insertSql=sql.deleteSQL();
                PreparedStatement ps=con.prepareStatement(insertSql);
                int x=ps.executeUpdate();
                sum+=x;
                ps.close();
            }
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("成功:"+sum+",第"+ ++sum +"条失败。");
            return sum;
        }
        close(con);
        return sum;
    }
    public boolean close(Connection con) {
        if (con != null) {  // 关闭连接对象
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
            System.out.println("数据库关闭成功！");
        }
        return true;
    }
}
