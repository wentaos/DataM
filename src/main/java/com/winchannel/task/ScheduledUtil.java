package com.winchannel.task;

import com.winchannel.bean.Photo;
import com.winchannel.utils.Constant;

public class ScheduledUtil {
	
	
	/**
	 * 判断是否是程序挂掉后重启运行
	 */
	public static boolean isExistMemoryData(){
		Long T1_CURR_ID = Memory.MARK_MAP.get(Constant.T1_CURR_ID);
		// 内存中没有数据
		if(T1_CURR_ID==null || T1_CURR_ID==0){
			return true;
		}
		return false;
	}
	
	
	/**
		PropUtil.setProp(Constant.T1_START_ID,Memory.T1_ID_POOL.get(0)+"");
		PropUtil.setProp(Constant.T1_CURR_ID,Memory.T1_ID_POOL.get(0)+"");
		PropUtil.setProp(Constant.T1_END_ID,Memory.T1_ID_POOL.get(Memory.T1_ID_POOL.size()-1)+"");
		
		PropUtil.setProp(Constant.T2_START_ID,Memory.T2_ID_POOL.get(0)+"");
		PropUtil.setProp(Constant.T2_CURR_ID,Memory.T2_ID_POOL.get(0)+"");
		PropUtil.setProp(Constant.T2_END_ID,Memory.T2_ID_POOL.get(Memory.T2_ID_POOL.size()-1)+"");
		
		// 多个线程操作的最大ID
		// PropUtil.setProp(Constant.CURR_MAX_ID,Memory.T2_ID_POOL.get(Memory.T2_ID_POOL.size()-1)+"");
	 */
	public static Photo groupFirstProp(){
    	StringBuffer img_url_buf = new StringBuffer();
		img_url_buf
			// T1线程
			.append(Memory.T1_ID_POOL.get(0)).append("-")// T1_START_ID
			.append(Memory.T1_ID_POOL.get(0)).append("-")// T1_CURR_ID
			.append(Memory.T1_ID_POOL.get(Memory.T1_ID_POOL.size()-1)).append("-")// T1_END_ID
			// T2 线程
			.append(Memory.T2_ID_POOL.get(0)).append("-")// T2_START_ID
			.append(Memory.T2_ID_POOL.get(0)).append("-")// T2_CURR_ID
			.append(Memory.T2_ID_POOL.get(Memory.T2_ID_POOL.size()-1)).append("-")// T2_END_ID
			// MAX_ID
			.append(Memory.T2_ID_POOL.get(Memory.T2_ID_POOL.size()-1)) ;// CURR_MAX_ID
		
		Photo prop = new Photo();
		prop.setImgId(Constant.GET_PROP_IMG_ID);
		prop.setImgUrl(img_url_buf.toString());
		return prop;
    }
    
	public static void groupFirstMarkMap(){
		Memory.MARK_MAP.put(Constant.T1_START_ID, Memory.T1_ID_POOL.get(0));
		Memory.MARK_MAP.put(Constant.T1_CURR_ID, Memory.T1_ID_POOL.get(0));
		Memory.MARK_MAP.put(Constant.T1_END_ID, Memory.T1_ID_POOL.get(Memory.T1_ID_POOL.size()-1));
		
		Memory.MARK_MAP.put(Constant.T2_START_ID, Memory.T2_ID_POOL.get(0));
		Memory.MARK_MAP.put(Constant.T2_CURR_ID, Memory.T2_ID_POOL.get(0));
		Memory.MARK_MAP.put(Constant.T2_END_ID, Memory.T2_ID_POOL.get(Memory.T2_ID_POOL.size()-1));
		
		// 做个线程操作最终的最大ID
		Memory.MARK_MAP.put(Constant.CURR_MAX_ID, Memory.T2_ID_POOL.get(Memory.T2_ID_POOL.size()-1));
	}
	
	
	
	
	
	/**
	 * 记录线程的ID相关信息到MARK_MAP
	 * 这里主要记录 T_CURR_ID
	 * T_START_ID 和 T_END_ID 在资源分配的时候保存
	 */
	public static void markIdInfo2_MarkMap(Thread currentT,Long curr_id){
		String threadName = currentT.getName();
		if(Constant.T1.equals(threadName)){
			Memory.MARK_MAP.put(Constant.T1_CURR_ID, curr_id);
			
		} else if(Constant.T2.equals(threadName)){
			Memory.MARK_MAP.put(Constant.T2_CURR_ID, curr_id);
			
		}
	}
	
    
}
