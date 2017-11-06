package usi.sys.entity;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import usi.sys.util.JacksonUtil;
/**
 * @author long.ming
 * 创建时间：2014-3-27 10:47:48
 */
public class SysScene {
	private String busiSceneCode;   //业务场景编码
	private String busiSceneName;   //业务场景名称
	private String sceneDesc;		//场景说明
	private Integer displayOrder;		//显示顺序
	private String createStaff;  	//创建人
	
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	private Date createTime;		//创建时间
	
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	private Date lastModiTime;  	//最后修改时间
	private Integer status;				//状态
	
	@Override
	public String toString(){
		String result = "";
		try {
			result = JacksonUtil.obj2json(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public String getBusiSceneCode() {
		return busiSceneCode;
	}

	public void setBusiSceneCode(String busiSceneCode) {
		this.busiSceneCode = busiSceneCode;
	}

	public String getBusiSceneName() {
		return busiSceneName;
	}

	public void setBusiSceneName(String busiSceneName) {
		this.busiSceneName = busiSceneName;
	}

	public String getSceneDesc() {
		return sceneDesc;
	}

	public void setSceneDesc(String sceneDesc) {
		this.sceneDesc = sceneDesc;
	}

	public Integer getDisplayOrder() {
		return displayOrder;
	}

	public void setDisplayOrder(Integer displayOrder) {
		this.displayOrder = displayOrder;
	}

	public String getCreateStaff() {
		return createStaff;
	}

	public void setCreateStaff(String createStaff) {
		this.createStaff = createStaff;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getLastModiTime() {
		return lastModiTime;
	}

	public void setLastModiTime(Date lastModiTime) {
		this.lastModiTime = lastModiTime;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}
	
}
