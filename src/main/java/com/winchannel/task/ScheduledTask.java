package com.winchannel.task;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.winchannel.bean.Photo;
import com.winchannel.service.CleanFileDirService;
import com.winchannel.service.PropService;
import com.winchannel.utils.CleanFileTool;
import com.winchannel.utils.Constant;
import com.winchannel.utils.DateUtil;
import com.winchannel.utils.PropUtil;

/**
 * 分隔符：
 * 凡是操作数据库中的统一使用"/"字符串
 * 需要文件操作(如：文件复制)的相关路径中的分隔符使用 File.separator
 */
@Component
public class ScheduledTask {
    private static final Logger logger = LoggerFactory.getLogger(ScheduledTask.class);
    
    @Autowired
    private CleanFileDirService cleanFileDirService;
    @Autowired
    private PropService propService;
    
    Calendar calendar = Calendar.getInstance();
    
    int LOOP_SAVE_COUNT = PropUtil.LOOP_SAVE_COUNT;

    @Scheduled(cron = "${T1_CRON}")
    public void cleanFileDirTask_T1(){
    	
     	Thread current = Thread.currentThread();
    	current.setName(Constant.T1);
    	boolean is_complete = true;
    	try {
    		// 除此启动分配ID_POOL
    		synchronized (ScheduledTask.class) {
    			this.distIDResourceTask(Constant.T1);
			}
    		
    		while(is_complete){
    			is_complete = this.cleanPathHandler(current,Memory.T1_ID_POOL);
    			// 如果完成了，就重新分配ID_POOL
    			if(is_complete){
    				synchronized (ScheduledTask.class) {
    	    			this.distIDResourceTask(Constant.T1);
    				}
    			}
			}
			
		} catch (SQLException e) {
			error("cleanFileDirTask_T1 Error: SQLException ");
			error("Time : "+DateUtil.getStandDateTime());
			info("开始备份线程相关ID ...");
			propService.updateByMap(Memory.MARK_MAP);
			info("备份线程相关ID完毕！");
			e.printStackTrace();
		}
    }
    
    
    @Scheduled(cron = "${T2_CRON}")
    public void cleanFileDirTask_T2(){
    	Thread current = Thread.currentThread();
    	current.setName(Constant.T2);
    	boolean is_complete = true;
    	try {
    		// 除此启动分配ID_POOL
    		synchronized (ScheduledTask.class) {
    			this.distIDResourceTask(Constant.T2);
    		}
    		
    		while(is_complete){
    			is_complete = this.cleanPathHandler(current,Memory.T2_ID_POOL);
    			// 如果完成了，就重新分配ID_POOL
    			synchronized (ScheduledTask.class) {
        			this.distIDResourceTask(Constant.T2);
        		}
    		}
    		
		} catch (SQLException e) {
			error("cleanFileDirTask_T2 Error: SQLException ");
			error("Time : "+DateUtil.getStandDateTime());
			info("开始备份线程相关ID ...");
			propService.updateByMap(Memory.MARK_MAP);
			info("备份线程相关ID完毕！");
			e.printStackTrace();
		}
    }
    
    
    /**
     * ID资源分配
     * 每30秒检查一次POOL中的ID资源是否已经处理完毕
     * 对T_START_ID T_END_ID T_CURR_ID 进行分配
     * 锁级方法
     */
    public synchronized void distIDResourceTask(String threadName) {
    	
    	// 判断是否第一次运行，如果数据库中没有prop这个记录
    	Map<String,Long> infoMap = propService.selectProp();
    	
    	// 最大ID
    	Long maxId = cleanFileDirService.getPhotoMaxId();
    	
    	// 是否是首次运行
    	if(infoMap==null){
    		Long minId = cleanFileDirService.getPhotoMinId();
    		
    		// 是首次运行的话,同时需要设置Prop信息
    		Memory.T1_ID_POOL = cleanFileDirService.getNextIdPoolByStartID(minId);
    		// 根据pool1的最大id+1获取
    		Memory.T2_ID_POOL = cleanFileDirService.getNextIdPoolByStartID(
    					Memory.T1_ID_POOL.get(Memory.T1_ID_POOL.size()-1)+1
    		);
    		
    		// 记录ID_POOL信息到 数据库和 MAR_MAP 中
    		// 顺序T1_START_ID-T1_CURR_ID-T1_END_ID-T2_START_ID-T2_CURR_ID-T2_END_ID-CURR_MAX_ID
    		Photo prop = ScheduledUtil.groupFirstProp(maxId);
    		propService.insertProp(prop);
    		
    		// 第一次组织 MARK_MAP
    		ScheduledUtil.groupFirstMarkMap();
    		
    	} else {
    		// 获取到 线程操作的ID相关信息
    		// 得到剩下的T_ID_POOL 中的ID，将剩下的作为T_ID_POOL 继续运行
    		
    		// 如果内存中的 MARK_MAP 中没有数据,说明是运行中程序挂掉了，又重新启动的，所以Prop中有ID的数据，但是内存没有数据
    		if(ScheduledUtil.isExistMemoryData()){
    			// 如果是挂掉重启,将Prop中的数据装载到MARK_MAP中
    			// 内部同时设置CURR_MAX_ID(MARK_MAP中的和Prop中的)
    			// 重新组织 T_ID_POOL:根据T_CURR_ID 和 END_ID生成新的T_ID_POOL
    			propService.groupMemoryData(infoMap);
    			
    		} else {// 这就是正常运行过程中需要重新分配 T_ID_POOL
    			
    			// 正在处理的最大ID:默认获取MARK_MAP中存储的数据
    			Long CURR_MAX_ID = Memory.MARK_MAP.get(Constant.CURR_MAX_ID);
    			
    			// 下一轮的开始ID：多线程中最大 ID+1
				Long startId = CURR_MAX_ID + 1;
						
    			// 这时候MARK_MAP 中的T_CURR_ID 和 T_END_ID 应该是相等的，因为一个迭代结束
    			
    			// 下面需要判断是哪个线程
    			if(Constant.T1.equals(threadName) &&
    					// 当当前和结束  id相等是说明是运行的时候一个轮询完成，避免第一次运行，一个线程分配好了多个线程的ID_POOL，其他县城过来后直接重新分配了，其实并没有处理对应的ID
    					(Memory.MARK_MAP.get(Constant.T1_CURR_ID).compareTo(Memory.MARK_MAP.get(Constant.T1_END_ID))==0)
    			  ){

    				// 是首次运行的话,同时需要设置Prop信息
    	    		Memory.T1_ID_POOL = cleanFileDirService.getNextIdPoolByStartID(startId);
    	    		
    	    		// 记录ID_POOL信息到 prop 和 MAR_MAP 中,这里需要更新prop数据库信息
    	    		infoMap.put(Constant.T1_START_ID,Memory.T1_ID_POOL.get(0));
    	    		infoMap.put(Constant.T1_CURR_ID,Memory.T1_ID_POOL.get(0));
    	    		infoMap.put(Constant.T1_END_ID,Memory.T1_ID_POOL.get(Memory.T1_ID_POOL.size()-1));
    	    		
    	    		Memory.MARK_MAP.put(Constant.T1_START_ID, Memory.T1_ID_POOL.get(0));
    	    		Memory.MARK_MAP.put(Constant.T1_CURR_ID, Memory.T1_ID_POOL.get(0));
    	    		Memory.MARK_MAP.put(Constant.T1_END_ID, Memory.T1_ID_POOL.get(Memory.T1_ID_POOL.size()-1));
    	    		
    	    		 // 设置CURR_MAX_ID
    	    		CURR_MAX_ID = Memory.T1_ID_POOL.get(Memory.T1_ID_POOL.size()-1);
    	    		
    			} else if(Constant.T2.equals(threadName) && 
    						(Memory.MARK_MAP.get(Constant.T2_CURR_ID).compareTo(Memory.MARK_MAP.get(Constant.T2_END_ID))==0)
    					){
    				
    				// 是首次运行的话,同时需要设置Prop信息
    	    		Memory.T2_ID_POOL = cleanFileDirService.getNextIdPoolByStartID(startId);
    	    		// 记录ID_POOL信息到 prop 和 MAR_MAP 中
    	    		infoMap.put(Constant.T2_START_ID,Memory.T2_ID_POOL.get(0));
    	    		infoMap.put(Constant.T2_CURR_ID,Memory.T2_ID_POOL.get(0));
    	    		infoMap.put(Constant.T2_END_ID,Memory.T2_ID_POOL.get(Memory.T2_ID_POOL.size()-1));
    	    		
    	    		Memory.MARK_MAP.put(Constant.T2_START_ID, Memory.T2_ID_POOL.get(0));
    	    		Memory.MARK_MAP.put(Constant.T2_CURR_ID, Memory.T2_ID_POOL.get(0));
    	    		Memory.MARK_MAP.put(Constant.T2_END_ID, Memory.T2_ID_POOL.get(Memory.T2_ID_POOL.size()-1));
    				
    	    		CURR_MAX_ID = Memory.T2_ID_POOL.get(Memory.T2_ID_POOL.size()-1);
    			}
    			
    			// 设置当前最大ID
    			infoMap.put(Constant.CURR_MAX_ID,CURR_MAX_ID);
    			// 更新prop数据信息
    			propService.updateByMap(infoMap);
    			
	    		Memory.MARK_MAP.put(Constant.CURR_MAX_ID, CURR_MAX_ID);
    			
    		}
    		
    	}
    	
    }
    

