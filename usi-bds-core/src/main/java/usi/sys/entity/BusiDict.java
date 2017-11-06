package usi.sys.entity;

import java.util.Date;

import usi.sys.util.JacksonUtil;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * @author long.ming
 * 创建时间：2014-3-27 10:47:48
 */
public class BusiDict {
	private Long dicId;           	//主键ID
	private String busiSceneCode;   //业务场景编码
	private String dicCode;			//字典编码
	private String dicName;			//字典名称
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
	
	public Long getDicId() {
		return dicId;
	}

	public void setDicId(Long dicId) {
		this.dicId = dicId;
	}

	public String getBusiSceneCode() {
		return busiSceneCode;
	}

	public void setBusiSceneCode(String busiSceneCode) {
		this.busiSceneCode = busiSceneCode;
	}

	public String getDicCode() {
		return dicCode;
	}

	public void setDicCode(String dicCode) {
		this.dicCode = dicCode;
	}
	public String getDicName() {
		return dicName;
	}
	public void setDicName(String dicName) {
		this.dicName = dicName;
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
