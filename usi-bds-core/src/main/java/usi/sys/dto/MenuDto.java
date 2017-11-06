package usi.sys.dto;

/*
 * 用于菜单树的类
 */
public class MenuDto {
	private long id;
	
	private String text;
	/**
	 * 菜单ID
	 */
	private long menuId;
	/**
	 * 菜单名称
	 */
	private String menuName;
	/**
	 * 父菜单ID
	 */
	private long parentId;
	/**
	 * 父菜单名称
	 */
	private String parentName;

	/**
	 * 菜单动作
	 */
	private String menuAction;
	/**
	 * 显示顺序
	 */
	private int displayOrder;
	/**
	 * 菜单序列
	 */
	private String menuSeq;
	/**
	 * 是否叶子节点
	 */
	private int isLeaf;
	/**
	 * 菜单备注
	 */
	private String menuMemo;
	/**
	 * 状态
	 */
	private int status;
	
	private String iconCls ="icon-memu-close"; 	//初始化树图标
	
	private int childMenu;//子菜单个数
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
	
	public int getChildMenu() {
		return childMenu;
	}

	public void setChildMenu(int childMenu) {
		this.childMenu = childMenu;
	}

	public String getIconCls() {
		return iconCls;
	}

	public void setIconCls(String iconCls) {
		this.iconCls = iconCls;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	private String state;
	
	public long getMenuId() {
		return menuId;
	}

	public void setMenuId(long menuId) {
		this.menuId = menuId;
	}

	public long getParentId() {
		return parentId;
	}

	public void setParentId(long parentId) {
		this.parentId = parentId;
	}

	public String getMenuName() {
		return menuName;
	}

	public void setMenuName(String menuName) {
		this.menuName = menuName;
	}

	public String getMenuAction() {
		return menuAction;
	}

	public void setMenuAction(String menuAction) {
		this.menuAction = menuAction;
	}

	public int getDisplayOrder() {
		return displayOrder;
	}

	public void setDisplayOrder(int displayOrder) {
		this.displayOrder = displayOrder;
	}

	public String getMenuSeq() {
		return menuSeq;
	}

	public void setMenuSeq(String menuSeq) {
		this.menuSeq = menuSeq;
	}

	public int getIsLeaf() {
		return isLeaf;
	}

	public void setIsLeaf(int isLeaf) {
		this.isLeaf = isLeaf;
	}

	public String getMenuMemo() {
		return menuMemo;
	}

	public void setMenuMemo(String menuMemo) {
		this.menuMemo = menuMemo;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
	
	public String getParentName() {
		return parentName;
	}

	public void setParentName(String parentName) {
		this.parentName = parentName;
	}

}