    /**
     * 根据T_ID_POOL处理对应的图片资源
     */
    private boolean cleanPathHandler(Thread currentT,List<Long> T_ID_POOL) throws SQLException{
		// 需要处理的最大ID
//        	Long maxPhotoId = cleanFileDirService.getPhotoMaxId();
    	info("开始处理 T_ID_POOL,SIZE="+T_ID_POOL.size());
    	
    	Photo photo = null;
    	
    	if(T_ID_POOL!=null && T_ID_POOL.size()>0){
    		
    		for(int index=0;index<T_ID_POOL.size();index++){
    			
    			Long curr_id = T_ID_POOL.get(index);
    			
    			System.out.println("处理ID："+curr_id);
    			
    			// 每次循环 将ID相关信息放到MARK_MAP中
    			synchronized (ScheduledTask.class) {
    				ScheduledUtil.markIdInfo2_MarkMap(currentT,curr_id);
    			}
    			
    			if(curr_id!=null){
    				photo = cleanFileDirService.getPhotoOne(curr_id);
    			} else {
    				System.out.println("ID is NUll : "+curr_id);
    				continue;
    			}
    			
    			if(photo == null){
    				continue;
    			}
    			
    			String absolutePath = photo.getImgAbsPath();
    			String imgUrl = photo.getImgUrl();
    			boolean containsDot2B = false;
    			// 判断是否包含 dot2B
    			if(imgUrl!=null && imgUrl.contains("dot2B")){// 说明是新数据
    				containsDot2B = true;
    			}
    			
    			// 判断该Photo是否已经是一个符合规则的路径
                if (CleanFileTool.isTruePath2(photo)) {
                    // 如果是符合规则的路径，就继续下一个Photo
                    continue;
                }
    			
                // 根据Photo的IMG_ID 去查询其他表中的 FUNC_CODE信息
                String FUNC_CODE = cleanFileDirService.getFuncCodeByPhoto(photo);
                
                // 获取到 FUNC_CODE
                if (FUNC_CODE != null && FUNC_CODE.length() > 0) {
                    photo.setFuncCode(FUNC_CODE);

                    // clean FUNC_CODE path 得到 D:/Photo_Test/photos/FUNC_CODE 这层目录
                    @SuppressWarnings("unused")
					String funcCodeFullPath = CleanFileTool.cleanFuncCodePath(FUNC_CODE);

                    // 处理日期目录  得到 D:/Photo_Test/photos/FUNC_CODE/2017-01-23 这层目录
                    @SuppressWarnings("unused")
                    String codeDateFullPath = CleanFileTool.cleanDatePath(FUNC_CODE, photo.getImgUrl());
                    
                    // 开始move文件 
                    // 在原绝对路径基础上加上FUNC_CODE目录
                    String newAbsPath = "";
                    // 移动文件到新目录
                    // 注意：方法内需要对路径中的分隔符处理
                    boolean moveFileOk = false;

                    if(containsDot2B){// 有绝对路径数据
                    	newAbsPath = CleanFileTool.getNewAbsPath(absolutePath, FUNC_CODE);
                    	moveFileOk = CleanFileTool.movePhoto(absolutePath, newAbsPath);
                    } else {
                    	newAbsPath = CleanFileTool.getNewAbsPath(new String[]{imgUrl,FUNC_CODE});
                    	moveFileOk = CleanFileTool.movePhoto(new String[]{imgUrl,newAbsPath});
                    }
                    
                    if (moveFileOk) {
                        // 更新数据库:需要更新photo的 absolute_path 和 img_url
                        String newImgUrl = CleanFileTool.getNewImgUrl(photo.getImgUrl(),FUNC_CODE);
                        // TODO ？对于老数据绝对路径需不需要保存
                        photo.setImgAbsPath(newAbsPath);// 修改绝对路径
                        photo.setImgUrl(newImgUrl);// 修改img_url
                        cleanFileDirService.updatePhoto(photo);
                        info("移动文件成功！");
                    }
                }
                
                // 检查LOOP次数 是否满100*N，是则update ID信息
                if((index % LOOP_SAVE_COUNT==0) && index!=0){
                	propService.updateByMap(Memory.MARK_MAP);
                }

    		}
    		
    		return true;
    	}
    		
    	return false;
    }
    
    
    
    
    


    /**
     * ********************************
     */

    public void info(String info) {
        logger.info(info);
    }

    public void error(String error) {
        logger.error(error);
    }

}