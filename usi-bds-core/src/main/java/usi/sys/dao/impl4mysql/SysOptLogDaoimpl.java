package usi.sys.dao.impl4mysql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import usi.common.annotation.MysqlDb;
import usi.sys.dao.JdbcDaoSupport4mysql;
import usi.sys.dao.SysOptLogDao;
import usi.sys.entity.BusiDict;
import usi.sys.entity.Menu;
import usi.sys.entity.Org;
import usi.sys.entity.Role;
import usi.sys.entity.Staff;
import usi.sys.entity.SysOptLog;
import usi.sys.entity.SysScene;

@MysqlDb
@Repository
public class SysOptLogDaoimpl extends JdbcDaoSupport4mysql implements SysOptLogDao   {
	//保存日志的sql
	private static final String INSERT_LOG = "insert into sys_opt_log(user_id ,"+
											" opt_name ,opt_module ,opt_ip ,opt_content)"+
											" values(?,?,?,?,?)";
	
	private static final String GET_BUSIDICT_BY_ID = "select * from sys_dic t where t.dic_id=? ";
	
	private static final String GET_SYSSCENE_BY_CODE = "select * from sys_scene t where t.busi_scene_code=? ";
	
	private static final String GET_MENU_BY_MENUID = "select * from sys_menu t where t.menu_id=? ";
	
	private static final String GET_ROLE_BY_ROLEID = "select * from sys_role t where t.role_id=? ";
	
	private static final String GET_ROLE_MENUS_BY_ROLEID = "select t.menu_id from sys_role_menu_rel t where t.role_id=? ";
	
	private static final String GET_ORG_BY_ORGID = "select * from sys_org t where t.org_id=? ";
	
	private static final String GET_STAFF_BY_STAFFID = "select t.*,t1.* from sys_staff t,sys_staff_attr t1 "+
													"where t.staff_id=t1.staff_id and t.staff_id=? ";
	
	class StaffMapper implements RowMapper<Staff> {

		@Override
		public Staff mapRow(ResultSet rs, int rowNum) throws SQLException {
			Staff item = new Staff();
			item.setStaffId(rs.getLong("staff_id"));
			item.setUserId(rs.getString("user_id"));
			item.setOperatorName(rs.getString("operator_name"));
			item.setGender(rs.getInt("gender"));
			item.setOrgId(rs.getLong("org_id"));
			item.setPassword(rs.getString("password"));
			item.setDuration(rs.getInt("duration"));
			if(rs.getTimestamp("pwd_last_mod_time") != null){
				item.setPwdlastModTime(new Date(rs.getTimestamp("pwd_last_mod_time").getTime()));
			} else {
				item.setPwdlastModTime(null);
			}
			item.setEmail(rs.getString("email"));
			item.setHomeTel(rs.getString("h_tel"));
			item.setHomeAddress(rs.getString("h_address"));
			item.setZipcode(rs.getString("h_zip_code") == null ? null : rs.getInt("h_zip_code"));
			item.setIdCardNum(rs.getString("id_card_nbr"));
			if(rs.getTimestamp("birthday") != null){
				item.setBirthday(new Date(rs.getTimestamp("birthday").getTime()));
			} else {
				item.setBirthday(null);
			}
			item.setDegree(rs.getString("degree") == null ? null : rs.getInt("degree"));
			item.setParty(rs.getString("party") == null ? null : rs.getInt("party"));
			item.setMobileNum(rs.getString("mobile_nbr"));
			if(rs.getTimestamp("enter_time") != null){
				item.setEnterTime(new Date(rs.getTimestamp("enter_time").getTime()));
			} else {
				item.setEnterTime(null);
			}
			item.setEmpType(rs.getString("emp_type") == null ? null : rs.getInt("emp_type"));
			item.setPicUrl(rs.getString("pic_url"));
			item.setCreateTime(new Date(rs.getTimestamp("create_time").getTime()));
			if(rs.getTimestamp("last_mod_time") != null){
				item.setLastModTime(new Date(rs.getTimestamp("last_mod_time").getTime()));
			} else {
				item.setLastModTime(null);
			}
			item.setIsOnduty(rs.getString("is_onduty") == null ? null : rs.getInt("is_onduty"));
			item.setStatus(rs.getInt("status"));
			item.setOtel(rs.getString("o_tel"));
			item.setOzipCode(rs.getString("o_zip_code") == null ? null : rs.getInt("o_zip_code"));
			item.setOaddres(rs.getString("o_addres"));
			item.setIndexCode(rs.getString("index_code"));
			item.setPerformanceSalary(rs.getString("performance_salary") == null ? null : rs.getInt("performance_salary"));
			item.setPositionSalary(rs.getString("position_salary") == null ? null : rs.getInt("position_salary"));
			item.setIsLock(rs.getInt("is_lock"));
			item.setPwdErrTimes(rs.getInt("pwd_err_times"));
			item.setLstErrPwdTime(new Date(rs.getTimestamp("lst_err_pwd_time").getTime()));
			return item;
		}
		
	}
	
