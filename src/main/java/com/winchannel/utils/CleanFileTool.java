package com.winchannel.utils;

import com.winchannel.bean.Photo;
import org.apache.log4j.Logger;
import org.junit.Test;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 内容中：涉及到的路径是基于 D:/aaa/bbb/2017-02-12/j23h43h24234324h32ui4hf.jpg 这种格式
 * headPath         D:/aaa/bbb/
 * datePath         2017-02-12
 * dateFullPath     D:/aaa/bbb/2017-02-12
 */
public class CleanFileTool {
    /**
     * 这些静态字段可专用于 在获取到的pathMap 中当作key获取对应的path
     * 暂时用不到
     */
    @Deprecated
    public static String FILE_NAME_PATH = "FILE_NAME_PATH";
    @Deprecated
    public static String DATE_PATH = "DATE_PATH";
    @Deprecated
    public static String DATE_FULL_PATH = "DATE_FULL_PATH";
    @Deprecated
    public static String HEAD_PATH = "HEAD_PATH";

    private static Logger logger = Logger.getLogger(CleanFileTool.class);

    /**
     * 判断对应的 FUNC_CODE + DATE + photoname 是否存在
     * 如果存在就师傅和规则的，不用处理了
     */
    public static boolean isTruePath(Photo photo) {
    	String absPath = photo.getImgAbsPath();
    	Pattern pattern = Pattern.compile("\\d{4}-\\d{2}-\\d{2}");
    	Matcher matcher = pattern.matcher(absPath);
    	// absPath 如：D:/Photo_Test/photos/2017-01-12/101.jpg
    	if(matcher.find()){
    		String date = matcher.group();
    			String photos = absPath.split(date)[0];// 得到 D:/Photo_Test/photos/
    			String tail = photos.split("photos")[1];// 得到 /
    			// 如果长度够长，中间会包含FUNC_CODE目录
    			if(tail.length()>3){
    				return true;
    			}
    	}
        return false;
    }

    /**
     * 判断创建 FUNC_CODE 对应的目录
     */
    public static String cleanFuncCodePath(String FUNC_CODE) {
        String PHOTO_PATH = PropUtil.PHOTO_PATH;
        String funcCodePath = PHOTO_PATH + "/" + FUNC_CODE;
        funcCodePath = createPath(funcCodePath);
        return funcCodePath;
    }

    /**
     * 对日期目录进行处理
     */
    public static String cleanDatePath(String funcCodePath, String absolutePath) {
        String PHOTO_PATH = PropUtil.PHOTO_PATH;
        String datePath = getDatePathFromAbsolutionPath(absolutePath);
        String code_date_path = funcCodePath + "/" + datePath;// 这里获取到的是 相对路径
        code_date_path = createPath(PHOTO_PATH+ "/" +code_date_path);
        return code_date_path;
    }


    /**
     * 截取图片日期部分 2016-07-28
     *
     * @param \ D:/APP/SFA_demo/webapps/ROOT/photos/2016-07-28/b5f6ebf8-eb7f-44ef-bd5d-8843bcd74706.jpg
     */
    public static String getDatePathFromAbsolutionPath(String absolutePath) {
        if (absolutePath != null && absolutePath.length() > 0) {
            int lastP1 = absolutePath.lastIndexOf("/");
            String subPath = absolutePath.substring(0, lastP1);
            int lastP2 = subPath.lastIndexOf("/");
            String date = subPath.substring(lastP2 + 1);
            return date;
        }
        return null;
    }


    /**
     * 得到 headPath
     */
    public static String getHeadPath(String absolutePath) {
        if (absolutePath != null && absolutePath.length() > 0) {
            int lastP1 = absolutePath.lastIndexOf("/");
            String subPath = absolutePath.substring(0, lastP1);
            int lastP2 = subPath.lastIndexOf("/");
            String headPath = subPath.substring(0, lastP2);
            return headPath;
        }
        return null;
    }

    /**
     * 获取文件名部分apth
     */
    public static String getFileNamePath(String absolutePath) {
        if (absolutePath == null) {
            return null;
        }
        String fileNamePath = absolutePath.substring(absolutePath.lastIndexOf("/") + 1);
        return fileNamePath;
    }


    /**
     * 组装新的file 绝对路径
     * 加上 FUNC_CODE_PATH
     */
    public static String getNewAbsPath(String absolutePath, String funcCodePath) {
        String headPath = getHeadPath(absolutePath);
        String datePath = getDatePathFromAbsolutionPath(absolutePath);
        String fileNamePath = getFileNamePath(absolutePath);
        String newAbsPath = headPath + "/" + funcCodePath + "/" + datePath + "/" + fileNamePath;
        return newAbsPath;
    }

