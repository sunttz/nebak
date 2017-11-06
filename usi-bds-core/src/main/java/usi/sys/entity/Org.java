package usi.sys.entity;

import usi.sys.util.JacksonUtil;

public class Org {
	private Long orgId; //机构ID
	private String orgName; //机构名称
	private Long parentOrgId; //父机构
	private String orgSeq; //机构序列
	private Integer orgTypeId; //机构类型
	private Integer orgGrade; //机构级别
	private String orgContacter; //联系人
	private String contactTel;	//联系电话
	private String orgAddress;  //机构地址
	private Integer isLeaf; //是否叶子 ,1是  0否
	private Integer displayOrder; //显示顺序
	private String memo; //备注
	private Integer status; //状态
	
	@Override
	public String toString() {
		String result = "";
		try {
			result = JacksonUtil.obj2json(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public Long getOrgId() {
		return orgId;
	}
	public void setOrgId(Long orgId) {
		this.orgId = orgId;
	}
	public String getOrgName() {
		return orgName;
	}
	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}
	public Long getParentOrgId() {
		return parentOrgId;
	}
	public void setParentOrgId(Long parentOrgId) {
		this.parentOrgId = parentOrgId;
	}
	public String getOrgSeq() {
		return orgSeq;
	}
	public void setOrgSeq(String orgSeq) {
		this.orgSeq = orgSeq;
	}
	public Integer getOrgTypeId() {
		return orgTypeId;
	}
	public void setOrgTypeId(Integer orgTypeId) {
		this.orgTypeId = orgTypeId;
	}
	public Integer getOrgGrade() {
		return orgGrade;
	}
	public void setOrgGrade(Integer orgGrade) {
		this.orgGrade = orgGrade;
	}
	public String getOrgContacter() {
		return orgContacter;
	}
	public void setOrgContacter(String orgContacter) {
		this.orgContacter = orgContacter;
	}
	public String getContactTel() {
		return contactTel;
	}
	public void setContactTel(String contactTel) {
		this.contactTel = contactTel;
	}
	public String getOrgAddress() {
		return orgAddress;
	}
	public void setOrgAddress(String orgAddress) {
		this.orgAddress = orgAddress;
	}
	public Integer getIsLeaf() {
		return isLeaf;
	}
	public void setIsLeaf(Integer isLeaf) {
		this.isLeaf = isLeaf;
	}
	public Integer getDisplayOrder() {
		return displayOrder;
	}
	public void setDisplayOrder(Integer displayOrder) {
		this.displayOrder = displayOrder;
	}
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	
}
