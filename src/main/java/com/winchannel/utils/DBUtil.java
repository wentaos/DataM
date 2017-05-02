package com.winchannel.utils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBUtil {
	
	/**
	 * 关闭连接
	 */
	public static void closeDbResources(Connection conn,Statement stmt,ResultSet rs){
		if(rs!=null){
			try{
				rs.close();
			}catch(SQLException e){
				e.printStackTrace();
			}
			rs=null;
		}
		if(stmt!=null){
			try{
				stmt.close();
			}catch(SQLException e){
				e.printStackTrace();
			}
			stmt=null;
		}
		if(conn!=null){
			try{
				conn.close();
			}catch(SQLException e){
				e.printStackTrace();
			}
			conn=null;
		}
	}
	
	
	
	
	
}