    /**
     * 得到新的 IMG_URL
     * IMG_URL: /media/Ddot1Adot2BAPPdot2BSFA_demodot2Bwebappsdot2BROOTdot2Bphotosdot2B2016-07-28dot2Bb5f6ebf8-eb7f-44ef-bd5d-8843bcd74706dot4Djpg.jpg
     */
    public static String getNewImgUrl(String oldImgUrl, String funcCodePath) {
        String newImgUrl = null;
        if (oldImgUrl != null) {
            Pattern p = Pattern.compile("\\d{4}-\\d{2}-\\d{2}");
            Matcher m = p.matcher(oldImgUrl);
            if (m.find()) {
                String datePath = m.group();
                // 替换
                newImgUrl = oldImgUrl.replace(datePath, funcCodePath + "dot2B" + datePath);
            }
        }
        return newImgUrl;
    }

    /**
     * 判断目录是否存在，不存在则创建
     */
    public static String createPath(String path) {
        File p = new File(path);
        boolean flag = p.exists() && p.isDirectory();
        if (!flag) {// 不存在该目录
            p.mkdirs();
        }
        return path;
    }

    
   

    /**
     * 剪切文件
     */
    public static boolean movePhoto(String sourcePath, String destPath) {
    	
    	String sysSourcePath = sourcePath.replace("/", File.separator);// 换成系统的分隔符
    	String sysDestPath = destPath.replace("/", File.separator);// 换成系统的分隔符
    	
        // 复制0
        boolean copyOk = copyPhoto(sysSourcePath, sysDestPath);
        try {
            if (copyOk) {
                // 删除源文件
                deletePhoto(sysSourcePath);
            }
            // eg: D:/aaa/2017-02-23
            String dateFullPath = sysSourcePath.substring(0, sysSourcePath.lastIndexOf(File.separator));
            // 检查对应的原日期目录中是否还有图片，没有图片，删除整个日期目录
            if (isEmptyPath(dateFullPath)) {
                // 删除日期目录
                new File(dateFullPath).delete();
                return true;
            } else {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * 检查目录中是否还存在文件
     */
    public static boolean isEmptyPath(String checkPath) {
        File path = new File(checkPath);
        String[] fileList = null;
        // 检查目录合法性
        if (checkPath != null && path.isDirectory()) {
            fileList = path.list();
        }
        // 没有文件
        if (fileList == null || fileList.length == 0) {
            return true;
        }
        return false;
    }


    /**
     * 复制单个文件
     */
    public static boolean copyPhoto(String sourcePath, String destPath) {
        logger.info("开始复制Photo ...");
        try {
            File photo = new File(sourcePath);

            // 判断photo是否是一个存在的文件，防止发生"系统找不到指定的路径"
            if(!photo.exists()){
                return false;
            }

            // 如果存在 再执行操作
            if (photo.exists()) {
                InputStream in = new FileInputStream(sourcePath); //读入原文件
                OutputStream out = new FileOutputStream(destPath);
                byte[] buffer = new byte[1024 * 10];
                int len = 0;
                while ((len = in.read(buffer)) != -1) {
                    out.write(buffer, 0, len);
                }
                in.close();
                out.close();
                logger.info("复制Photo完成 ...");
                logger.info("文件大小 Length ：" + photo.length());
            }
            
            return true;
        } catch (IOException ioe) {
            logger.error("复制Photo失败 ...");
            ioe.printStackTrace();
            return false;
        }

    }


    /**
     * 文件删除
     */
    public static boolean deletePhoto(String sourcePath) {
        File photo = new File(sourcePath);
        if (photo.exists()) {
            System.out.print(sourcePath);
            photo.delete();
            return true;
        } else if (!photo.isAbsolute()) {// 不是绝对路径删除不了
            return false;
        }
        return false;
    }


    @Test
    public void test1() {
        String fi = "D:/Photo_Test/photos/2017-01-12/101.jpg";
        Pattern pattern = Pattern.compile("\\d{4}-\\d{2}-\\d{2}");
    	Matcher matcher = pattern.matcher(fi);
    	if(matcher.find()){
    		String date = matcher.group();
    			String photos = fi.split(date)[0];
    			String tail = photos.split("photos")[1];
    			System.out.print(tail);
    	}
    }


}
