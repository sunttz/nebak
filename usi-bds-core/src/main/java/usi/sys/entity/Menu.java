package usi.sys.entity;

import usi.sys.util.JacksonUtil;

public class Menu {
	/**
	 * 菜单ID
	 */
	private Long menuId;
	/**
	 * 菜单名称
	 */
	private String menuName;
	/**
	 * 父菜单ID
	 */
	private Long parentId;
	/**
	 * 菜单动作
	 */
	private String menuAction;
	/**
	 * 显示顺序
	 */
	private Integer displayOrder;
	/**
	 * 菜单序列
	 */
	private String menuSeq;
	/**
	 * 是否叶子节点
	 */
	private Integer isLeaf;
	/**
	 * 菜单备注
	 */
	private String menuMemo;
	/**
	 * 状态
	 */
	private Integer status;
	
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
	
	public Long getMenuId() {
		return menuId;
	}
	public void setMenuId(Long menuId) {
		this.menuId = menuId;
	}
	public String getMenuName() {
		return menuName;
	}
	public void setMenuName(String menuName) {
		this.menuName = menuName;
	}
	public Long getParentId() {
		return parentId;
	}
	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}
	public String getMenuAction() {
		return menuAction;
	}
	public void setMenuAction(String menuAction) {
		this.menuAction = menuAction;
	}
	public Integer getDisplayOrder() {
		return displayOrder;
	}
	public void setDisplayOrder(Integer displayOrder) {
		this.displayOrder = displayOrder;
	}
	public String getMenuSeq() {
		return menuSeq;
	}
	public void setMenuSeq(String menuSeq) {
		this.menuSeq = menuSeq;
	}
	public Integer getIsLeaf() {
		return isLeaf;
	}
	public void setIsLeaf(Integer isLeaf) {
		this.isLeaf = isLeaf;
	}
	public String getMenuMemo() {
		return menuMemo;
	}
	public void setMenuMemo(String menuMemo) {
		this.menuMemo = menuMemo;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	
}