	class OrgMapper implements RowMapper<Org> {

		@Override
		public Org mapRow(ResultSet rs, int rowNum) throws SQLException {
			Org item = new Org();
			item.setOrgId(rs.getLong("org_id"));
			item.setOrgName(rs.getString("org_name"));
			item.setParentOrgId(rs.getLong("parent_org_id"));
			item.setOrgSeq(rs.getString("org_seq"));
			item.setOrgTypeId(rs.getInt("org_type_id"));
			item.setOrgGrade(rs.getInt("org_grade"));
			item.setOrgContacter(rs.getString("org_contacter"));
			item.setContactTel(rs.getString("contact_tel"));
			item.setOrgAddress(rs.getString("org_address"));
			item.setIsLeaf(rs.getInt("is_leaf"));
			item.setDisplayOrder(rs.getInt("display_order"));
			item.setMemo(rs.getString("memo"));
			item.setStatus(rs.getInt("status"));
			return item;
		}
		
	}
	
	class RoleMapper implements RowMapper<Role> {
		
		@Override
		public Role mapRow(ResultSet rs, int rowNum) throws SQLException {
			Role item = new Role();
			item.setRoleId(rs.getInt("role_id"));
			item.setRoleCode(rs.getString("role_code"));
			item.setRoleDesc(rs.getString("role_desc"));
			item.setRoleName(rs.getString("role_name"));
			item.setState(rs.getInt("status"));
			return item;
		}
	}
	
	class BusiDictMapper implements RowMapper<BusiDict> {
		
		@Override
		public BusiDict mapRow(ResultSet rs, int rowNum) throws SQLException {
			BusiDict dict = new BusiDict();
			dict.setBusiSceneCode(rs.getString("busi_scene_code"));
			dict.setCreateStaff(rs.getString("create_staff"));
			dict.setCreateTime(new Date(rs.getTimestamp("create_time").getTime()));
			dict.setDicCode(rs.getString("dic_code"));
			dict.setDicId(rs.getLong("dic_id"));
			dict.setDicName(rs.getString("dic_name"));
			dict.setDisplayOrder(rs.getInt("display_order"));
			if(rs.getTimestamp("last_modi_time") != null){
				dict.setLastModiTime(new Date(rs.getTimestamp("last_modi_time").getTime()));
			} else {
				dict.setLastModiTime(null);
			}
			dict.setStatus(rs.getInt("status"));
			return dict;
		}
	}

	class SysSceneMapper implements RowMapper<SysScene> {
		
		@Override
		public SysScene mapRow(ResultSet rs, int rowNum) throws SQLException {
			SysScene item = new SysScene();
			item.setBusiSceneCode(rs.getString("busi_scene_code"));
			item.setBusiSceneName(rs.getString("busi_scene_name"));
			item.setCreateStaff(rs.getString("create_staff"));
			item.setCreateTime(new Date(rs.getTimestamp("create_time").getTime()));
			item.setDisplayOrder(rs.getInt("display_order"));
			if(rs.getTimestamp("last_modi_time") != null){
				item.setLastModiTime(new Date(rs.getTimestamp("last_modi_time").getTime()));
			} else {
				item.setLastModiTime(null);
			}
			item.setSceneDesc(rs.getString("scene_desc"));
			item.setStatus(rs.getInt("status"));
			return item;
		}
	}
	
