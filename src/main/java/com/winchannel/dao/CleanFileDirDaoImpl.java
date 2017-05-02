package com.winchannel.dao;

import com.winchannel.bean.Photo;
import com.winchannel.utils.PropUtil;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


@Repository
public class CleanFileDirDaoImpl implements CleanFileDirDao {
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
	public List<Long> getNextIdPoolByStartID(Long startID) {
		
		Connection conn = this.getConnection();

        PreparedStatement pstmt;
        
        Long reduce_num = PropUtil.REDUCE_ID_NUM;
        
        Long flag_id = reduce_num+startID;
        
        List<Long> id_pool = new ArrayList<Long>();
        
        try {
            String sql = "SELECT ID from VISIT_PHOTO where ID<? ORDER BY ID";
            pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, flag_id);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
            	Long id = rs.getLong("ID");
            	id_pool.add(id);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
		
		return id_pool;
	}
	
	
    public Photo selectPhotoOne(Long id) throws SQLException {
        Connection conn = this.getConnection();

        PreparedStatement pstmt;
        Photo photo = null;
        try {
            String sql = "SELECT * FROM VISIT_PHOTO_T WHERE id=?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, id);
            ResultSet rs = pstmt.executeQuery();
            //int col = rs.getMetaData().getColumnCount();

            if (rs.next()) {
                photo = new Photo();
                photo.setId(rs.getLong("ID"));
                photo.setImgId(rs.getString("IMG_ID"));
                photo.setImgUrl(rs.getString("IMG_URL"));
                photo.setImgAbsPath(rs.getString("ABSOLUTE_PATH"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return photo;
    }

    public List<Photo> selectPhotoList() {
        Connection conn = this.getConnection();
        PreparedStatement pstmt;
        List<Photo> photoList = new ArrayList<Photo>();
        try {
            // 按照 ID 排序得到第一个
            String oneSql = "SELECT top 1 ID,IMG_ID,IMG_URL,ABSOLUTE_PATH from VISIT_PHOTO_T ORDER BY ID";
            pstmt = conn.prepareStatement(oneSql);
            ResultSet rs = pstmt.executeQuery();
            //int col = rs.getMetaData().getColumnCount();

            while (rs.next()) {
                Photo photo = new Photo();
                photo.setId(rs.getLong("ID"));
                photo.setImgId(rs.getString("IMG_ID"));
                photo.setImgUrl(rs.getString("IMG_URL"));
                photo.setImgAbsPath(rs.getString("ABSOLUTE_PATH"));
                photoList.add(photo);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return photoList;
    }

    @Override
    public String getFuncCodeByImgId(String imgId) {
        String[] tabNames = new String[]{"VISIT_INOUT_STORE", "MS_VISIT_ACVT", "VISIT_DIST_RULE", "VISIT_SEC_DISP"};
        String funcCode = "";
        try {
            logger.info("遍历FUNC_CODE相关的表数组 START ...");
            for (String TABLE_NAME : tabNames) {
                // 这里先用于测试
                funcCode = "FAC_123";// selectFuncCodeFrom(TABLE_NAME, imgId);
                if (funcCode != null && funcCode.length() > 0) {
                    logger.info("获取到对应的 FUNC_CODE：FUNC_CODE=" + funcCode);
                    break;
                }
            }
            return funcCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int updatePhoto(Photo photo) {

        Connection conn = this.getConnection();
        PreparedStatement pstmt;
        // 设置事务为非自动提交
        try{
            // 设置手动处理事务
            conn.setAutoCommit(false);
            String sql  ="UPDATE VISIT_PHOTO_T SET IMG_URL=?,ABSOLUTE_PATH=? WHERE ID=?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1,photo.getImgUrl());
            pstmt.setString(2,photo.getImgAbsPath());
            pstmt.setLong(3,photo.getId());
            int record = pstmt.executeUpdate();
            conn.commit();// 提交Update
            logger.info("Update Success! 事务提交！");
            return record;
        }catch (Exception e){
            logger.error("Dao Update Photo Error!");
            try{
                logger.info("Update 回滚！");
                conn.rollback();
            }catch (SQLException sqle){
                sqle.printStackTrace();
            }
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public Long selectPhotoMaxId() {
        Connection conn = this.getConnection();
        PreparedStatement pstmt;
        try{
            String sql = "SELECT top 1 max(ID) ID FROM VISIT_PHOTO_T";
            pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            if(rs.next()){
                Long ID = rs.getLong("ID");
                if (ID!=null){
                    return ID;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }


    public String selectFuncCodeFrom(String TABLE_NAME, String imgId) {
        Connection conn = this.getConnection();
        PreparedStatement pstmt;
        String funcCode = "";
        try {
            String sql = "SELECT FUNC_CODE FROM " + TABLE_NAME + " WHERE IMG_ID=?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, imgId);
            logger.info("查询表" + TABLE_NAME + " 中 FUNC_CODE ... BY IMG_ID=" + imgId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                funcCode = rs.getString("FUNC_CODE");
                logger.info("获取到FUNC_CODE = " + funcCode);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return funcCode;
    }




    private Connection getConnection() {
        Connection conn = null;
        try {
            Class.forName(driver);
            conn = DriverManager.getConnection(dbUrl, userName, passWord);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }

	@Override
	public Long selectPhotoMinId() {
		 Connection conn = this.getConnection();
	        PreparedStatement pstmt;
	        try{
	            String sql = "SELECT top 1 min(ID) ID FROM VISIT_PHOTO_T";
	            pstmt = conn.prepareStatement(sql);
	            ResultSet rs = pstmt.executeQuery();
	            if(rs.next()){
	                Long ID = rs.getLong("ID");
	                if (ID!=null){
	                    return ID;
	                }
	            }
	        }catch (Exception e){
	            e.printStackTrace();
	        }
	        return null;
	}


}
