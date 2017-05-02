package com.winchannel.task;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.winchannel.bean.Photo;
import com.winchannel.service.CleanFileDirService;
import com.winchannel.utils.CleanFileTool;
import com.winchannel.utils.Constant;
import com.winchannel.utils.DateUtil;
import com.winchannel.utils.PropUtil;

@Component
public class ScheduledTask {
    private static final Logger logger = LoggerFactory.getLogger(ScheduledTask.class);

    @Autowired
    private CleanFileDirService cleanFileDirService;
    
    Calendar calendar = Calendar.getInstance();
    
    
    
    /**
     * ID资源分配
     * 每30秒检查一次POOL中的ID资源是否已经处理完毕
     * 对T_START_ID T_END_ID T_CURR_ID 进行分配
     */
    public void distIDResourceTask() { 
    	// 是否是首次运行
    	if(ScheduledUtil.isFirstRun()){
    		Long minId = cleanFileDirService.getPhotoMinId();
    		// 是首次运行的话,同时需要设置Prop信息
    		// 获取前10000条记录
    		Memory.T1_ID_POOL = cleanFileDirService.getNextIdPoolByStartID(minId);
    		Memory.T2_ID_POOL = cleanFileDirService.getNextIdPoolByStartID(Memory.T1_ID_POOL.get(Memory.T1_ID_POOL.size()));
    		
    	} else {
    		// 获取到 线程操作的ID相关信息
    		// 得到剩下的T_ID_POOL 中的ID，将剩下的作为T_ID_POOL 继续运行
    		
    		// 如果内存中的 MARK_MAP 中没有数据,说明是运行中程序挂掉了，又重新启动的，所以Prop中有ID的数据，但是内存没有数据
    		if(ScheduledUtil.isReRun()){
    			// 如果是挂掉重启,将Prop中的数据装载到MARK_MAP中
    			Memory.MARK_MAP = PropUtil.groupMarkMap();
    			// 重新组织 T_ID_POOL:根据T_CURR_ID 和 END_ID生成新的T_ID_POOL
    			PropUtil.groupIdPool(Constant.T1);
    			PropUtil.groupIdPool(Constant.T2);
    			
    			
    			
    			
    		} else {// 这就是正常运行过程中需要重新分配 T_ID_POOL
    			// 这时候MARK_MAP 中的T_CURR_ID 和 T_END_ID 应该是相等的，因为一个迭代结束
    			
    			
    		}
    		
    	}
    	
    }
    
    
  

