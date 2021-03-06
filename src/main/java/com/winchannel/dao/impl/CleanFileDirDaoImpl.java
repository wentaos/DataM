package com.winchannel.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.winchannel.bean.Photo;
import com.winchannel.dao.CleanFileDirDao;
import com.winchannel.utils.DBUtil;
import com.winchannel.utils.PropUtil;


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
		
		Connection conn = DBUtil.getConnection(driver,dbUrl,userName,passWord);

        PreparedStatement pstmt;
        
        Long reduce_num = PropUtil.REDUCE_ID_NUM;
        
        Long flag_id = reduce_num + startID;
        
        List<Long> id_pool = new ArrayList<Long>();
        
        String table_name = PropUtil.IS_TEST?"VISIT_PHOTO_T":"VISIT_PHOTO";
        
        try {
            String sql = "SELECT ID from "+table_name+" where ID>=? AND ID<= ? ORDER BY ID";
            pstmt = conn.prepareStatement(sql);
            
            pstmt.setLong(1, startID);// 开始ID
            pstmt.setLong(2, flag_id);// 结束ID
            
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
            	Long id = rs.getLong("ID");
            	id_pool.add(id);
            }

            DBUtil.closeDbResources(conn, pstmt, rs);
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
		
		return id_pool;
	}
	
	
    public Photo selectPhotoOne(Long id) throws SQLException {
        Connection conn = DBUtil.getConnection(driver,dbUrl,userName,passWord);

        PreparedStatement pstmt;
        Photo photo = null;
        String table_name = PropUtil.IS_TEST?"VISIT_PHOTO_T":"VISIT_PHOTO";
        try {
            String sql = "SELECT * FROM "+table_name+" WHERE id=?";
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
            DBUtil.closeDbResources(conn, pstmt, rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return photo;
    }

    public List<Photo> selectPhotoList() {
        Connection conn = DBUtil.getConnection(driver,dbUrl,userName,passWord);
        PreparedStatement pstmt;
        List<Photo> photoList = new ArrayList<Photo>();
        String table_name = PropUtil.IS_TEST?"VISIT_PHOTO_T":"VISIT_PHOTO";
        try {
            // 按照 ID 排序得到第一个
        	String oneSql = "";
        	if(PropUtil.IS_MYSQL){
        		oneSql = "SELECT ID,IMG_ID,IMG_URL,ABSOLUTE_PATH from "+table_name+" ORDER BY ID LIMIT 1";
        	} else if (PropUtil.IS_SQLSERVER){
        		oneSql = "SELECT top 1 ID,IMG_ID,IMG_URL,ABSOLUTE_PATH from "+table_name+" ORDER BY ID";
        	}
            pstmt = conn.prepareStatement(oneSql);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Photo photo = new Photo();
                photo.setId(rs.getLong("ID"));
                photo.setImgId(rs.getString("IMG_ID"));
                photo.setImgUrl(rs.getString("IMG_URL"));
                photo.setImgAbsPath(rs.getString("ABSOLUTE_PATH"));
                photoList.add(photo);
            }
            DBUtil.closeDbResources(conn, pstmt, rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return photoList;
    }

    @Override
    public String getFuncCodeByImgId(String imgId) {
    	
        String[] tabNames = null;
        if(PropUtil.IS_TEST){
        	tabNames = new String[]{"VISIT_INOUT_STORE_T", "MS_VISIT_ACVT_T", "VISIT_DIST_RULE_T", "VISIT_SEC_DISP_T"};
        } else {
        	tabNames = new String[]{"VISIT_INOUT_STORE", "MS_VISIT_ACVT", "VISIT_DIST_RULE", "VISIT_SEC_DISP"};
        }
        		
        String funcCode = "";
        try {
            logger.info("遍历FUNC_CODE相关的表数组 START ...");
            for (String TABLE_NAME : tabNames) {
                // 这里先用于测试
                funcCode = selectFuncCodeFrom(TABLE_NAME, imgId);
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

    
    public String selectFuncCodeFrom(String TABLE_NAME, String imgId) {
        Connection conn = DBUtil.getConnection(driver,dbUrl,userName,passWord);
        PreparedStatement pstmt;
        String funcCode = "";
        try {
        	String IMG_ID_COL_NAME = "IMG_IDX";
        	String MS_VISIT_ACVT = "MS_VISIT_ACVT";
        	 if(PropUtil.IS_TEST){
        		 MS_VISIT_ACVT = "MS_VISIT_ACVT_T";
        	 }
        	
        	if(MS_VISIT_ACVT.toLowerCase().equals(TABLE_NAME.toLowerCase())){
        		IMG_ID_COL_NAME = "IMG_ID";
        	}
        	
            String sql = "SELECT FUNC_CODE FROM " + TABLE_NAME + " WHERE "+IMG_ID_COL_NAME+"=?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, imgId);
            logger.info("查询表" + TABLE_NAME + " 中 FUNC_CODE ... BY "+IMG_ID_COL_NAME+"=" + imgId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                funcCode = rs.getString("FUNC_CODE");
                logger.info("获取到FUNC_CODE = " + funcCode);
            }
            DBUtil.closeDbResources(conn, pstmt, rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return funcCode;
    }

    
    
    @Override
    public int updatePhoto(Photo photo) {

        Connection conn = DBUtil.getConnection(driver,dbUrl,userName,passWord);
        PreparedStatement pstmt;
        
        String table_name = PropUtil.IS_TEST?"VISIT_PHOTO_T":"VISIT_PHOTO";
        
        // 设置事务为非自动提交
        try{
            // 设置手动处理事务
            conn.setAutoCommit(false);
            String sql  ="UPDATE "+table_name+" SET IMG_URL=?,ABSOLUTE_PATH=? WHERE ID=?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1,photo.getImgUrl());
            pstmt.setString(2,photo.getImgAbsPath());
            pstmt.setLong(3,photo.getId());
            int record = pstmt.executeUpdate();
            conn.commit();// 提交Update
            logger.info("Update Success! 事务提交！");
            DBUtil.closeDbResources(conn, pstmt, null);
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
        Connection conn = DBUtil.getConnection(driver,dbUrl,userName,passWord);
        PreparedStatement pstmt;
        
        String table_name = PropUtil.IS_TEST?"VISIT_PHOTO_T":"VISIT_PHOTO";
        
        try{
            String oneSql = "SELECT top 1 max(ID) ID FROM "+table_name+" WHERE IMG_ID!='GET_PROP_IMG_ID'";
        	if(PropUtil.IS_MYSQL){
        		oneSql = "SELECT max(ID) ID FROM "+table_name+" WHERE IMG_ID!='GET_PROP_IMG_ID' LIMIT 1";
        	} else if (PropUtil.IS_SQLSERVER){
        		oneSql = "SELECT top 1 max(ID) ID FROM "+table_name+" WHERE IMG_ID!='GET_PROP_IMG_ID'";
        	}
        	
            pstmt = conn.prepareStatement(oneSql);
            ResultSet rs = pstmt.executeQuery();
            if(rs.next()){
                Long ID = rs.getLong("ID");
                if (ID!=null){
                    return ID;
                }
            }
            DBUtil.closeDbResources(conn, pstmt, rs);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }


	@Override
	public Long selectPhotoMinId() {
		Connection conn = DBUtil.getConnection(driver,dbUrl,userName,passWord);
        PreparedStatement pstmt;
        
        String table_name = PropUtil.IS_TEST?"VISIT_PHOTO_T":"VISIT_PHOTO";
        
        try{
            String sql = "";
            if(PropUtil.IS_MYSQL){
            	sql = "SELECT min(ID) ID FROM "+table_name+" LIMIT 1";
        	} else if (PropUtil.IS_SQLSERVER){
        		sql = "SELECT top 1 min(ID) ID FROM "+table_name+"";
        	}
            pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            if(rs.next()){
                Long ID = rs.getLong("ID");
                if (ID!=null){
                    return ID;
                }
            }
            DBUtil.closeDbResources(conn, pstmt, rs);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
	}


	@Override
	public void updatePhotoImgId(Long ID) {
		Connection conn = DBUtil.getConnection(driver,dbUrl,userName,passWord);
        PreparedStatement pstmt;
        String table_name = PropUtil.IS_TEST?"VISIT_PHOTO_T":"VISIT_PHOTO";
        try{
            String sql = "update "+table_name+" set IMG_ID='img_id_ok' WHERE ID=?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, ID);
            pstmt.executeUpdate();
            DBUtil.closeDbResources(conn, pstmt, null);
        }catch (Exception e){
            e.printStackTrace();
        }
	}

    
    /**
     * 用于测试
     */
	@Override
	public void insertPhoto(Photo photo) {
		Connection conn = DBUtil.getConnection(driver,dbUrl,userName,passWord);
        PreparedStatement pstmt;
        String table_name = PropUtil.IS_TEST?"VISIT_PHOTO_T":"VISIT_PHOTO";
        try{
            String photoSql = "insert into "+table_name+"(IMG_ID,IMG_URL,ABSOLUTE_PATH)"+" VALUES(?,?,?)";
            pstmt = conn.prepareStatement(photoSql);
//            pstmt.setLong(1, photo.getId());
            pstmt.setString(1, photo.getImgId());
            pstmt.setString(2,photo.getImgUrl());
            pstmt.setString(3,photo.getImgAbsPath());
            pstmt.executeUpdate();
            
            DBUtil.closeDbResources(conn, pstmt, null);
        }catch (Exception e){
            e.printStackTrace();
        }
	}
	/**
	 * 用于测试
	 */
	@Override
	public void inserFuncCodeTable(String funcCodeTable,Photo photo){
		Connection conn = DBUtil.getConnection(driver,dbUrl,userName,passWord);
        PreparedStatement pstmt;
        try{
        	String IMG_ID_COL_NAME = "IMG_IDX";
        	if(funcCodeTable.toLowerCase().contains("MS_VISIT_ACVT".toLowerCase())){
        		IMG_ID_COL_NAME = "IMG_ID";
        	}
        	
            String funcCodeSql = "insert into "+funcCodeTable+"(ID,"+IMG_ID_COL_NAME+",FUNC_CODE"+") VALUES(?,?,?)";
            pstmt = conn.prepareStatement(funcCodeSql);
            pstmt.setLong(1,photo.getId());
            pstmt.setString(2, photo.getImgId());
            pstmt.setString(3, photo.getFuncCode());
            pstmt.executeUpdate();
            
            DBUtil.closeDbResources(conn, pstmt, null);
        }catch (Exception e){
            e.printStackTrace();
        }
	}
	
	
	
	
}
