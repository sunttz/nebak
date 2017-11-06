package usi.sys.dao.impl4mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import usi.common.annotation.MysqlDb;
import usi.sys.dao.JdbcDaoSupport4mysql;
import usi.sys.dao.MenuDao;
import usi.sys.dto.MenuDto;
import usi.sys.dto.PageObj;
import usi.sys.entity.Menu;
/**
 * @author chen.kui
 * @date 2014年9月28日15:28:43
 */
@MysqlDb
@Repository
public class MenuDaoImpl extends JdbcDaoSupport4mysql implements MenuDao {

	@Override
	public List<MenuDto> queryAllMenus() {
		String sql = "select t.menu_id,t.menu_name,t.menu_action,t.parent_menu_id,t.display_order,t.menu_seq,t.is_leaf,"
				+ " (select menu_name from sys_menu u where u.menu_id= t.parent_menu_id) parent_name,"
				+ "t.menu_memo,t.status from sys_menu t where t.menu_id !=0 order by t.display_order ";
		return this.getJdbcTemplate().query(sql, new RowMapper<MenuDto>() {

			@Override
			public MenuDto mapRow(ResultSet rs, int rowNum) throws SQLException {
				MenuDto menu = new MenuDto();
				menu.setMenuId(rs.getLong("menu_id"));
				menu.setMenuName(rs.getString("menu_name"));
				menu.setMenuMemo(rs.getString("menu_memo"));
				menu.setDisplayOrder(rs.getInt("display_order"));
				menu.setIsLeaf(rs.getInt("is_leaf"));
				menu.setMenuAction(rs.getString("menu_action"));
				menu.setMenuSeq(rs.getString("menu_seq"));
				menu.setParentId(rs.getLong("parent_menu_id"));
				menu.setStatus(rs.getInt("status"));
				menu.setParentName(rs.getString("parent_name"));
				if(rs.getInt("is_leaf")==1){
					menu.setState("closed");
					menu.setIconCls("icon-memu-leaf");
				}else{
					menu.setState("closed");
				}
				return menu;
			}
		});
	}

	@Override
	public List<MenuDto> queryRootMenu() {
		String sql = "select t.menu_id,t.menu_name,t.menu_action,t.parent_menu_id,t.display_order,t.menu_seq,t.is_leaf,"
				+ " (select menu_name from sys_menu u where u.menu_id= t.parent_menu_id) parent_name,"
				+ "t.menu_memo,t.status from sys_menu t where t.menu_id =0 order by t.display_order ";
		return this.getJdbcTemplate().query(sql, new RowMapper<MenuDto>() {
			
			@Override
			public MenuDto mapRow(ResultSet rs, int rowNum) throws SQLException {
				MenuDto menu = new MenuDto();
				menu.setMenuId(rs.getLong("menu_id"));
				menu.setMenuName(rs.getString("menu_name"));
				menu.setMenuMemo(rs.getString("menu_memo"));
				menu.setDisplayOrder(rs.getInt("display_order"));
				menu.setIsLeaf(rs.getInt("is_leaf"));
				menu.setMenuAction(rs.getString("menu_action"));
				menu.setMenuSeq(rs.getString("menu_seq"));
				menu.setParentId(rs.getLong("parent_menu_id"));
				menu.setStatus(rs.getInt("status"));
				menu.setParentName(rs.getString("parent_name"));
				if(rs.getInt("is_leaf")==1){
					menu.setState("open");
					menu.setIconCls("icon-memu-leaf");
				}else{
					menu.setState("closed");
				}
				return menu;
			}
		});
	}

