package com.winchannel.task;

import java.util.Map;

import com.winchannel.utils.Constant;
import com.winchannel.utils.PropUtil;

public class ScheduledUtil {
	
	/**
	 * 检查是否是首次运行
	 */
	public static boolean isFirstRun(){
		String testTsartId = PropUtil.getValue(Constant.T1_START_ID);
		if(testTsartId==null || testTsartId.length()==0){
			return true;
		}
		return false;
	}
	
	/**
	 * 判断是否是程序挂掉后重启运行
	 */
	public static boolean isReRun(){
		if(!isFirstRun()){
			Long T1_CURR_ID = Memory.MARK_MAP.get(Constant.T1_CURR_ID);
			// 内存中没有数据
			if(T1_CURR_ID==null || T1_CURR_ID==0){
				return true;
			}
		}
		return false;
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
	
	 /**
     * 记录线程中的相关ID信息
     */
    public static void bakThreadMapIdInfo2Prop(Map<String,Long> MARK_MAP){
    	Long T1_START_ID = MARK_MAP.get(Constant.T1_START_ID);
    	Long T1_END_ID = MARK_MAP.get(Constant.T1_END_ID);
    	Long T1_CURR_ID = MARK_MAP.get(Constant.T1_CURR_ID);
    	
    	Long T2_START_ID = MARK_MAP.get(Constant.T2_START_ID);
    	Long T2_END_ID = MARK_MAP.get(Constant.T2_END_ID);
    	Long T2_CURR_ID = MARK_MAP.get(Constant.T2_CURR_ID);
    	
    	PropUtil.setProp(Constant.T1_START_ID, T1_START_ID+"");
    	PropUtil.setProp(Constant.T1_END_ID,T1_END_ID+"");
    	PropUtil.setProp(Constant.T1_CURR_ID,T1_CURR_ID+"");
    	PropUtil.setProp(Constant.T2_START_ID, T2_START_ID+"");
    	PropUtil.setProp(Constant.T2_END_ID,T2_END_ID+"");
    	PropUtil.setProp(Constant.T2_CURR_ID,T2_CURR_ID+"");  	
    }
	

}
