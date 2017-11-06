package usi.sys.dao;

import java.util.List;
import java.util.Map;

import usi.sys.dto.MenuDto;
import usi.sys.dto.PageObj;
import usi.sys.entity.Menu;

public interface MenuDao {
	
	/**
	 * 查出所有的菜单节点
	 * @return
	 */
	public List<MenuDto> queryAllMenus();
	
	public List<MenuDto> queryRootMenu();
	
	/**
	 * 查出所有的菜单节点
	 * @return
	 */
	public List<MenuDto> queryAllMenus1(String id);
	
	/**
	 * 向菜单表中插入数据
	 * @param menu
	 */
	public void insertMenu(final Menu menu);
	
	/**
	 * 删除菜单表中的数据
	 * @param menuId
	 * @return
	 */
	public int deleteMenu(Long menuId  );
	/**
	 * 删除角色的菜单权限
	 * @param menuId
	 * @return
	 */
	public int deleteMenuRole(Long menuId);
	/**
	 * 更新菜单表中的数据
	 * @param menu
	 * @return
	 */
	public int updateMenu(Menu menu);
	
	/**
	 * 分页查询所有菜单
	 */
	public List<Menu> queryMenusByPage(PageObj pageObj,String menuName,String menuLevel,String seq);
	
	/**
	 * 取得菜单与角色的关联关系
	 * 结果Map中key是菜单url,value是对该菜单拥有权限的角色列表
	 */
	public Map<String,List<String>> getMenuRoles();
	
}
