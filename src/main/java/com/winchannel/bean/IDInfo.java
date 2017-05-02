package com.winchannel.bean;

import java.io.Serializable;

public class IDInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * 需要处理的文件目录
	 */
	private String PHOTO_PATH;
	
	/**
	 * 定时任务运行时长
	 */
	private Long RUN_TASK_TIME_LEN;
	
	/**
	 * 一个线程一次处理的ID数
	 */
	private int REDUCE_ID_NUM;
	
	/**
	 * 线程ID_POOL的 开始ID，当前处理的ID，结束ID
	 */
	private Long startId;
	private Long currId;
	private Long endId;
	
	public String getPHOTO_PATH() {
		return PHOTO_PATH;
	}
	public void setPHOTO_PATH(String pHOTO_PATH) {
		PHOTO_PATH = pHOTO_PATH;
	}
	public Long getRUN_TASK_TIME_LEN() {
		return RUN_TASK_TIME_LEN;
	}
	public void setRUN_TASK_TIME_LEN(Long rUN_TASK_TIME_LEN) {
		RUN_TASK_TIME_LEN = rUN_TASK_TIME_LEN;
	}
	public int getREDUCE_ID_NUM() {
		return REDUCE_ID_NUM;
	}
	public void setREDUCE_ID_NUM(int rEDUCE_ID_NUM) {
		REDUCE_ID_NUM = rEDUCE_ID_NUM;
	}
	public Long getStartId() {
		return startId;
	}
	public void setStartId(Long startId) {
		this.startId = startId;
	}
	public Long getCurrId() {
		return currId;
	}
	public void setCurrId(Long currId) {
		this.currId = currId;
	}
	public Long getEndId() {
		return endId;
	}
	public void setEndId(Long endId) {
		this.endId = endId;
	}
	
}
