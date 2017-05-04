package com.winchannel.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.winchannel.bean.Photo;
import com.winchannel.dao.PropDao;
import com.winchannel.utils.Constant;
import com.winchannel.utils.DBUtil;
import com.winchannel.utils.PropUtil;

@Repository
public class PropDaoImpl implements PropDao{

	private static Logger logger = Logger.getLogger(CleanFileDirDaoImpl.class);

    @Value("${spring.datasource.url}")
    private String dbUrl;
    @Value("${spring.datasource.driverClassName}")
    private String driver;
    @Value("${spring.datasource.username}")
    private String userName;
    @Value("${spring.datasource.password}")
    private String passWord;
    
    
	@Override
	public void insertProp(Photo prop) {
		Connection conn = DBUtil.getConnection(driver, dbUrl, userName, passWord);
		PreparedStatement pstmt;
		String table_name = PropUtil.IS_TEST ? "VISIT_PHOTO_T" : "VISIT_PHOTO";
		table_name="VISIT_PHOTO";
		
		try {
			// 得到最大ID
//			Long maxId = selectMaxId();
			
			String sql = "INSERT INTO " + table_name + "(ID,IMG_ID,IMG_URL) VALUES(?,?,?)";
			if(PropUtil.IS_TEST){
				sql = "INSERT INTO " + table_name + "(ID,IMG_ID,IMG_URL) VALUES(?,?,?)";
			}
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setLong(1, 999999999);// 这里使用9位最大值
			pstmt.setString(2, prop.getImgId());
			pstmt.setString(3, prop.getImgUrl());
			
			pstmt.executeUpdate();
			
			logger.info("INSERT PROP OBJECT SUCCESS!");
			
			DBUtil.closeDbResources(conn, pstmt, null);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public Long selectMaxId(){
		Connection conn = DBUtil.getConnection(driver, dbUrl, userName, passWord);
		PreparedStatement pstmt;
		ResultSet rs;
		try {
			String sql = "SELECT MAX(ID) ID FROM VISIT_PHOTO";
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			if(rs.next()){
				Long maxId = rs.getLong("ID");
				return maxId;
			}
			DBUtil.closeDbResources(conn, pstmt, null);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0L;
	}
	

	@Override
	public void updateProp(Photo prop) {
		Connection conn = DBUtil.getConnection(driver, dbUrl, userName, passWord);
		PreparedStatement pstmt;
		String table_name = PropUtil.IS_TEST ? "VISIT_PHOTO_T" : "VISIT_PHOTO";
		table_name="VISIT_PHOTO";
		
		try {
			String sql = "UPDATE " + table_name + " SET IMG_URL=? WHERE IMG_ID=?";
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1,prop.getImgUrl());
			pstmt.setString(2,prop.getImgId());
			pstmt.executeUpdate();
			
			logger.info("UPDATE PROP OBJECT SUCCESS!");
			
			DBUtil.closeDbResources(conn, pstmt, null);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	

	@Override
	public Photo selectProp() {
		Connection conn = DBUtil.getConnection(driver,dbUrl,userName,passWord);
        PreparedStatement pstmt;
        Photo photo = null;
        String table_name = PropUtil.IS_TEST?"VISIT_PHOTO_T":"VISIT_PHOTO";
        table_name="VISIT_PHOTO";
        
        try {
        	String queryCol = "ID,IMG_ID,IMG_URL,ABSOLUTE_PATH";
            String sql = "SELECT "+queryCol+" FROM "+table_name+" WHERE IMG_ID=?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1,Constant.GET_PROP_IMG_ID);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                photo = new Photo();
                photo.setId(rs.getLong("ID"));
                photo.setImgId(rs.getString("IMG_ID"));
                photo.setImgUrl(rs.getString("IMG_URL"));
                photo.setImgAbsPath(rs.getString("ABSOLUTE_PATH"));
            }
            
            logger.info("SELECT PROP OBJECT SUCCESS!");
            
            DBUtil.closeDbResources(conn, pstmt, rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return photo;
	}

}
