package com.winchannel.dao;

import com.winchannel.bean.Photo;

/**
 * 使用 Photo对象保存ID、线程的相关信息到数据库中
 * 只是用一条记录
 */
public interface PropDao {
	/**
	 * 第一次运行时保存
	 */
	void insertProp(Photo prop);
	
	/**
	 * 修改里面的数据
	 */
	void updateProp(Photo prop);
	
	/**
	 * 获取报错ID、线程信息的Photo
	 * 使用一个固定的标识：img_id,这个属性值我们自己定义
	 */
	Photo selectProp();

	Long selectMaxId();
	
	
}
