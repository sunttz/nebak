package usi.sys.dao.impl4mysql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import usi.common.annotation.MysqlDb;
import usi.sys.dao.JdbcDaoSupport4mysql;
import usi.sys.dao.RoleDao;
import usi.sys.dto.PageObj;
import usi.sys.dto.StaffInfo;
import usi.sys.entity.Role;
import usi.sys.entity.Staff;
import usi.sys.util.CommonUtil;
import usi.sys.util.ConstantUtil;

/**
 * @author chen.kui
 * @date 2014年9月28日15:28:43
 */
@MysqlDb
@Repository
public class RoleDaoImpl extends JdbcDaoSupport4mysql implements RoleDao {

	@Override
	public List<Role> queryAllRoles(Role role) {
		String sql = "select t.role_id,t.role_name,k.menu_id,t.role_code,t.role_desc,t.status,"
				+ "(select count(*) num from sys_staff_role_rel j where j.role_id = t.role_id) staff_size "
				+ "from sys_role t left join sys_role_menu_rel u on (t.role_id = u.role_id ) "
				+ "left join sys_menu k on u.menu_id = k.menu_id "
				+ "where t.status = 1";
		if (CommonUtil.hasValue(role.getRoleName())) {
			sql += " and role_name like '%" + role.getRoleName() + "%'";
		}
		sql += " order by role_id desc ";
		final List<Role> roles = new ArrayList<Role>();
		final List<Object> roleIds = new ArrayList<Object>();

		this.getJdbcTemplate().query(sql, new RowCallbackHandler() {

			@Override
			public void processRow(ResultSet rs) throws SQLException {
				Integer roleId = rs.getInt("role_id");
				Long menuId = rs.getLong("menu_id");
				if (!roleIds.contains(roleId)) {
					roleIds.add(roleId);
					// 新建角色
					Role role = new Role();
					role.setRoleId(roleId);
					role.setRoleName(rs.getString("role_name"));
					role.setRoleDesc(rs.getString("role_desc"));
					role.setRoleCode(rs.getString("role_code"));
					role.setState(rs.getInt("status"));
					role.setStaffSize(rs.getInt("staff_size"));
					List<Long> menuIds = new ArrayList<Long>();
					if (menuId != 0) {
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

	@Override
	public int queryAllRolesCount() {
		String queryAllCountSql = "select count(1) rn from sys_role t where  t.status = 1";
		return this.getJdbcTemplate().queryForObject(queryAllCountSql,
				Integer.class);
	}

	@Override
	public void insertRole(Role role) {
		String sql = "insert into sys_role(role_name,role_code,role_desc,status) values(?, ?, ?,?)";
		this.getJdbcTemplate().update(sql, role.getRoleName(),
				role.getRoleCode(), role.getRoleDesc(), role.getState());
	}

	@Override
	public void updateRole(Role role) {
		String sql = "update sys_role set role_name=?, role_desc=?, role_code=?  where role_id=?";
		this.getJdbcTemplate().update(sql, role.getRoleName(),
				role.getRoleDesc(), role.getRoleCode(), role.getRoleId());
	}

	@Override
	public void deleteRoleMenus(Role role) {
		String sql = "delete from sys_role_menu_rel where role_id = ?";
		this.getJdbcTemplate().update(sql, role.getRoleId());
	}

	@Override
	public void batchSaveRoleMenus(final Role role) {
		String sql = "insert into sys_role_menu_rel(role_id, menu_id) values(?, ?)";
		this.getJdbcTemplate().batchUpdate(sql, new BatchPreparedStatementSetter() {
					@Override
					public void setValues(PreparedStatement ps, int i)
							throws SQLException {
						ps.setLong(1, role.getRoleId());
						ps.setLong(2, role.getMenuIds().get(i));
					}

					@Override
					public int getBatchSize() {
						return role.getMenuIds().size();
					}
				});
	}

	@Override
	public List<StaffInfo> queryPageStaffsByRoleId(PageObj pageObj, Staff staff,
			String roleId) {
		String sql = "select t.role_id,k.user_id,k.staff_id,k.operator_name,k.status,"
				+ "(select u.org_name from (select org_id, cast(f_org_name_seq_gen(org_id) as char) org_name from sys_org"
				+ "             where find_in_set(parent_org_id, f_get_child_lst("
				+ ConstantUtil.ORG_ROOT_PARENT
				+ "))and parent_org_id != "
				+ ConstantUtil.ORG_ROOT_PARENT
				+ ") u where u.org_id = k.org_id) org_name,o.org_id "
				+ "from sys_role t, sys_staff_role_rel u, sys_staff k, sys_org o "
				+ "where t.role_id = u.role_id and k.staff_id = u.staff_id "
				+ "and k.status = 1 and k.org_id = o.org_id  and t.role_id = ?";
		if (CommonUtil.hasValue(staff.getUserId())) {
			sql += " and k.user_id like '%" + staff.getUserId() + "%'";
		}
		if (CommonUtil.hasValue(staff.getOperatorName())) {
			sql += " and k.operator_name like '%" + staff.getOperatorName()+ "%'";
		}
		sql += " order by k.create_time";
		return this.queryByPage(sql, new Object[] { roleId }, new RowMapper<StaffInfo>() {

					@Override
					public StaffInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
						StaffInfo staff = new StaffInfo();
						staff.setStaffId(rs.getLong("staff_id"));
						staff.setUserId(rs.getString("user_id"));
						staff.setOperatorName(rs.getString("operator_name"));
						staff.setOrgId(rs.getLong("org_id"));
						staff.setOrgName(rs.getString("org_name"));
						return staff;
					}
				}, pageObj);
	}

	@Override
	public void batchDeleteRoles(final Role role) {
		String sql = "delete from sys_staff_role_rel  where role_id = ? and staff_id= ?";
		this.getJdbcTemplate().batchUpdate(sql, new BatchPreparedStatementSetter() {
					@Override
					public void setValues(PreparedStatement ps, int i) throws SQLException {
						ps.setLong(1, role.getRoleId());
						ps.setLong(2, role.getStaffIds().get(i));
					}

					@Override
					public int getBatchSize() {
						return role.getStaffIds().size();
					}
				});
	}

	@Override
	public List<StaffInfo> queryPageAllOtherStaffsByRoleId(PageObj pageObj,
			Staff staff, String roleId) {
		String sql = "select k.user_id,k.staff_id,k.operator_name,k.status,k.org_id ,"
				+ "(select u.org_name from "
				+ "(select org_id,cast(f_org_name_seq_gen(org_id) as char) org_name from sys_org "
				+ "where find_in_set(parent_org_id,f_get_child_lst("
				+ ConstantUtil.ORG_ROOT_PARENT
				+ ")) and parent_org_id !="
				+ ConstantUtil.ORG_ROOT_PARENT
				+ ") u where u.org_id = k.org_id) org_name   "
				+ "from sys_staff k "
				+ "where k.staff_id not in "
				+ "(select u.staff_id from sys_staff_role_rel u where u.role_id = ? ) and k.status=1 ";
		if (CommonUtil.hasValue(staff.getUserId())) {
			sql += " and k.user_id  like '%" + staff.getUserId() + "%'";
		}
		if (CommonUtil.hasValue(staff.getOperatorName())) {
			sql += " and k.operator_name like '%" + staff.getOperatorName()+ "%'";
		}
		sql += " order by k.create_time";
		return this.queryByPage(sql, new Object[] { roleId }, new RowMapper<StaffInfo>() {

					@Override
					public StaffInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
						StaffInfo staff = new StaffInfo();
						staff.setStaffId(rs.getLong("staff_id"));
						staff.setUserId(rs.getString("user_id"));
						staff.setOperatorName(rs.getString("operator_name"));
						staff.setOrgName(rs.getString("org_name"));
						return staff;
					}
				}, pageObj);
	}

	@Override
	public void batchSaveRoleStaffs(final Role role) {
		String sql = "insert into sys_staff_role_rel(role_id, staff_id) values(?, ?)";
		this.getJdbcTemplate().batchUpdate(sql, new BatchPreparedStatementSetter() {
					@Override
					public void setValues(PreparedStatement ps, int i) throws SQLException {
						ps.setLong(1, role.getRoleId());
						ps.setLong(2, role.getStaffIds().get(i));
					}

					@Override
					public int getBatchSize() {
						return role.getStaffIds().size();
					}
				});
	}

	@Override
	public void deleteRoleById(Integer roleId) {
		String sql = "update sys_role set status=0 where role_id = ?";
		this.getJdbcTemplate().update(sql, roleId);
	}

	@Override
	public int getRoleCodeCount(String roleCode) {
		String sql = "select count(1) from sys_role t where t.role_code=? and t.status=1";
		return this.getJdbcTemplate().queryForObject(sql, Integer.class, roleCode);
	}

}
