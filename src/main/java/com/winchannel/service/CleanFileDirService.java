package com.winchannel.service;


import java.sql.SQLException;
import java.util.List;

import com.winchannel.bean.Photo;

public interface CleanFileDirService {

    Photo getPhotoOne(Long id) throws SQLException;

    Photo getFirstPhotoOne();

    String getFuncCodeByPhoto(Photo photo);

    boolean updatePhoto(Photo photo);

    Long getPhotoMinId();
    
    Long getPhotoMaxId();
    
    List<Long> getNextIdPoolByStartID(Long startId);

	void updatePhotoImgId(Long ID);
    
}
