package usi.sys.dao.impl4oracle;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import usi.common.annotation.OracleDb;
import usi.sys.dao.JdbcDaoSupport4oracle;
import usi.sys.dao.OrgDao;
import usi.sys.dto.OrgDto;
import usi.sys.dto.OrgInfo;
import usi.sys.entity.Org;
import usi.sys.util.ConstantUtil;

@OracleDb
@Repository
public class OrgDaoImpl extends JdbcDaoSupport4oracle implements OrgDao {
	
	/**
	 * 逐级加载机构树
	 * @param orgId
	 * @return
	 */
	public List<OrgInfo> getOrgTree(Long orgId){
		String sql = "select t.org_id,t.org_name,t.org_grade,t.org_contacter,t.contact_tel,t.display_order," +
				" t.org_address,t.parent_org_id,t.org_seq,t.org_type_id,t.is_leaf,t.memo "+
				" from sys_org t where t.status=1 and t.parent_org_id = ? order by t.display_order ";
		List<OrgInfo> list = this.getJdbcTemplate().query(sql, new Object[]{orgId}, new RowMapper<OrgInfo>(){

			@Override
			public OrgInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
				OrgInfo org = new OrgInfo();
				org.setId(rs.getLong("org_id"));
				org.setText(rs.getString("org_name"));
				org.setOrgId(rs.getLong("org_id"));
				org.setOrgName(rs.getString("org_name"));
				org.setOrgContacter(rs.getString("org_contacter"));
				org.setContactTel(rs.getString("contact_tel"));
				org.setDisplayOrder(rs.getInt("display_order"));
				org.setOrgAddress(rs.getString("org_address"));
				org.setParentOrgId(rs.getLong("parent_org_id"));
				org.setOrgSeq(rs.getString("org_seq"));
				org.setOrgTypeId(rs.getInt("org_type_id"));
				org.setIsLeaf(rs.getInt("is_leaf"));
				org.setMemo(rs.getString("memo"));
				org.setOrgGrade(rs.getInt("org_grade"));
				return org;
			}
			
		});
		return list;
	}
	
	/**
	 * 获取机构树数据(用于同步树)[只有org_id、org_name、parent_org_id、org_seq字段]
	 * @return
	 */
	public List<OrgDto> queryAllOrg(){
		String sql = "select a.org_id,a.org_name,a.parent_org_id,a.org_seq" +
					" from sys_org a where a.status=1 order by a.display_order ";
		return this.getJdbcTemplate().query(sql, new RowMapper<OrgDto>(){

			@Override
			public OrgDto mapRow(ResultSet rs, int rowNum) throws SQLException {
				OrgDto orgDto = new OrgDto();
				orgDto.setId(rs.getLong("org_id"));
				orgDto.setText(rs.getString("org_name"));
				orgDto.setOrgSeq(rs.getString("org_seq"));
				orgDto.setParentOrgId(rs.getString("parent_org_id") == null ? null : rs.getLong("parent_org_id"));	//防止为空时自动赋值0
				return orgDto;
			}
			
		});
	}
	
	/**
	 * 保存子机构
	 * @param org
	 */
	public long saveSubOrg(final Org org){
		KeyHolder kh = new GeneratedKeyHolder();
		this.getJdbcTemplate().update(new PreparedStatementCreator(){

			@Override
			public PreparedStatement createPreparedStatement(Connection con)throws SQLException {
				String sql = "insert into sys_org (org_id, org_name, parent_org_id, org_type_id," +
						" org_grade, org_contacter, contact_tel, org_address, is_leaf, display_order, memo) "+
						" values (sys_org_seq.nextval, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ";
				PreparedStatement ps = con.prepareStatement(sql,new String[]{"org_id"});
				ps.setString(1, org.getOrgName());
				ps.setLong(2, org.getParentOrgId());
				ps.setInt(3, org.getOrgTypeId());
				ps.setInt(4, org.getOrgGrade());
				ps.setString(5, org.getOrgContacter());
				ps.setString(6, org.getContactTel());
				ps.setString(7, org.getOrgAddress());
				ps.setInt(8, org.getIsLeaf());
				ps.setInt(9, org.getDisplayOrder());
				ps.setString(10, org.getMemo());
				return ps;
			}
			
		},kh);
		return kh.getKey().longValue();
	}
	
	/**
	 * 保存机构序列
	 * @param seq
	 * @param orgId
	 */
	public void saveOrgSeq(final String seq,final long orgId){
		String sql = "update sys_org so set so.org_seq = ? where so.org_id=? ";
		this.getJdbcTemplate().update(sql, new PreparedStatementSetter(){

			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setString(1, seq);
				ps.setLong(2, orgId);
			}
			
		});
	}
	
	/**
	 * 更新机构信息
	 * @param org
	 */
	public void updateOrg(final Org org){
		String sql = "update sys_org a " +
					"   set a.org_name = ?, a.org_type_id = ?, " + 
					"       a.org_grade = ?, a.org_contacter = ?, " + 
					"       a.contact_tel = ?, a.org_address = ?, " + 
					"       a.memo = ?, a.display_order = ? " + 
//					"       ,a.is_leaf = ? " + 
					" where a.org_id = ? ";

		this.getJdbcTemplate().update(sql, new PreparedStatementSetter() {
			
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setString(1, org.getOrgName());
				ps.setInt(2,org.getOrgTypeId());
				ps.setInt(3, org.getOrgGrade());
				ps.setString(4, org.getOrgContacter());
				ps.setString(5, org.getContactTel());
				ps.setString(6, org.getOrgAddress());
				ps.setString(7, org.getMemo());
//				ps.setInt(8, org.getIsLeaf());
				ps.setInt(8, org.getDisplayOrder());
				ps.setLong(9, org.getOrgId());
			}
		});
	}
	
	/**
	 * 根据主键逻辑删除机构
	 * @param orgId
	 */
	public void delOrg(final Long orgId){
		String sql = "update sys_org set status=0 where org_id=? ";
		this.getJdbcTemplate().update(sql, new PreparedStatementSetter(){

			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setLong(1, orgId);
			}
		});
	}
	
	/**
	 * 检查机构是否存在子机构
	 * @param orgId
	 * @return
	 */
	public int checkHasSubOrg(final Long orgId){
		String sql = "select count(1) from sys_org where parent_org_id=? and status=1 ";
		return this.getJdbcTemplate().queryForObject(sql, Integer.class, orgId);
	}
	
	/**
	 * 检查机构下是否存在员工
	 * @param orgId
	 * @return
	 */
	public int checkHasStaff(final Long orgId){
		String sql = "select count(1) from sys_staff where org_id=? and status=1 ";
		return this.getJdbcTemplate().queryForObject(sql, Integer.class, orgId);
	}
	
	/**
	 * 取得所有的机构二级节点[只有org_id和org_name信息]
	 * @return
	 */
	public List<Org> getSecLevelOrg(){
		String sql = "select o.org_id,o.org_name from sys_org o where o.status=1 " +
				" and o.parent_org_id=(select o.org_id from sys_org o "+
				" where o.parent_org_id="+ConstantUtil.ORG_ROOT_PARENT+") ";
		return this.getJdbcTemplate().query(sql, new RowMapper<Org>(){

			@Override
			public Org mapRow(ResultSet rs, int rowNum) throws SQLException {
				Org temp = new Org();
				temp.setOrgId(rs.getLong("org_id"));
				temp.setOrgName(rs.getString("org_name"));
				return temp;
			}
			
		});
	}
	
	/**
	 * 查询机构根节点信息[只有org_id和org_name信息]
	 * @return
	 */
	public Org getOrgRoot(){
		String sql = "select o.org_id,o.org_name from sys_org o "+
					" where o.parent_org_id="+ConstantUtil.ORG_ROOT_PARENT;
		List<Org> list = this.getJdbcTemplate().query(sql, new RowMapper<Org>(){

			@Override
			public Org mapRow(ResultSet rs, int rowNum) throws SQLException {
				Org temp = new Org();
				temp.setOrgId(rs.getLong("org_id"));
				temp.setOrgName(rs.getString("org_name"));
				return temp;
			}
			
		});
		return list.size() == 0 ? null : list.get(0);
	}

}
