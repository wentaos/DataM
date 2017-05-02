package com.winchannel.utils;


import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.winchannel.task.Memory;

/**
 * Author songxudong
 * 操作 记录了当前操作信息的prop文件
 */
public class PropUtil {

    private static Logger logger = Logger.getLogger(PropUtil.class);

    private static Properties prop = new Properties();

    /**每操作一组Photo的结束ID*/
    public static Long MARK_PHOTO_ID = currentPhotoId();

    /** 需要处理得路径 */
    public static String PHOTO_PATH = photoPath();
    
    /** 定时任务时长*/
    public static Long RUN_TASK_TIME_LEN = Long.parseLong(getValue(Constant.RUN_TASK_TIME_LEN));

    
    /**
     * 线程操作ID相关
     */
    public static String T1_START_ID = getValue(Constant.T1_START_ID);
    public static String T1_END_ID = getValue(Constant.T1_END_ID);
    public static String T1_CURR_ID = getValue(Constant.T1_CURR_ID);
    
    public static String T2_START_ID = getValue(Constant.T2_START_ID);
    public static String T2_END_ID = getValue(Constant.T2_END_ID);
    public static String T2_CURR_ID = getValue(Constant.T2_CURR_ID);
    
    // 当前操作的最大ID
    public static String CURR_MAX_ID = getValue(Constant.CURR_MAX_ID);
    
    // 一个线程一次处理的ID数
    public static Long REDUCE_ID_NUM = Long.parseLong(getValue(Constant.REDUCE_ID_NUM).trim());
    
    
    
    

    
    /*****************************************************************************************/
    
    public static List<Long> groupIdPool(String TNo){
    	if(Constant.T1.equals(TNo)){
    		// 清空ID_POOL
        	Memory.T1_ID_POOL.clear();
        	
        	
        	
        	return Memory.T1_ID_POOL;
    	} else if(Constant.T2.equals(TNo)){
    		// 清空ID_POOL
        	Memory.T2_ID_POOL.clear();
        	
        	
        	
        	return Memory.T2_ID_POOL;
    	}
    	
    	return null;
    }
    
    
    /**
     * 根据Prop中的ID信息组装 MARK_MAP
     */
    public static Map<String,Long> groupMarkMap(){
    	Map<String,Long> MARK_MAP = new HashMap<String,Long>();
    	MARK_MAP.put(Constant.T1_START_ID, Long.parseLong(T1_START_ID));
    	MARK_MAP.put(Constant.T1_END_ID, Long.parseLong(T1_END_ID));
    	MARK_MAP.put(Constant.T1_CURR_ID, Long.parseLong(T1_CURR_ID));
    	
    	MARK_MAP.put(Constant.T2_START_ID, Long.parseLong(T2_START_ID));
    	MARK_MAP.put(Constant.T2_END_ID, Long.parseLong(T2_END_ID));
    	MARK_MAP.put(Constant.T2_CURR_ID, Long.parseLong(T2_CURR_ID));
    	return MARK_MAP;
    }
    
    
    
    private static Long currentPhotoId() {
       try{
           String propId = getValue(Constant.MARK_PHOTO_ID);
           Long markId =Long.parseLong(propId);
           return markId;
       }catch (Exception e){
           e.printStackTrace();
           return null;
       }
    }

    public static String photoPath(){
        try{
            String PHOTO_PATH = getValue(Constant.PHOTO_PATH);
            return PHOTO_PATH;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static String getValue(String key){
        String value = null;
       try{
           logger.debug("Get value photo option properties");
           InputStream in = PropUtil.class.getClassLoader().getResourceAsStream("spring/config/option.properties");
           prop.load(in);
           value = prop.getProperty(key);
       }catch (Exception e){
           e.printStackTrace();
       }
        return value;
    }

    public static boolean setProp(String key,String value,String comm){
        try{
            logger.debug("Set value photo option properties");
            String path  =PropUtil.class.getClassLoader().getResource("spring/config/option.properties").getPath();
            OutputStream out = new FileOutputStream(path,false);
            prop.setProperty(key,value);
            prop.store(out,comm);
            out.close();
            return true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public static boolean setProp(String key,String value){
        try{
            logger.debug("Set value photo option properties");
            String path  =PropUtil.class.getClassLoader().getResource("spring/config/option.properties").getPath();
            OutputStream out = new FileOutputStream(path,false);
            prop.setProperty(key,value);
            prop.store(out,"comm:"+key);
            out.close();
            return true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

}