	@Override
	public List<MenuDto> queryAllMenus1(String id) {
		String sql = "select t.menu_id,t.menu_name,t.menu_action,t.parent_menu_id,t.display_order,t.menu_seq,t.is_leaf,"
				+ " (select menu_name from sys_menu u where u.menu_id= t.parent_menu_id) parent_name,"
				+ "(select count(1) from sys_menu t1 where t.menu_id=t1.parent_menu_id) cou,"
				+ "t.menu_memo,t.status from sys_menu t where t.parent_menu_id=? order by t.display_order ";
		return this.getJdbcTemplate().query(sql, new Object[]{id},new RowMapper<MenuDto>() {

			@Override
			public MenuDto mapRow(ResultSet rs, int rowNum) throws SQLException {
				MenuDto menu = new MenuDto();
				menu.setId(rs.getInt("menu_id"));
				menu.setText(rs.getString("menu_name"));
				menu.setMenuId(rs.getLong("menu_id"));
				menu.setMenuName(rs.getString("menu_name"));
				menu.setMenuMemo(rs.getString("menu_memo"));
				menu.setDisplayOrder(rs.getInt("display_order"));
				menu.setIsLeaf(rs.getInt("is_leaf"));
				menu.setMenuAction(rs.getString("menu_action"));
				menu.setMenuSeq(rs.getString("menu_seq"));
				menu.setParentId(rs.getLong("parent_menu_id"));
				menu.setStatus(rs.getInt("status"));
				menu.setParentName(rs.getString("parent_name"));
				menu.setChildMenu(rs.getInt("cou"));
				if(rs.getInt("is_leaf")==1){
					menu.setState("open");
					menu.setIconCls("icon-memu-leaf");
				}else{
					menu.setState("closed");
				}
				return menu;
			}
		});
	}

	@Override
	public void insertMenu(final Menu menu) {
		String seq = menu.getMenuSeq();
		int menuId = 0;
		final String sql = "insert into sys_menu(menu_name,menu_action,parent_menu_id,display_order,"
				+ "is_leaf,menu_memo,status ) "
				+ " values(?,?,?,?,?,?,?) ";
		String updateSql = "update sys_menu set menu_seq =? where menu_id = ?";
		KeyHolder holder = new GeneratedKeyHolder();
		if(seq==null){
			this.getJdbcTemplate().update(new PreparedStatementCreator() {
				@Override
				public PreparedStatement createPreparedStatement(Connection con)
						throws SQLException {
					PreparedStatement ps = con.prepareStatement(sql, new String[]{"menu_id"});
					ps.setString(1, menu.getMenuName());
					ps.setString(2, menu.getMenuAction());
					ps.setLong(3, menu.getParentId());
					ps.setInt(4, menu.getDisplayOrder());
					ps.setInt(5,menu.getIsLeaf());
					ps.setString(6, menu.getMenuMemo());
					ps.setInt(7, menu.getStatus());
					return ps;
				}
			}, holder);
			menuId = holder.getKey().intValue();
			seq = menuId+".";
			this.getJdbcTemplate().update(updateSql, new Object[]{seq,menuId});
		}else{
			this.getJdbcTemplate().update(new PreparedStatementCreator() {
				@Override
				public PreparedStatement createPreparedStatement(Connection con)
						throws SQLException {
					PreparedStatement ps = con.prepareStatement(sql, new String[]{"menu_id"});
					ps.setString(1, menu.getMenuName());
					ps.setString(2, menu.getMenuAction());
					ps.setLong(3, menu.getParentId());
					ps.setInt(4, menu.getDisplayOrder());
					ps.setInt(5,menu.getIsLeaf());
					ps.setString(6, menu.getMenuMemo());
					ps.setInt(7, menu.getStatus());
					return ps;
				}
			}, holder);
			menuId = holder.getKey().intValue();
			seq = seq+menuId+".";
			this.getJdbcTemplate().update(updateSql, new Object[]{seq,menuId});
		}
	}

	@Override
	public int deleteMenu(Long menuId) {
		String sql = "delete from sys_menu where menu_id = ?";
		return this.getJdbcTemplate().update(sql, new Object[]{menuId} );
	}

	@Override
	public int deleteMenuRole(Long menuId) {
		String sql = "delete from sys_role_menu_rel where menu_id = ?";
		return this.getJdbcTemplate().update(sql, new Object[]{menuId} );
	}

