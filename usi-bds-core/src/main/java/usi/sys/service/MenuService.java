package usi.sys.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import usi.sys.dao.MenuDao;
import usi.sys.dto.MenuDto;
import usi.sys.dto.PageObj;
import usi.sys.entity.Menu;

@Service
public class MenuService {
	
	@Resource
	private MenuDao menuDao;
	
//	@Resource
//	private MenuRolesHolder menuRolesHolder;
	
	/**
	 * 查询菜单树
	 * @return
	 */
	public List<MenuDto> getMenuTree(String id) {
		List<MenuDto> menu = menuDao.queryAllMenus1(id);
		return menu;
	}
	/**
	 * 分页查询所有的菜单
	 * @param pageObj
	 * @param menuName
	 * @param menuLevel
	 * @return
	 */
	public Map<String, Object> queryMenusByPage(PageObj pageObj,String menuName,String menuLevel,String seq){
		Map<String, Object> map = new HashMap<String, Object>();
		List<Menu> menus = menuDao.queryMenusByPage(pageObj,menuName,menuLevel,seq);
		map.put("total", pageObj.getTotal());
		map.put("rows", menus);
		return map;
	}
	
	public List<MenuDto> getMenuTree1(String id) {
		List<MenuDto> menu = menuDao.queryAllMenus1(id);
		return menu;
	}
	
	/**
	 * 向菜单表中插入数据
	 * @param menu
	 */
	@Transactional(rollbackFor=Exception.class)
	public void insertMenu(Menu menu){
		menuDao.insertMenu(menu);
//		//更新系统维护的全局变量(菜单及其对应的角色列表)
//		menuRolesHolder.updateMenuRoles();
	}
	/**
	 * 删除菜单表中的数据,先删除角色的菜单权限
	 * @param seq
	 * @return
	 */
	@Transactional(rollbackFor=Exception.class)
	public int deleteMenu(Long menuId){
		menuDao.deleteMenuRole(menuId);
		int ret =menuDao.deleteMenu(menuId);
//		//更新系统维护的全局变量(菜单及其对应的角色列表)
//		menuRolesHolder.updateMenuRoles();
		return ret;
	}
	/**
	 * 更新菜单表中的数据
	 * @param menu
	 * @return
	 */
	@Transactional(rollbackFor=Exception.class)
	public void updateMenu(Menu menu){
		menuDao.updateMenu(menu);
//		//更新系统维护的全局变量(菜单及其对应的角色列表)
//		menuRolesHolder.updateMenuRoles();
	}
	
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getMenus() {
		List<MenuDto> rootMenu=menuDao.queryRootMenu();
		Map<String, Object> tree = new HashMap<String, Object>();
		tree.put("id", rootMenu.get(0).getMenuId());
		tree.put("text", rootMenu.get(0).getMenuName());
		tree.put("pId", rootMenu.get(0).getParentId());
		tree.put("isLeaf", 0);
		tree.put("iconCls", rootMenu.get(0).getIconCls());
		tree.put("children", new ArrayList<Map<String, Object>>());
		List<Map<String, Object>> trees = new ArrayList<Map<String,Object>>();
		trees.add(tree);
		List<MenuDto> menus = menuDao.queryAllMenus();
		List<Map<String, Object>> nodes = convert(menus);
		nodes.add(tree);
		for(Map<String, Object> node: nodes) {
			for(Map<String, Object> node2: nodes) {
				if(node2.get("pId").equals(node.get("id")) ) {
					((List<Map<String, Object>>) node.get("children")).add(node2);
				}
			}
		}
		
		return trees;
	}
	
	private List<Map<String, Object>> convert(List<MenuDto> menus) {
		List<Map<String, Object>> nodes = new ArrayList<Map<String,Object>>();
		for(MenuDto menu: menus) {
			Map<String, Object> node = new HashMap<String, Object>();
			node.put("id", menu.getMenuId());
			node.put("pId", menu.getParentId());
			node.put("text", menu.getMenuName());
			node.put("isLeaf", menu.getIsLeaf());
			node.put("iconCls", menu.getIconCls());
			node.put("children", new ArrayList<Map<String, Object>>());
			nodes.add(node);
		}
		return nodes;
	}
	
}
