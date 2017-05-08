package com.winchannel.utils;


import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.junit.Test;

/**
 * Author songxudong
 * 操作 记录了当前操作信息的prop文件
 */
public class PropUtil {

    private static Logger logger = Logger.getLogger(PropUtil.class);

    private static String resourceFilePath = "spring/config/option.properties";
    
    private static Properties prop = new Properties();
    
    /** 需要处理得路径 */
    public static String PHOTO_PATH = photoPath();
    
    /** 定时任务时长*/
    public static Long RUN_TASK_TIME_LEN = Long.parseLong(getValue(Constant.RUN_TASK_TIME_LEN));

    // 一个线程一次处理的ID数
    public static Long REDUCE_ID_NUM = Long.parseLong(getValue(Constant.REDUCE_ID_NUM).trim());
    
    // 遍历多少次保存一次ID信息
    public static int LOOP_SAVE_COUNT = Integer.parseInt(getValue("LOOP_SAVE_COUNT"));
    
    // 线程数
    public static int THREA_NUM = Integer.parseInt(getValue("THREA_NUM"));
    
    // 是否测试：配合数据库使用
    public static boolean IS_TEST = "1".equals(getValue(Constant.IS_TEST))?true:false;
    
    // 判断是什么数据库
    public static boolean IS_MYSQL = Constant.DB_NAME_MYSQL.equals(getValue(Constant.DB_NAME))?true:false;
    public static boolean IS_SQLSERVER = Constant.DB_NAME_SQLSERVER.equals(getValue(Constant.DB_NAME))?true:false;
    
    
    /*****************************************************************************************/
    
    
    
    
    
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
           InputStream in = PropUtil.class.getClassLoader().getResourceAsStream(resourceFilePath);
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
            String path  =PropUtil.class.getClassLoader().getResource(resourceFilePath).getPath();
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
            String path  =PropUtil.class.getClassLoader().getResource(resourceFilePath).getPath();
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

    @Test
    public void sadsa(){
    	System.out.println(Long.MAX_VALUE);
    }
    
}