	@Override
	public int updateMenu(Menu menu) {
		String sql = "update sys_menu t set t.menu_name =?,t.menu_action=?,"
				+ "t.display_order=?, t.is_leaf=?,t.menu_memo=? where menu_id = ?";
		return this.getJdbcTemplate().update(sql, 
				new Object[]{menu.getMenuName(),menu.getMenuAction(),menu.getDisplayOrder(),
					menu.getIsLeaf(),menu.getMenuMemo(),menu.getMenuId()} );
	}

	@Override
	public List<Menu> queryMenusByPage(PageObj pageObj, String menuName,
			String menuLevel, String seq) {
		String sql = "select t.menu_id,t.menu_name,t.menu_action,t.menu_level,t.parent_menu_id,"
				+ "t.display_order,t.pic_url,t.menu_seq,t.is_leaf,t.menu_memo,t.status "
				+ "from sys_menu t where 1=1  and t.status=1 ";
		if(menuName != null){
			sql += "  and  t.menu_name like '%" +menuName+ "%' ";
	    }
	    if(menuLevel != null){
	    	sql += "  and t.menu_level = '" + menuLevel + "' ";
	    }
	    if(seq != null){
	    	sql += "  and t.menu_seq like '" + seq + "%' ";
	    }
	    sql += " order by t.menu_level";
		 List<Menu> menus= this.queryByPage(sql,  new RowMapper<Menu>(){
				@Override
				public Menu mapRow(ResultSet rs, int rowNum)
						throws SQLException {
					Menu menu = new Menu();
					menu.setMenuId(rs.getLong("menu_id"));
					menu.setMenuName(rs.getString("menu_name"));
					menu.setMenuMemo(rs.getString("menu_memo"));
					menu.setDisplayOrder(rs.getInt("display_order"));
					menu.setIsLeaf(rs.getInt("is_leaf"));
					menu.setMenuAction(rs.getString("menu_action"));
					menu.setMenuSeq(rs.getString("menu_seq"));
					menu.setParentId(rs.getLong("parent_menu_id"));
					menu.setStatus(rs.getInt("status"));
					return menu;
				}}, pageObj);
		 return menus;
	}

	public Map<String, List<String>> getMenuRoles1() {
		String sql = "select t2.menu_action, t1.role_id " +
					"  from sys_role_menu_rel t0, sys_role t1, sys_menu t2 " + 
					" where t0.menu_id = t2.menu_id " + 
					"   and t2.status = 1 " + 
					"   and t0.role_id = t1.role_id " + 
					"   and t1.status = 1 ";
		return this.getJdbcTemplate().query(sql, new ResultSetExtractor<Map<String,List<String>>>(){

			@Override
			public Map<String,List<String>> extractData(ResultSet rs) throws SQLException, DataAccessException {
				Map<String,List<String>> result = new ConcurrentHashMap<String, List<String>>();
				while(rs.next()){
					String tempMenu = rs.getString("menu_action");
					String tempRole = rs.getString("role_id");
					List<String> roleList = result.get(tempMenu);
					if(roleList == null){
						roleList = new ArrayList<String>();
						result.put(tempMenu, roleList);
					}
					roleList.add(tempRole);
				}
				return result;
			}
			
		});
	}
	
	/**
	 * 查询每个菜单及其对应的角色列表
	 */
	@Override
	public Map<String, List<String>> getMenuRoles() {
		String sql = "select t0.menu_action,t1.role_id " +
					"from  sys_menu t0 " + 
					"left join sys_role_menu_rel t1 on t0.menu_id=t1.menu_id " + 
					"where t0.status=1 and t0.is_leaf=1";
		return this.getJdbcTemplate().query(sql, new ResultSetExtractor<Map<String,List<String>>>(){

			@Override
			public Map<String,List<String>> extractData(ResultSet rs) throws SQLException, DataAccessException {
				Map<String,List<String>> result = new ConcurrentHashMap<String, List<String>>();
				while(rs.next()){
					String tempMenu = rs.getString("menu_action");
					String tempRole = rs.getString("role_id");
					List<String> roleList = result.get(tempMenu);
					if(roleList == null){
						roleList = new ArrayList<String>();
						result.put(tempMenu, roleList);
					}
					if(tempRole != null){
						roleList.add(tempRole);
					}
				}
				return result;
			}
			
		});
	}

}
