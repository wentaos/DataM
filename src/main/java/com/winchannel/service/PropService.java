package com.winchannel.service;

import java.util.Map;

import com.winchannel.bean.Photo;

public interface PropService {
	/***
	 * 下面是操作 PropDao的方法
	 */
	void insertProp(Photo prop);
    void updateProp(Photo prop);
    void updateByMap(Map<String, Long> infoMap);
    Map<String,Long> selectProp();
    
    
	void groupMemoryData();
}
