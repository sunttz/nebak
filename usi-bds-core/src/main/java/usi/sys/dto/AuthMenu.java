package usi.sys.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户的菜单
 * @author fan.fan
 * @date 2014-4-2 下午8:26:55
 */
public class AuthMenu {

	private long menuId;
	
	private String menuName;
	
	private String menuAction;
	
	private long parentId;
	
	private String target = "c_iframe";
	/**是否叶子节点（1：是，0：否）*/
	private int isLeaf;
	
	private List<AuthMenu> children = new ArrayList<AuthMenu>();

	public long getMenuId() {
		return menuId;
	}

	public void setMenuId(long menuId) {
		this.menuId = menuId;
	}

	public int getIsLeaf() {
		return isLeaf;
	}

	public void setIsLeaf(int isLeaf) {
		this.isLeaf = isLeaf;
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

	public long getParentId() {
		return parentId;
	}

	public void setParentId(long parentId) {
		this.parentId = parentId;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public List<AuthMenu> getChildren() {
		return children;
	}

	public void setChildren(List<AuthMenu> children) {
		this.children = children;
	}
}
