package usi.sys.dao.impl4oracle;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import usi.common.annotation.OracleDb;
import usi.sys.dao.JdbcDaoSupport4oracle;
import usi.sys.dao.RoleDao;
import usi.sys.dto.PageObj;
import usi.sys.dto.StaffInfo;
import usi.sys.entity.Role;
import usi.sys.entity.Staff;
import usi.sys.util.CommonUtil;
import usi.sys.util.ConstantUtil;

@OracleDb
@Repository
public class RoleDaoImpl extends JdbcDaoSupport4oracle implements RoleDao{
	/**
	 * 分页查找出所有的角色
	 * @return
	 */
	public List<Role> queryAllRoles(Role role) {
		String sql ="select t.role_id,t.role_name,k.menu_id,t.role_code,t.role_desc,t.status, "
				+ "(select count(*) from sys_staff_role_rel j where j.role_id=t.role_id) staff_size "
				+ "from sys_role t,sys_role_menu_rel u,sys_menu k "
				+ "where t.role_id=u.role_id(+) and k.menu_id(+)=u.menu_id  and t.status = 1 ";
		if(CommonUtil.hasValue(role.getRoleName())) {
			sql += " and t.role_name  like '%" + role.getRoleName()+ "%'";
		}
		sql+=" order by t.role_id desc ";
		final List<Role> roles = new ArrayList<Role>();
		final List<Object> roleIds = new ArrayList<Object>();
		
		this.getJdbcTemplate().query(sql,new RowCallbackHandler() {

			@Override
			public void processRow(ResultSet rs) throws SQLException {
				Integer roleId = rs.getInt("role_id");
				Long menuId = rs.getLong("menu_id");
				if(!roleIds.contains(roleId)) {
					roleIds.add(roleId);
					//新建角色
					Role role = new Role();
					role.setRoleId(roleId);
					role.setRoleName(rs.getString("role_name"));
					role.setRoleDesc(rs.getString("role_desc"));
					role.setRoleCode(rs.getString("role_code"));
					role.setState(rs.getInt("status"));
					role.setStaffSize(rs.getInt("staff_size"));
					List<Long> menuIds = new ArrayList<Long>();
					if(menuId!=0) {
						menuIds.add(menuId);
					}
					role.setMenuIds(menuIds);
					roles.add(role);
				} else {
					roles.get(roleIds.indexOf(roleId)).getMenuIds().add(menuId);
				}
			}
		});
		
		return roles;
	}
	
	/**
	 * 查询角色的总记录数
	 * @return
	 */
	public int queryAllRolesCount(){
		String queryAllCountSql = "select count(1) from sys_role t where t.status = 1";
		return this.getJdbcTemplate().queryForObject(queryAllCountSql,Integer.class);
	}
	
	/**
	 * 插入角色
	 * @param role
	 */
	public void insertRole(Role role) {
		String sql = "insert into sys_role(role_id,role_name,role_code,role_desc,status) "+
					" values(sys_role_seq.nextval, ?, ?, ?,?)";
		this.getJdbcTemplate().update(sql, role.getRoleName(), role.getRoleCode(),
					role.getRoleDesc(),role.getState());
	}
	
	/**
	 * 根据角色ID修改角色
	 * @param role
	 */
	public void updateRole(Role role) {
		String sql = "update sys_role set role_name=?, role_desc=?, role_code=?  where role_id=?";
		this.getJdbcTemplate().update(sql, role.getRoleName(), role.getRoleDesc(),
				role.getRoleCode(), role.getRoleId());
	}
	
	/**
	 * 删除角色的菜单权限
	 * @param role
	 */
	public void deleteRoleMenus(Role role) {
		String sql = "delete from sys_role_menu_rel t where t.role_id = ?";
		this.getJdbcTemplate().update(sql, role.getRoleId());
	}
	
	/**
	 * 批量添加角色的菜单权限
	 * @param role
	 */
	public void batchSaveRoleMenus(final Role role) {
		String sql = "insert into sys_role_menu_rel(role_id, menu_id) values(?, ?)";
		this.getJdbcTemplate().batchUpdate(sql, new BatchPreparedStatementSetter () {
			@Override
			public void setValues(PreparedStatement ps, int i)
					throws SQLException {
				ps.setLong(1, role.getRoleId());
				ps.setLong(2, role.getMenuIds().get(i));
			}

			@Override
			public int getBatchSize() {
				return role.getMenuIds().size();
			}});
	}
	
