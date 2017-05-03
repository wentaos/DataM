package com.winchannel.bean;

import java.io.Serializable;

public class ThreadIDInfo implements Serializable {
	
	/**
	 * 线程名称
	 */
	private String threadName;
	
	/**
	 * 需要处理的文件目录
	 */
	private String photoPath;
	
	/**
	 * 定时任务运行时长
	 */
	private Long runTaskTime;
	
	/**
	 * 一个线程一次处理的ID数
	 */
	private int reduceIdNum;
	
	/**
	 * 是否是又有最大ID线程
	 */
	private boolean isMax;
	
	/**
	 * 线程ID_POOL的 开始ID，当前处理的ID，结束ID
	 */
	private Long startId;
	private Long currId;
	private Long endId;
	public String getThreadName() {
		return threadName;
	}
	public void setThreadName(String threadName) {
		this.threadName = threadName;
	}
	public String getPhotoPath() {
		return photoPath;
	}
	public void setPhotoPath(String photoPath) {
		this.photoPath = photoPath;
	}
	public Long getRunTaskTime() {
		return runTaskTime;
	}
	public void setRunTaskTime(Long runTaskTime) {
		this.runTaskTime = runTaskTime;
	}
	public int getReduceIdNum() {
		return reduceIdNum;
	}
	public void setReduceIdNum(int reduceIdNum) {
		this.reduceIdNum = reduceIdNum;
	}
	public boolean isMax() {
		return isMax;
	}
	public void setMax(boolean isMax) {
		this.isMax = isMax;
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
	
	
	private static final long serialVersionUID = 1L;
}
