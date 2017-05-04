package com.winchannel.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.winchannel.bean.Photo;
import com.winchannel.dao.PropDao;
import com.winchannel.service.PropService;
import com.winchannel.task.Memory;
import com.winchannel.utils.Constant;

@Service
public class PropServiceImpl implements PropService{
	
	@Autowired
    private PropDao propDao;
	
	@Override
	public void insertProp(Photo prop) {
		propDao.insertProp(prop);		
	}

	@Override
	public void updateProp(Photo prop) {
		propDao.updateProp(prop);		
	}

	@Override
	public Map<String,Long> selectProp() {
		Map<String,Long> infoMap = new HashMap<String,Long>();
		
		Photo prop = propDao.selectProp();
		if(prop==null){
			return null;
		}
		String[] infos = this.parseInfos(prop.getImgUrl());
		if(infos==null){
			return null;
		}
		
		infoMap.put(Constant.T1_START_ID, Long.parseLong(infos[0]));
		infoMap.put(Constant.T1_CURR_ID, Long.parseLong(infos[1]));
		infoMap.put(Constant.T1_END_ID, Long.parseLong(infos[2]));
		infoMap.put(Constant.T2_START_ID, Long.parseLong(infos[3]));
		infoMap.put(Constant.T2_CURR_ID, Long.parseLong(infos[4]));
		infoMap.put(Constant.T2_END_ID, Long.parseLong(infos[5]));
		infoMap.put(Constant.CURR_MAX_ID, Long.parseLong(infos[6]));
		
		return infoMap;
	}
	
	

	@Override
	public void groupMemoryData(Map<String, Long> infoMap) {
		// 先查询出prop数据
//		Map<String,Long> infoMap = selectProp();
		
		/**处理ID_POOL*/
		Memory.T1_ID_POOL.clear();
		Memory.T2_ID_POOL.clear();
		
		Long t1StartId = infoMap.get(Constant.T1_CURR_ID);
		Long t1EndId = infoMap.get(Constant.T1_END_ID);
		
		for(Long id=t1StartId;id<=t1EndId;id++){
			Memory.T1_ID_POOL.add(id);
		}
		
		Long t2StartId = infoMap.get(Constant.T2_CURR_ID);
		Long t2EndId = infoMap.get(Constant.T2_END_ID);
		
		for(Long id=t2StartId;id<=t2EndId;id++){
			Memory.T2_ID_POOL.add(id);
		}
		
		/**后面是MARK_MAP*/
		Memory.MARK_MAP = infoMap;
		
	}

	
	/**
	 * 分解prop信息
	 */
	private String[] parseInfos(String propInfo) {
		if(propInfo!=null && propInfo.trim().length()>0){
			String[] infos = propInfo.split("-");
			return infos;
		}
		return null;
	}

	@Override
	public void updateByMap(Map<String, Long> infoMap) {
		Photo prop = new Photo();
		StringBuffer img_url_buf = new StringBuffer();
		img_url_buf
			// T1线程
			.append(infoMap.get(Constant.T1_START_ID)).append("-")// T1_START_ID
			.append(infoMap.get(Constant.T1_CURR_ID)).append("-")// T1_CURR_ID
			.append(infoMap.get(Constant.T1_END_ID)).append("-")// T1_END_ID
			// T2 线程
			.append(infoMap.get(Constant.T2_START_ID)).append("-")// T2_START_ID
			.append(infoMap.get(Constant.T2_CURR_ID)).append("-")// T2_CURR_ID
			.append(infoMap.get(Constant.T2_END_ID)).append("-")// T2_END_ID
			// MAX_ID
			.append(infoMap.get(Constant.CURR_MAX_ID)) ;// CURR_MAX_ID
		
		prop.setImgId(Constant.GET_PROP_IMG_ID);
		prop.setImgUrl(img_url_buf.toString());
		propDao.updateProp(prop);
	}

}