	/**
	 * 根据角色ID查询该角色下所有的人员
	 * @param pageObj
	 * @param staff
	 * @param roleId
	 * @return
	 */
	public List<StaffInfo> queryPageStaffsByRoleId(PageObj pageObj, Staff staff, String roleId) {
		String sql = "select t1.role_id,t3.user_id, t3.staff_id,t3.operator_name," + 
					"       t3.status,t0.full_org_name,t3.org_id" + 
					"  from sys_role t1, sys_staff_role_rel t2, sys_staff t3," + 
					"       (select org_id, substr(sys_connect_by_path(org_name, '→'), 2) as full_org_name" + 
					"        from sys_org" + 
					"        where status=1" + 
					"            start with org_id=" + 
					"               (select org_id from sys_org where parent_org_id="+ConstantUtil.ORG_ROOT_PARENT+")" + 
					"            connect by prior org_id=parent_org_id) t0" + 
					" where t1.role_id = t2.role_id" + 
					"   and t3.staff_id = t2.staff_id" + 
					"   and t3.org_id = t0.org_id" + 
					"   and t3.status = 1" + 
					"   and t1.role_id = ?";
		if(CommonUtil.hasValue(staff.getUserId())) {
			sql += " and t3.user_id like '%" + staff.getUserId()+ "%'";
		}
		if(CommonUtil.hasValue(staff.getOperatorName())) {
			sql += " and t3.operator_name like '%" + staff.getOperatorName() + "%'";
		}
		sql += " order by t3.create_time";
		return this.queryByPage(sql, new Object[]{roleId}, new RowMapper<StaffInfo>() {

			@Override
			public StaffInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
				StaffInfo staff = new StaffInfo();
				staff.setStaffId(rs.getLong("staff_id"));
				staff.setUserId(rs.getString("user_id"));
				staff.setOperatorName(rs.getString("operator_name"));
				staff.setOrgId(rs.getLong("org_id"));
				staff.setOrgName(rs.getString("full_org_name"));
				return staff;
			}
		}, pageObj);
	}
	
	/**
	 * 批量删除角色下的成员
	 * @param role
	 */
	public void batchDeleteRoles(final Role role) {
		String sql = "delete from sys_staff_role_rel t where t.role_id = ? and t.staff_id= ?";
		this.getJdbcTemplate().batchUpdate(sql, new BatchPreparedStatementSetter () {
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				ps.setLong(1, role.getRoleId());
				ps.setLong(2, role.getStaffIds().get(i));
			}

			@Override
			public int getBatchSize() {
				return role.getStaffIds().size();
			}});
	}
	
	/**
	 * 根据角色ID查询出所有的未添加进该角色的员工,用于选择添加
	 * @param pageObj
	 * @param staff
	 * @param roleId
	 * @return
	 */
	public List<StaffInfo> queryPageAllOtherStaffsByRoleId(PageObj pageObj, Staff staff, String roleId) {
		String sql = "select t1.user_id, t1.staff_id, t1.operator_name," + 
					"      t1.status,t1.org_id,t0.full_org_name" + 
					"  from sys_staff t1," + 
					"       (select org_id, substr(sys_connect_by_path(org_name, '→'), 2) as full_org_name" + 
					"        from sys_org" + 
					"        where status=1" + 
					"            start with org_id=" + 
					"                 (select org_id from sys_org where parent_org_id="+ConstantUtil.ORG_ROOT_PARENT+")" + 
					"            connect by prior org_id = parent_org_id)  t0" + 
					" where t1.staff_id not in" + 
					"       (select u.staff_id from sys_staff_role_rel u where u.role_id=?)" + 
					"   and t1.status=1" + 
					"   and t1.org_id=t0.org_id";

		if(CommonUtil.hasValue(staff.getUserId())) {
			sql += " and t1.user_id like '%" + staff.getUserId() + "%'";
		}
		if(CommonUtil.hasValue(staff.getOperatorName())) {
			sql += " and t1.operator_name like '%" + staff.getOperatorName() + "%'";
		}
		sql += " order by t1.create_time";
		return this.queryByPage(sql, new Object[]{roleId}, new RowMapper<StaffInfo>() {

			@Override
			public StaffInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
				StaffInfo staff = new StaffInfo();
				staff.setStaffId(rs.getLong("staff_id"));
				staff.setUserId(rs.getString("user_id"));
				staff.setOperatorName(rs.getString("operator_name"));
				staff.setOrgName(rs.getString("full_org_name"));
				return staff;
			}
		}, pageObj);
	}
	
	/**
	 * 批量添加角色的成员
	 * @param role
	 */
	public void batchSaveRoleStaffs(final  Role role) {
		String sql = "insert into sys_staff_role_rel(role_id, staff_id) values(?, ?)";
		this.getJdbcTemplate().batchUpdate(sql, new BatchPreparedStatementSetter () {
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				ps.setLong(1, role.getRoleId());
				ps.setLong(2, role.getStaffIds().get(i));
			}

			@Override
			public int getBatchSize() {
				return role.getStaffIds().size();
			}});
	}
	
	/**
	 * 删除角色(逻辑删除)
	 * @param roleId
	 */
	public void deleteRoleById(Integer roleId) {
		String sql = "update sys_role t set t.status=0 where t.role_id = ?";
		this.getJdbcTemplate().update(sql, roleId);
	}
	
	@Override
	public int getRoleCodeCount(String roleCode) {
		String sql = "select count(1) from sys_role t where t.role_code=? and t.status=1";
		return this.getJdbcTemplate().queryForObject(sql, Integer.class, roleCode);
	}
}
