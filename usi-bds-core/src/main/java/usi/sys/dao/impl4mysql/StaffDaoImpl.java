package usi.sys.dao.impl4mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import usi.common.annotation.MysqlDb;
import usi.sys.dao.JdbcDaoSupport4mysql;
import usi.sys.dao.StaffDao;
import usi.sys.dto.AuthMenu;
import usi.sys.dto.PageObj;
import usi.sys.dto.RoleDto;
import usi.sys.dto.RoleGrantInfo;
import usi.sys.dto.StaffInfo;
import usi.sys.entity.Staff;
import usi.sys.util.CommonUtil;
import usi.sys.util.ConstantUtil;

/**
 * @author chen.kui
 * @date 2014年9月28日15:28:43
 */
@MysqlDb
@Repository
public class StaffDaoImpl extends JdbcDaoSupport4mysql implements StaffDao {

	@Override
	public List<StaffInfo> queryPageStaffByCondition(PageObj pageObj, Staff staff) {
		String sql = "select a.staff_id, a.user_id, a.operator_name, a.gender, c.mobile_nbr,"
				+ " cast(f_org_name_seq_gen(o.org_id) as char) org_name "
				+ " from sys_staff a, sys_staff_attr c,sys_org o "
				+ " where a.status = 1 and a.is_onduty = 1 and a.staff_id = c.staff_id and a.org_id = o.org_id";
		if (staff != null) {
			if (CommonUtil.hasValue(staff.getUserId()))
				sql += " and a.user_id like '%" + staff.getUserId() + "%' ";
			if (CommonUtil.hasValue(staff.getOperatorName()))
				sql += " and a.operator_name like '%" + staff.getOperatorName()+ "%' ";
			if (staff.getOrgId() != null)
				sql += " and a.org_id =" + staff.getOrgId();
		}
		sql += " order by a.last_mod_time desc ";
		return this.queryByPage(sql, new RowMapper<StaffInfo>() {

			@Override
			public StaffInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
				StaffInfo staff0 = new StaffInfo();
				staff0.setStaffId(rs.getLong("staff_id"));
				staff0.setUserId(rs.getString("user_id"));
				staff0.setOperatorName(rs.getString("operator_name"));
				staff0.setMobileNum(rs.getString("mobile_nbr"));
				staff0.setOrgName(rs.getString("org_name"));
				staff0.setGender(rs.getInt("gender"));
				return staff0;
			}
		}, pageObj);
	}

	@Override
	public StaffInfo queryStaffByUserId(String userId) {
		String sql = "select t1.*,"
				+ "t2.mobile_nbr,"
				+ "t3.org_name,"
				+ "t3.org_seq,"
				+ "(case "
				+ "when ( char_length(t3.org_seq) - char_length( replace(t3.org_seq, '.' , ''))) >1 then "
				+ "substr(t3.org_seq,instr(t3.org_seq, '.') + 1, "
				+ "locate('.',t3.org_seq," + (ConstantUtil.FIRST_LEVEL_ORG_LENGTH + 2)
				+ ") - instr(t3.org_seq, '.') - 1) "
				+ "else "
				+ "substr(t3.org_seq, 0, instr(t3.org_seq, '.') - 1) "
				+ "end) area_org_id,"
				+ "substr(t3.org_seq, 0, instr(t3.org_seq, '.') - 1) root_org_id, "
		        + "(case when (unix_timestamp(now())-unix_timestamp(lst_err_pwd_time))<1800 and is_lock=1 then 1 else 0 end) real_lock "
				+ "from sys_staff t1, sys_staff_attr t2, sys_org t3 "
				+ "where t1.staff_id = t2.staff_id "
				+ "and t1.org_id = t3.org_id " + "and t1.user_id = ? "
				+ "and t1.status = 1 " + "and t1.is_onduty = 1";

		List<StaffInfo> staffs = this.getJdbcTemplate().query(sql, new RowMapper<StaffInfo>() {
					@Override
					public StaffInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
						StaffInfo staff = new StaffInfo();
						staff.setStaffId(rs.getLong("staff_id"));
						staff.setUserId(rs.getString("user_id"));
						staff.setOrgId(rs.getLong("org_id"));
						staff.setMobileNum(rs.getString("mobile_nbr"));
						staff.setOrgSeq(rs.getString("org_seq"));
						staff.setAreaOrgId(rs.getLong("area_org_id"));
						staff.setRootOrgId(rs.getLong("root_org_id"));
						staff.setOrgName(rs.getString("org_name"));
						staff.setOperatorName(rs.getString("operator_name"));
						staff.setPassword(rs.getString("password"));
						staff.setDuration(rs.getInt("duration"));
						if(rs.getTimestamp("pwd_last_mod_time") != null){
							staff.setPwdlastModTime(new Date(rs.getTimestamp("pwd_last_mod_time").getTime()));
						} else {
							staff.setPwdlastModTime(null);
						}
						staff.setRealLock(rs.getInt("real_lock"));
						return staff;
					}
				}, userId);
		return staffs.size() == 0 ? null : staffs.get(0);
	}
	
	/**
	 * 根据userid更新输入密码错误次数
	 * @param userId
	 * @return
	 * 备注：上次输入错误时间距离当前大于10分钟或者错误次数已经达到3次，更新为1次
	 * 上次输入错误时间距离当前10分钟且已达到2次（加上本次为3次），则锁定，否则不锁定
	 */
	public int uptErrPwdTimes(String userId) {
		String sql = "update sys_staff set lst_err_pwd_time=now(),"+
				" pwd_err_times=(case when (unix_timestamp(now())-unix_timestamp(lst_err_pwd_time))>600 " +
	            " or pwd_err_times+1>3 then 1 " + 
	            " else pwd_err_times+1 end), " + 
	            " is_lock= (case when (unix_timestamp(now())-unix_timestamp(lst_err_pwd_time))<600 and pwd_err_times+1>=3 then 1 " + 
	            " else 0 end) " + 
				" where user_id=?";
		return this.getJdbcTemplate().update(sql, userId);
	}
	
	@Override
	public StaffInfo queryStaffDetailById(Long staffId) {
		String sql = " select a.staff_id, a.org_id, a.user_id, a.operator_name, a.duration, "
				+ "b.email,b.o_tel, b.o_zip_code, b.o_addres, b.id_card_nbr, a.gender,b.mobile_nbr, "
				+ "cast(f_org_name_seq_gen(c.org_id) as char) org_name "
				+ "from sys_staff a, sys_staff_attr b,sys_org c "
				+ "where a.staff_id = b.staff_id and a.org_id =c.org_id and a.staff_id = ?";

		return this.getJdbcTemplate().queryForObject(sql,
				new Object[] { staffId }, new RowMapper<StaffInfo>() {

					@Override
					public StaffInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
						StaffInfo staff = new StaffInfo();
						staff.setStaffId(rs.getLong("staff_id"));
						staff.setOrgId(rs.getLong("org_id"));
						staff.setUserId(rs.getString("user_id"));
						staff.setOperatorName(rs.getString("operator_name"));
						staff.setDuration(rs.getInt("duration"));
						staff.setEmail(rs.getString("email"));
						staff.setOtel(rs.getString("o_tel"));
						staff.setOaddres(rs.getString("o_addres"));
						staff.setOzipCode(rs.getString("o_zip_code"));
						staff.setIdCardNum(rs.getString("id_card_nbr"));
						staff.setGender(rs.getInt("gender"));
						staff.setMobileNum(rs.getString("mobile_nbr"));
						staff.setOrgName(rs.getString("org_name"));
						return staff;
					}
				});
	}

	@Override
	public StaffInfo queryStaffById(Long staffId) {
		String sql = "select a.staff_id,a.org_id,a.user_id,a.operator_name, b.org_name, b.org_seq "
				+ "from sys_staff a,sys_org b where a.org_id = b.org_id and a.staff_id = ? ";
		return this.getJdbcTemplate().queryForObject(sql,
				new Object[] { staffId }, new RowMapper<StaffInfo>() {

					@Override
					public StaffInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
						StaffInfo staff = new StaffInfo();
						staff.setStaffId(rs.getLong("staff_id"));
						staff.setOrgId(rs.getLong("org_id"));
						staff.setUserId(rs.getString("user_id"));
						staff.setOperatorName(rs.getString("operator_name"));
						staff.setOrgName(rs.getString("org_name"));
						staff.setOrgSeq(rs.getString("org_seq"));
						return staff;
					}
				});
	}

	@Override
	public void batchDelStaff(final Long[] staffIds) {
		String sql = "update sys_staff set status=0,last_mod_time=now() where staff_id=? ";
		this.getJdbcTemplate().batchUpdate(sql, new BatchPreparedStatementSetter() {

					@Override
					public void setValues(PreparedStatement ps, int i) throws SQLException {
						ps.setLong(1, staffIds[i]);
					}

					@Override
					public int getBatchSize() {
						return staffIds.length;
					}
				});
	}

	@Override
	public void batchDelStaffRoleRel(final Long[] staffIds) {
		String sql = "delete from sys_staff_role_rel where staff_id=? ";
		this.getJdbcTemplate().batchUpdate(sql, new BatchPreparedStatementSetter() {

					@Override
					public void setValues(PreparedStatement ps, int i) throws SQLException {
						ps.setLong(1, staffIds[i]);
					}

					@Override
					public int getBatchSize() {
						return staffIds.length;
					}
				});
	}

	@Override
	public void batchRevokeRole(final Long staffId, final Integer[] roleIds) {
		String sql = "delete from sys_staff_role_rel where staff_id=? and role_id =? ";
		this.getJdbcTemplate().batchUpdate(sql, new BatchPreparedStatementSetter() {

					@Override
					public void setValues(PreparedStatement ps, int i) throws SQLException {
						ps.setLong(1, staffId);
						ps.setInt(2, roleIds[i]);
					}

					@Override
					public int getBatchSize() {
						return roleIds.length;
					}
				});
	}

	@Override
	public void batchGrantRole(final Long staffId, final Integer[] roleIds) {
		String sql = "insert into sys_staff_role_rel(staff_id,role_id) values (?, ?) ";
		this.getJdbcTemplate().batchUpdate(sql, new BatchPreparedStatementSetter() {

					@Override
					public void setValues(PreparedStatement ps, int i) throws SQLException {
						ps.setLong(1, staffId);
						ps.setInt(2, roleIds[i]);
					}

					@Override
					public int getBatchSize() {
						return roleIds.length;
					}
				});
	}

	@Override
	public Long insertStaffMain(final Staff staff) {
		KeyHolder keyHolder = new GeneratedKeyHolder();
		this.getJdbcTemplate().update(new PreparedStatementCreator() {

			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				String sql = "insert into sys_staff (user_id, operator_name, org_id, password, "
						+ "duration, gender, status, is_onduty, last_mod_time) "
						+ "values (?,?,?,?,?,?,?,?,now()) ";
				PreparedStatement ps = con.prepareStatement(sql, new String[] { "staff_id" });
				ps.setString(1, staff.getUserId());
				ps.setString(2, staff.getOperatorName());
				ps.setLong(3, staff.getOrgId());
				ps.setString(4, staff.getPassword());
				ps.setInt(5, staff.getDuration());
				ps.setInt(6, staff.getGender());
				ps.setInt(7, staff.getStatus());
				ps.setInt(8, staff.getIsOnduty());
				return ps;
			}

		}, keyHolder);
		return keyHolder.getKey().longValue();
	}

	@Override
	public void insertStaffAttr(final Staff staff) {
		String sql = "insert into sys_staff_attr (staff_id, index_code, email, "
				+ "mobile_nbr, o_tel, o_zip_code, o_addres, last_mod_time) "
				+ "values (? ,f_name_to_pinyin(?) ,?, ?, ?, ?, ?, now()) ";
		this.getJdbcTemplate().update(sql, new PreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setLong(1, staff.getStaffId());
				ps.setString(2, staff.getOperatorName());
				ps.setString(3, staff.getEmail());
				ps.setString(4, staff.getMobileNum());
				ps.setString(5, staff.getOtel());
				if(staff.getOzipCode() != null){
					ps.setInt(6, staff.getOzipCode());
				} else {
					ps.setNull(6, Types.INTEGER);
				}
				ps.setString(7, staff.getOaddres());
			}

		});

	}

	@Override
	public void updateStaffMain(final Staff staff) {
		String sql = "update sys_staff set operator_name=? ,duration=? ,"
				+ "gender=?, last_mod_time=now() where staff_id=? ";
		this.getJdbcTemplate().update(sql, new PreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setString(1, staff.getOperatorName());
				ps.setInt(2, staff.getDuration());
				ps.setInt(3, staff.getGender());
				ps.setLong(4, staff.getStaffId());
			}

		});
	}

	@Override
	public void resetPwd(final Staff staff) {
		String sql = "update sys_staff set password=?,is_lock=0 where staff_id=? ";
		this.getJdbcTemplate().update(sql, new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setString(1, staff.getPassword());
				ps.setLong(2, staff.getStaffId());
			}
		});
	}

	@Override
	public void updateStaffAttr(final Staff staff) {
		String sql = "update sys_staff_attr set index_code= "
				+ "f_name_to_pinyin((select operator_name from sys_staff where staff_id = ?)), "
				+ "email = ?, mobile_nbr= ?,o_tel = ?,o_zip_code = ?,o_addres = ?, "
				+ "last_mod_time = now() where staff_id = ?";
		this.getJdbcTemplate().update(sql, new PreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setLong(1, staff.getStaffId());
				ps.setString(2, staff.getEmail());
				ps.setString(3, staff.getMobileNum());
				ps.setString(4, staff.getOtel());
				if(staff.getOzipCode() != null){
					ps.setInt(5, staff.getOzipCode());
				} else {
					ps.setNull(5, Types.INTEGER);
				}
				ps.setString(6, staff.getOaddres());
				ps.setLong(7, staff.getStaffId());
			}

		});
	}

	@Override
	public List<RoleGrantInfo> queryPageRole(PageObj pageObj,
			final RoleGrantInfo roleGrantInfo) {
		String sql = "select a.role_id,a.role_code,a.role_name,a.role_desc,"
				+ "(case when b.role_id is not null then 0 else 1 end ) isgranted "
				+ "from sys_role a left join (select t.role_id,t.staff_id from sys_staff_role_rel t where t.staff_id=?) b "
				+ "on a.role_id=b.role_id " + " where a.status=1 ";
		if (CommonUtil.hasValue(roleGrantInfo.getRoleCode()))
			sql += " and a.role_code like '%" + roleGrantInfo.getRoleCode() + "%' ";
		if (CommonUtil.hasValue(roleGrantInfo.getRoleName()))
			sql += " and a.role_name like '%" + roleGrantInfo.getRoleName() + "%' ";
		if (roleGrantInfo.getIsGranted() != null)
			sql += " and (case when b.role_id is not null then 0 else 1 end) = "
					+ roleGrantInfo.getIsGranted();
		return this.queryByPage(sql,new Object[] { roleGrantInfo.getStaffId() },
				new RowMapper<RoleGrantInfo>() {

					@Override
					public RoleGrantInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
						RoleGrantInfo roleGrantInfo0 = new RoleGrantInfo();
						roleGrantInfo0.setRoleId(rs.getInt("role_id"));
						roleGrantInfo0.setRoleCode(rs.getString("role_code"));
						roleGrantInfo0.setRoleName(rs.getString("role_name"));
						roleGrantInfo0.setRoleDesc(rs.getString("role_desc"));
						roleGrantInfo0.setIsGranted(rs.getInt("isgranted"));
						roleGrantInfo0.setStaffId(roleGrantInfo.getStaffId());
						return roleGrantInfo0;
					}
				}, pageObj);
	}

	@Override
	public int checkUserId(String userId) {
		String sql = "select count(1) from sys_staff t where t.user_id = ? ";
		return this.getJdbcTemplate().queryForObject(sql, Integer.class, userId);
	}

	@Override
	public List<RoleDto> getRoleByUserId(long staffId) {
		String sql = "select role.role_id,role.role_name,role.role_code "
				+ "from sys_staff_role_rel rel,sys_role role "
				+ "where rel.role_id=role.role_id and rel.staff_id=? ";
		return this.getJdbcTemplate().query(sql, new Object[] { staffId },
				new RowMapper<RoleDto>() {
					public RoleDto mapRow(ResultSet rs, int rowNum) throws SQLException {
						RoleDto roleDto = new RoleDto();
						roleDto.setRoleId(rs.getInt("role_id"));
						roleDto.setRoleName(rs.getString("role_name"));
						roleDto.setRoleCode(rs.getString("role_code"));
						return roleDto;
					}
				});
	}

	@Override
	public List<AuthMenu> queryAuthMenusByUserId(String userId) {
		String sql = "select distinct t3.menu_id, "
				+ "t3.menu_name,t3.menu_action, "
				+ "t3.parent_menu_id,t3.is_leaf,t3.display_order "
				+ "from sys_staff t0,sys_staff_role_rel t1, "
				+ "sys_role_menu_rel t2,sys_menu t3 "
				+ "where t0.staff_id = t1.staff_id "
				+ "and t1.role_id = t2.role_id "
				+ "and t2.menu_id = t3.menu_id and t3.status = 1 "
				+ "and t0.user_id = ? order by t3.display_order ";

		return this.getJdbcTemplate().query(sql, new RowMapper<AuthMenu>() {

			@Override
			public AuthMenu mapRow(ResultSet rs, int rowNum) throws SQLException {
				AuthMenu menu = new AuthMenu();
				menu.setMenuId(rs.getLong("menu_id"));
				menu.setMenuName(rs.getString("menu_name"));
				menu.setMenuAction(rs.getString("menu_action"));
				menu.setParentId(rs.getLong("parent_menu_id"));
				menu.setIsLeaf(rs.getInt("is_leaf"));
				return menu;
			}
		}, userId);
	}

	@Override
	public int updatePwdById(long staffId, String newPwd) {
		String sql = "update sys_staff set password=?, pwd_last_mod_time=? where staff_id=?";
		return this.getJdbcTemplate().update(sql, newPwd, new Date(), staffId);
	}

	@Override
	public StaffInfo getStaffRoleInfo(long staffId) {
		String sql = "select ss.staff_id,so.org_seq,so.org_id,t3.role_name from sys_staff ss "
				+ "left join (select t1.staff_id,t2.role_name from sys_staff_role_rel t1,sys_role t2 "
				+ "where t1.role_id=t2.role_id) t3 "
				+ "on ss.staff_id=t3.staff_id ,sys_org so where ss.org_id=so.org_id and ss.staff_id=? ";
		return this.getJdbcTemplate().query(sql, new Object[] { staffId },
				new ResultSetExtractor<StaffInfo>() {

					@Override
					public StaffInfo extractData(ResultSet rs) throws SQLException, DataAccessException {
						StaffInfo item = new StaffInfo();
						item.setRoleNames(new ArrayList<String>());
						while (rs.next()) {
							item.setStaffId(rs.getLong("staff_id"));
							item.setOrgSeq(rs.getString("org_seq"));
							item.setOrgId(rs.getLong("org_id"));
							item.getRoleNames().add(rs.getString("role_name"));
						}
						return item;
					}
				});
	}

}
