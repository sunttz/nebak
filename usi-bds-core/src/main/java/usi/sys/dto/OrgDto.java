package usi.sys.dto;

import java.util.ArrayList;
import java.util.List;

/*
 * 查询同步机构树时需要
 * (同步树,即一次性加载完全部节点的树)
 */
public class OrgDto {
	private Long id; //机构ID
	private String text; //机构名称
	private String orgSeq;	//机构序列
	private Long parentOrgId; //父机构
	private List<OrgDto> children = new ArrayList<OrgDto>();
	private String state;//用于区分机构节点的打开状态,closed为关闭,open为打开
	private String iconCls = "icon-treeopen";
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public Long getParentOrgId() {
		return parentOrgId;
	}
	public void setParentOrgId(Long parentOrgId) {
		this.parentOrgId = parentOrgId;
	}
	public List<OrgDto> getChildren() {
		return children;
	}
	public void setChildren(List<OrgDto> children) {
		this.children = children;
	}
	public String getIconCls() {
		return iconCls;
	}
	public void setIconCls(String iconCls) {
		this.iconCls = iconCls;
	}
	public String getOrgSeq() {
		return orgSeq;
	}
	public void setOrgSeq(String orgSeq) {
		this.orgSeq = orgSeq;
	}
	
}