    @Scheduled(cron = "0 26 15 * * ? ")
    public void cleanFileDirTask_T1(){
    	Thread current = Thread.currentThread();
    	current.setName("T1");
    	try {
			this.cleanPathHandler(current,Memory.T1_ID_POOL);
			
		} catch (SQLException e) {
			error("cleanFileDirTask_T1 Error: SQLException ");
			error("Time : "+DateUtil.getStandDateTime());
			info("开始备份线程相关ID ...");
			ScheduledUtil.bakThreadMapIdInfo2Prop(Memory.MARK_MAP);
			info("备份线程相关ID完毕！");
			e.printStackTrace();
		}
    }
    
    
    
    
    
    
    @Scheduled(cron = "0 26 15 * * ? ")
    public void cleanFileDirTask() { // 间隔3分钟执行一次任务
        Thread current = Thread.currentThread();
        info("开始进行文件目录清理 线程 ：:" + current.getId() + ",name:" + current.getName());

        // 定时任务规定时长
        Long runTaskTimeLen = PropUtil.RUN_TASK_TIME_LEN;

        int startMinute = calendar.get(Calendar.MINUTE);

        // 读取记录的 photo id 是否存在，如果不存在就是第一次执行，如果存在直接作为开始点
        info("获取Prop MarkId");

        Long id = PropUtil.MARK_PHOTO_ID;
        boolean flag = true;
        try {

            // 查询出数据库Photo的最大ID，如果while中的id >最大ID了说明Task完成了
            Long maxId = cleanFileDirService.getPhotoMaxId();
            
            if(id>=maxId){
                flag = false;
            }

            while (flag) {
                Photo photo = null;
                if (id == null || id == 0L) {
                    // 查询第一条记录
                    info("查询 第一个Photo");
                    photo = cleanFileDirService.getFirstPhotoOne();
                    info("第一个Photo:" + photo.getId());
                } else {
                    try {
                        photo = cleanFileDirService.getPhotoOne(id);
                        if (photo == null) {
                            id++;
                            continue;
                        }

                    } catch (Exception e) {
                        error("cleanFileDirTask ERROR");
                        e.printStackTrace();
                    }
                }

                // 判断该Photo是否已经是一个符合规则的路径
                if (CleanFileTool.isTruePath(photo)) {
                    // 如果是符合规则的路径，就继续下一个Photo
                    id++;
                    continue;
                }

                // 根据Photo的IMG_ID 去查询其他表中的 FUNC_CODE信息
                String funcCode = cleanFileDirService.getFuncCodeByPhoto(photo);

                // 获取到 FUNC_CODE
                if (funcCode != null && funcCode.length() > 0) {
                    photo.setFuncCode(funcCode);

                    // clean FUNC_CODE path
//                    String funcCodeFullPath = CleanFileTool.cleanFuncCodePath(funcCode);

                    // 处理日期目录
//                    String codeDatePath = CleanFileTool.cleanDatePath(funcCode, photo.getImgAbsPath());

                    // 开始move文件
                    String absolutePath = photo.getImgAbsPath();
                    String newAbsPath = CleanFileTool.getNewAbsPath(absolutePath, funcCode);
                    boolean moveFileOk = CleanFileTool.movePhoto(absolutePath, newAbsPath);

                    if (moveFileOk) {
                        // 更新数据库:需要更新photo的 absolute_path 和 img_url
                        String newImgUrl = CleanFileTool.getNewImgUrl(photo.getImgUrl(),funcCode);
                        photo.setImgAbsPath(newAbsPath);
                        photo.setImgUrl(newImgUrl);
                        cleanFileDirService.updatePhoto(photo);
                        info("移动文件成功！");
                    }
                }

                int endMinute = calendar.get(Calendar.MINUTE);
                // 超过了规定的时长则停止运行
                if ((endMinute - startMinute) >= runTaskTimeLen) {
                    // 记录ID
                    PropUtil.setProp("MARK_PHOTO_ID", id + "", "COMM:MARK_PHOTO_ID");
                    info("备份ID完成 ...");
                    break;
                }

                // 正常处理条件下，回到这里，操作下一个Photo
                id++;
            }
        } catch (Exception e) {
            error("Clean ERROR！Time：" + DateUtil.getStandDateTime(new Date()));
            info("Clean Error,开始备份ID ...");
            PropUtil.setProp("MARK_PHOTO_ID", id + "", "COMM:MARK_PHOTO_ID");
            info("备份ID完成 ...");
            e.printStackTrace();
        }

        info("任务结束!");
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
    			
    			// 每次循环 将ID相关信息放到MARK_MAP中
    			ScheduledUtil.markIdInfo2_MarkMap(currentT,curr_id);
    			
    			photo = cleanFileDirService.getPhotoOne(curr_id);
    			
    			if(photo == null){
    				continue;
    			}
    			
    			// 判断该Photo是否已经是一个符合规则的路径
                if (CleanFileTool.isTruePath(photo)) {
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
                    String codeDateFullPath = CleanFileTool.cleanDatePath(FUNC_CODE, photo.getImgAbsPath());

                    // 开始move文件 
                    String absolutePath = photo.getImgAbsPath();
                    // 在原绝对路径基础上加上FUNC_CODE目录
                    String newAbsPath = CleanFileTool.getNewAbsPath(absolutePath, FUNC_CODE);
                    // 移动文件到新目录
                    boolean moveFileOk = CleanFileTool.movePhoto(absolutePath, newAbsPath);

                    if (moveFileOk) {
                        // 更新数据库:需要更新photo的 absolute_path 和 img_url
                        String newImgUrl = CleanFileTool.getNewImgUrl(photo.getImgUrl(),FUNC_CODE);
                        photo.setImgAbsPath(newAbsPath);// 修改绝对路径
                        photo.setImgUrl(newImgUrl);// 修改img_url
                        cleanFileDirService.updatePhoto(photo);
                        info("移动文件成功！");
                    }
                }

    		}
    		
    	}
    		
    	return true;
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    //    @Scheduled(cron="0 0/2 8-20 * * ?")
    public void executeTask1() { // 间隔2分钟执行一次任务
        Thread current = Thread.currentThread();
        System.out.println("定时任务1: " + current.getId());
        logger.info("ScheduledTask.executeTask1 定时任务1:" + current.getId() + ",name:" + current.getName());
    }


    //    @Scheduled(cron="0 0/1 8-20 * * ?")
    public void executeTask2() { // 间隔1分钟执行一次任务
        Thread current = Thread.currentThread();

//    	List<DemoBean> queryList = demoService.findAll();
//    	if(queryList!=null && queryList.size()>0)
//    		System.out.println("queryList:"+queryList.size());

        System.out.println("定时任务2: " + current.getId());
        logger.info("ScheduledTask.executeTask2 定时任务2:" + current.getId() + ",name:" + current.getName());
    }


    //    @Scheduled(cron="0 0/3 5-23 * * ?")
    public void executeTask3() { // 间隔3分钟执行一次任务
        Thread current = Thread.currentThread();
        System.out.println("定时任务3: " + current.getId());
        logger.info("ScheduledTask.executeTask3  定时任务3:" + current.getId() + ",name:" + current.getName());
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