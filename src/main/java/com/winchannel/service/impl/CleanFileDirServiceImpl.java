package com.winchannel.service.impl;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.winchannel.bean.Photo;
import com.winchannel.dao.CleanFileDirDao;
import com.winchannel.dao.PropDao;
import com.winchannel.service.CleanFileDirService;


@Service
public class CleanFileDirServiceImpl implements CleanFileDirService {

    @Autowired
    private CleanFileDirDao cleanFileDirDao;
    @Autowired
    private PropDao propDao;
    

    @Override
    public Photo getPhotoOne(Long id) throws SQLException {
        Photo photo = cleanFileDirDao.selectPhotoOne(id);
        return photo;
    }

    @Override
    public Photo getFirstPhotoOne() {
        List<Photo> photoList = cleanFileDirDao.selectPhotoList();
        if(photoList!=null && photoList.size()>0){
            return photoList.get(0);
        }
        return null;
    }

    @Override
    public String getFuncCodeByPhoto(Photo photo) {
        String fc = null;
        if(photo!=null){
            String imgId = photo.getImgId();
            fc = cleanFileDirDao.getFuncCodeByImgId(imgId);
        }
        return fc;
    }

    @Override
    public boolean updatePhoto(Photo photo) {
        try{
            int record = cleanFileDirDao.updatePhoto(photo);
            if(record>=1){
                return true;
            }
            return false;
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public Long getPhotoMaxId() {
        try{
            Long maxId = cleanFileDirDao.selectPhotoMaxId();
            return maxId;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Long getPhotoMinId() {
        try{
            Long maxId = cleanFileDirDao.selectPhotoMinId();
            return maxId;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }


	/**
	 * 
	 */
	@Override
	public List<Long> getNextIdPoolByStartID(Long startID) {
		return cleanFileDirDao.getNextIdPoolByStartID(startID);
	}

	@Override
	public void updatePhotoImgId(Long ID) {
		cleanFileDirDao.updatePhotoImgId(ID);
	}

	


}
