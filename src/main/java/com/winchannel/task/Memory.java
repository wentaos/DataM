package com.winchannel.task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 系统内存临时存储信息
 */
public class Memory {
	
	/**
     * 线程中使用到的待处理的ID
     * 判断的话：这里我们只能知道处理的起始ID信息
     */
    public static List<Long> T1_ID_POOL = new ArrayList<Long>();
    public static List<Long> T2_ID_POOL = new ArrayList<Long>();
    
    /**
     * 存放线程处理的ID位置：包括 起始ID、当前ID
     * KEY NAME规则：T1_START_ID、T1_END_ID、T1_CURR_ID 这是线程1 需要处理的开始、结束和当前ID
     * 刚分配好MAP时，当前ID和开始ID相等
     * 一个MAP迭代后，当前ID和结束ID相等
     */
    public static Map<String,Long> MARK_MAP = new HashMap<String,Long>();
    

}
