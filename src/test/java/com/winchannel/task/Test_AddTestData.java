package com.winchannel.task;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Random;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.winchannel.bean.Photo;
import com.winchannel.dao.CleanFileDirDao;
import com.winchannel.utils.DateUtil;
import com.winchannel.utils.PropUtil;

@RunWith(SpringJUnit4ClassRunner.class)  
@ContextConfiguration({"classpath:spring/applicationContext.xml"})  
public class Test_AddTestData {

    @Resource CleanFileDirDao cleanFileDirDao;  
     
    @Test  
    public void addTestData(){
    	
    	// 图片文件处理目录
        String filePath = PropUtil.PHOTO_PATH;
    	
        // 需要复制的图片原文件
//        File sourceFile = new File("D:"+File.separator+"Photo_Test"+File.separator+"photo.jpg");
    	
        // 数据库拼接数据
        // ABSOLUTE_PATH : D:/Photo_Test/photos/2017-01-23/310.jpg；需要拼接 date,再拼接photoName
        
        // 起始日期
        String date = "2017-01-01";
        File datePath = new File(filePath + date + "/");
		datePath.mkdirs();
		
        // IMG_URL:后面拼接 date 然后再拼接 "dot2B",再拼接photoName
        String urlHead = "/media/Ddot1Adot2BPhoto_Testdot2Bphotosdot2B";
        
        // 每个日期目录创建100条图片记录
        int count = 1;
        int codeNum = 1;
        // 一次产生1000条数据和1000各图片文件
        for(Long i=1L;i<=7777;i++){
        	// 组装photoName
        	String photoName = i+".jpg";
        	
        	String ABSOLUTE_PATH = filePath + date + "/" + photoName;
        	
        	// 创建目录中的图片
//        	copyPhoto(sourceFile, ABSOLUTE_PATH);
        	
        	String IMG_URL = urlHead + date + "dot2B" + photoName;
        	
        	// FUNC_CODE
        	String funcCode = "FAC_"+codeNum;
        	
        	// 存库
        	Photo photo = new Photo();
        	photo.setId(i);
        	photo.setImgUrl(IMG_URL);
        	photo.setImgAbsPath(ABSOLUTE_PATH);
        	photo.setFuncCode(funcCode);
        	photo.setImgId("img_id_"+i);
        	cleanFileDirDao.insertPhoto(photo);
        	
        	String[] tabNames = new String[]{"VISIT_INOUT_STORE_T", "MS_VISIT_ACVT_T", "VISIT_DIST_RULE_T", "VISIT_SEC_DISP_T"};
        	String funcCodeTable = tabNames[new Random().nextInt(4)];
        	
        	// 随机将imgId和funcCode插入到对应的表中
        	cleanFileDirDao.inserFuncCodeTable(funcCodeTable,photo);
        	
        	count++;
        	// 累计100 更换新的日期文件目录
        	if(count==235){
        		codeNum++;
        		count=1;
        		date = DateUtil.getNextDate(date);
        		// 创建日期目录
        		datePath = new File(filePath + date + "/");
        		datePath.mkdirs();
        	}
        	
        }
        
    	
    }
    
    
    public void copyPhoto(File sourceFile, String destPath) {
        try {
            // 如果存在 再执行操作
            if (sourceFile.exists()) {
                InputStream in = new FileInputStream(sourceFile); //读入原文件
                OutputStream out = new FileOutputStream(destPath);
                byte[] buffer = new byte[1024 * 10];
                int len = 0;
                while ((len = in.read(buffer)) != -1) {
                    out.write(buffer, 0, len);
                }
                in.close();
                out.close();
            }

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

    }
    
    
} 