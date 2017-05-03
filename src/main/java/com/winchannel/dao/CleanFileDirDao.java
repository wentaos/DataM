package com.winchannel.dao;

import java.sql.SQLException;
import java.util.List;

import com.winchannel.bean.Photo;

public interface CleanFileDirDao {

    /**
     * 根据当前记录的markid获取下一个Photo
     */
    Photo selectPhotoOne(Long id) throws SQLException;

    List<Photo> selectPhotoList();

    String getFuncCodeByImgId(String imgId);

    int updatePhoto(Photo photo);

    Long selectPhotoMinId();
    
    Long selectPhotoMaxId();
    
    /**
     * 根据起始ID 和 prop配置中的 REDUCE_ID_NUM 得到下一组 ID_POOL
     */
    List<Long> getNextIdPoolByStartID(Long startID);

	void updatePhotoImgId(Long ID);
	
	void insertPhoto(Photo photo);

	void inserFuncCodeTable(String funcCodeTable,Photo photo);

}