	class MenuMapper implements RowMapper<Menu> {
		
		@Override
		public Menu mapRow(ResultSet rs, int rowNum) throws SQLException {
			Menu item = new Menu();
			item.setMenuId(rs.getLong("menu_id"));
			item.setMenuName(rs.getString("menu_name"));
			item.setParentId(rs.getLong("parent_menu_id"));
			item.setMenuAction(rs.getString("menu_action"));
			item.setDisplayOrder(rs.getInt("display_order"));
			item.setMenuSeq(rs.getString("menu_seq"));
			item.setIsLeaf(rs.getInt("is_leaf"));
			item.setMenuMemo(rs.getString("menu_memo"));
			item.setStatus(rs.getInt("status"));
			return item;
		}
	}
	
	private RowMapper<BusiDict> getBusiDictRowMapper(){
		return new BusiDictMapper();
	}
	private RowMapper<SysScene> getSysSceneMapper(){
		return new SysSceneMapper();
	}
	private RowMapper<Menu> getMenuMapper(){
		return new MenuMapper();
	}
	private RowMapper<Role> getRoleMapper(){
		return new RoleMapper();
	}
	private RowMapper<Org> getOrgMapper(){
		return new OrgMapper();
	}
	private RowMapper<Staff> getStaffIdMapper(){
		return new StaffMapper();
	}
	
	/**
	 * 保存操作日志
	 */
	@Override
	public void insertLog(final SysOptLog sysOptLog) {
		this.getJdbcTemplate().update(INSERT_LOG, new PreparedStatementSetter() {
			
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setString(1, sysOptLog.getUserId());
				ps.setString(2, sysOptLog.getOptName());
				ps.setString(3, sysOptLog.getOptModule());
				ps.setString(4, sysOptLog.getOptIp());
				ps.setString(5, sysOptLog.getOptContent());
			}
		});
	}

	/**
	 * 根据ID查询字典项
	 */
	@Override
	public BusiDict getBusiDictById(Long dicId) {
		Object[] params = new Object[]{dicId};
		return this.getJdbcTemplate().queryForObject(GET_BUSIDICT_BY_ID, params, getBusiDictRowMapper());
	}
	
	/**
	 * 根据编码查询场景信息
	 */
	@Override
	public SysScene getSysSceneByCode(String busiSceneCode) {
		Object[] params = new Object[]{busiSceneCode};
		return this.getJdbcTemplate().queryForObject(GET_SYSSCENE_BY_CODE, params, getSysSceneMapper());
	}
	
	/**
	 * 根据menuId查询菜单信息
	 */
	@Override
	public Menu getMenuByMenuId(Long menuId) {
		Object[] params = new Object[]{menuId};
		return this.getJdbcTemplate().queryForObject(GET_MENU_BY_MENUID, params, getMenuMapper());
	}
	
	/**
	 * 根据roleId查询角色信息
	 */
	@Override
	public Role getRoleByRoleId(Integer roleId) {
		Object[] params = new Object[]{roleId};
		return this.getJdbcTemplate().queryForObject(GET_ROLE_BY_ROLEID, params, getRoleMapper());
	}
	
	/**
	 * 根据roleId查询其拥有的菜单(返回菜单ID字段)
	 */
	@Override
	public List<Long> getRoleMenusByRoleId(Integer roleId) {
		Object[] params = new Object[]{roleId};
		return this.getJdbcTemplate().queryForList(GET_ROLE_MENUS_BY_ROLEID, params, Long.class);
	}
	
	/**
	 * 根据orgId查询机构信息
	 */
	@Override
	public Org getOrgByOrgId(Long orgId) {
		Object[] params = new Object[]{orgId};
		return this.getJdbcTemplate().queryForObject(GET_ORG_BY_ORGID, params, getOrgMapper());
	}
	
	/**
	 * 根据staffId查询人员信息
	 */
	@Override
	public Staff getStaffByStaffId(Long staffId) {
		Object[] params = new Object[]{staffId};
		return this.getJdbcTemplate().queryForObject(GET_STAFF_BY_STAFFID, params, getStaffIdMapper());
	}
	
}