package usi.sys.dao.impl4mysql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import usi.common.annotation.MysqlDb;
import usi.sys.dao.BusiDictDao;
import usi.sys.dao.JdbcDaoSupport4mysql;
import usi.sys.dto.DictDto;
import usi.sys.dto.PageObj;
import usi.sys.entity.BusiDict;
import usi.sys.entity.SysScene;
import usi.sys.util.CommonUtil;
/**
 * @author chen.kui
 * @date 2014年9月28日15:28:43
 */
@MysqlDb
@Repository
public class BusiDictDaoImpl extends JdbcDaoSupport4mysql implements BusiDictDao {

	@Override
	public List<SysScene> querySysSceneByPage(PageObj pageObj, String busiSceneCode, String busiSceneName) {
		String sql = " select t.busi_scene_code,t.busi_scene_name,t.scene_desc,"
				+ " t.display_order,t.create_staff,t.create_time,t.last_modi_time,t.status"
				+ " from sys_scene t where t.status=1";
		if (CommonUtil.hasValue(busiSceneCode)) {
			sql += "  and  t.busi_scene_code like '%" + busiSceneCode + "%' ";
		}
		if (CommonUtil.hasValue(busiSceneName)) {
			sql += "  and t.busi_scene_name like '%" + busiSceneName + "%' ";
		}
		sql += " order by t.last_modi_time desc";
		return this.queryByPage(sql, new RowMapper<SysScene>() {

			@Override
			public SysScene mapRow(ResultSet rs, int rowNum) throws SQLException {
				SysScene sysScene = new SysScene();
				sysScene.setBusiSceneCode(rs.getString("busi_scene_code"));
				sysScene.setBusiSceneName(rs.getString("busi_scene_name"));
				sysScene.setSceneDesc(rs.getString("scene_desc"));
				sysScene.setDisplayOrder(rs.getInt("display_order"));
				sysScene.setCreateStaff(rs.getString("create_staff"));
				sysScene.setCreateTime(new Date(rs.getTimestamp("create_time").getTime()));
				if(rs.getTimestamp("last_modi_time") != null){
					sysScene.setLastModiTime(new Date(rs.getTimestamp("last_modi_time").getTime()));
				} else {
					sysScene.setLastModiTime(null);
				}
				sysScene.setStatus(rs.getInt("status"));
				return sysScene;
			}
		}, pageObj);
	}

	@Override
	public List<SysScene> querySysSceneData(String busiSceneCode) {
		String sql = " select t.busi_scene_code,t.busi_scene_name,t.scene_desc,t.display_order,"+
					" t.create_staff,t.create_time,t.last_modi_time,t.status "+
					" from sys_scene t where t.status=1";

		if (busiSceneCode != null) {
			sql += "  and  t.busi_scene_code = '" + busiSceneCode + "' ";
		}
		sql += "order by t.last_modi_time desc";
		return this.getJdbcTemplate().query(sql, new RowMapper<SysScene>() {

			@Override
			public SysScene mapRow(ResultSet rs, int rowNum)
					throws SQLException {
				SysScene sysScene = new SysScene();
				sysScene.setBusiSceneCode(rs.getString("busi_scene_code"));
				sysScene.setBusiSceneName(rs.getString("busi_scene_name"));
				sysScene.setSceneDesc(rs.getString("scene_desc"));
				sysScene.setDisplayOrder(rs.getInt("display_order"));
				sysScene.setCreateStaff(rs.getString("create_staff"));
				sysScene.setCreateTime(new Date(rs.getTimestamp("create_time").getTime()));
				if(rs.getTimestamp("last_modi_time") != null){
					sysScene.setLastModiTime(new Date(rs.getTimestamp("last_modi_time").getTime()));
				} else {
					sysScene.setLastModiTime(null);
				}
				sysScene.setStatus(rs.getInt("status"));
				return sysScene;
			}
		});
	}

	@Override
	public void insertSysScene(SysScene sysScene) {
		String insertSysScene = "insert into sys_scene(busi_scene_code,busi_scene_name,scene_desc,"
				+ "display_order,create_staff,create_time,last_modi_time,status) "
				+ "values(?,?,?,?,?,now(),now(),?)";
		Object [] params = new Object[]{
				sysScene.getBusiSceneCode(),
				sysScene.getBusiSceneName(),
				sysScene.getSceneDesc(),
				sysScene.getDisplayOrder(),
				sysScene.getCreateStaff(),
				sysScene.getStatus()
		};
		this.getJdbcTemplate().update(insertSysScene, params);

	}

	@Override
	public int updateSysScene(SysScene sysScene) {
		String sql = "update  sys_scene  set busi_scene_name = ?,scene_desc=?,"
				+ "display_order=?,last_modi_time=now() "
				+ "where busi_scene_code = ?";
		return this.getJdbcTemplate().update(sql,
				new Object[] { sysScene.getBusiSceneName(),sysScene.getSceneDesc(),
					sysScene.getDisplayOrder(),sysScene.getBusiSceneCode() });
	}

	@Override
	public int deleteSysScene(SysScene sysScene) {
		String sql = "update sys_scene set status=0,last_modi_time=now() where busi_scene_code = ?";
		return this.getJdbcTemplate().update(sql,
				new Object[] { sysScene.getBusiSceneCode() });
	}

	@Override
	public List<BusiDict> queryBusiDictByPage(String busiSceneCode) {
		String sql = " select t.dic_id,t.busi_scene_code,t.dic_code,t.dic_name,t.display_order,"+
				" t.create_staff,t.create_time,t.last_modi_time,t.status from sys_dic t "+
				" where t.status=1";

		if (busiSceneCode != null) {
			sql += " and t.busi_scene_code = '" + busiSceneCode + "' ";
		}
		sql += " order by t.display_order";
		return this.getJdbcTemplate().query(sql, new RowMapper<BusiDict>() {

			@Override
			public BusiDict mapRow(ResultSet rs, int rowNum)
					throws SQLException {
				BusiDict busiDic = new BusiDict();
				busiDic.setDicId(rs.getLong("dic_id"));
				busiDic.setBusiSceneCode(rs.getString("busi_scene_code"));
				busiDic.setDicCode(rs.getString("dic_code"));
				busiDic.setDicName(rs.getString("dic_name"));
				busiDic.setDisplayOrder(rs.getInt("display_order"));
				busiDic.setCreateStaff(rs.getString("create_staff"));
				busiDic.setCreateTime(new Date(rs.getTimestamp("create_time").getTime()));
				if(rs.getTimestamp("last_modi_time") != null){
					busiDic.setLastModiTime(new Date(rs.getTimestamp("last_modi_time").getTime()));
				} else {
					busiDic.setLastModiTime(null);
				}
				busiDic.setStatus(rs.getInt("status"));
				return busiDic;
			}
		});
	}

	@Override
	public List<BusiDict> queryBusiDict(String busiSceneCode, String dicCode) {
		String sql = " select t.dic_id,t.busi_scene_code,t.dic_code,t.dic_name,t.display_order,"+
				" t.create_staff,t.create_time,t.last_modi_time,t.status "+
				" from sys_dic t where t.status=1";

		if (busiSceneCode != null) {
			sql += " and t.busi_scene_code = '" + busiSceneCode + "' ";
		}
		sql += " and t.dic_code = '" + dicCode + "' ";
		sql += " order by t.display_order";
		return this.getJdbcTemplate().query(sql, new RowMapper<BusiDict>() {

			@Override
			public BusiDict mapRow(ResultSet rs, int rowNum) throws SQLException {
				BusiDict busiDic = new BusiDict();
				busiDic.setDicId(rs.getLong("dic_id"));
				busiDic.setBusiSceneCode(rs.getString("busi_scene_code"));
				busiDic.setDicCode(rs.getString("dic_code"));
				busiDic.setDicName(rs.getString("dic_name"));
				busiDic.setDisplayOrder(rs.getInt("display_order"));
				busiDic.setCreateStaff(rs.getString("create_staff"));
				busiDic.setCreateTime(new Date(rs.getTimestamp("create_time").getTime()));
				if(rs.getTimestamp("last_modi_time") != null){
					busiDic.setLastModiTime(new Date(rs.getTimestamp("last_modi_time").getTime()));
				} else {
					busiDic.setLastModiTime(null);
				}
				busiDic.setStatus(rs.getInt("status"));
				return busiDic;
			}
		});
	}

	@Override
	public void insertBusiDict(BusiDict busiDict) {
		String insertBusiDict = "insert into sys_dic(busi_scene_code,dic_code,dic_name,display_order,"
				+ "create_staff,create_time,last_modi_time,status) "
				+ "values(?,?,?,?,?,now(),now(),?)";
		Object[] params = new Object[] { busiDict.getBusiSceneCode(),
				busiDict.getDicCode(), busiDict.getDicName(),
				busiDict.getDisplayOrder(), busiDict.getCreateStaff(),busiDict.getStatus() };
		this.getJdbcTemplate().update(insertBusiDict, params);
	}

	@Override
	public int updateBusiDict(BusiDict busiDict) {
		String sql = "update sys_dic set busi_scene_code = ?,dic_code=?,dic_name=?,"
				+ "display_order=?,last_modi_time=now() where dic_id = ?";
		return this.getJdbcTemplate().update( sql,
				new Object[] { busiDict.getBusiSceneCode(), busiDict.getDicCode(),
					busiDict.getDicName(), busiDict.getDisplayOrder(), busiDict.getDicId() });
	}

	@Override
	public int deleteBusiDict(BusiDict busiDict) {
		String sql = "update sys_dic set status = 0,last_modi_time=now() where dic_id = ?";
		return this.getJdbcTemplate().update(sql,new Object[] { busiDict.getDicId() });
	}

	@Override
	public List<DictDto> getDictionaryByCode(String busiSceneCode) {
		String sql = " select d.dic_code,d.dic_name from sys_dic d "
				+ "where d.busi_scene_code = ? and d.status=1 order by d.display_order ";
		return this.getJdbcTemplate().query(sql, new Object[]{busiSceneCode}, new RowMapper<DictDto>(){

			@Override
			public DictDto mapRow(ResultSet rs, int rowNum) throws SQLException {
				DictDto dict = new DictDto();
				dict.setDicCode(rs.getString("dic_code"));
				dict.setDicName(rs.getString("dic_name"));
				return dict;
			}
			
		});
	}

	@Override
	public List<DictDto> getDictionaryByCodes(String[] busiSceneCodes) {
		if(busiSceneCodes.length <= 0) return null;
		//拼接字符串
		StringBuilder sBuilder = new StringBuilder();
		//存放最终结果
		String s = "";
		for(String code : busiSceneCodes){
			sBuilder.append("'");
			sBuilder.append(code);
			sBuilder.append("',");
		}
		s = sBuilder.substring(0, s.length()-1);
		String sql = " select d.busi_scene_code,d.dic_code,d.dic_name from sys_dic d "+
				" where d.busi_scene_code in ("+s+") and d.status=1 "+
				" order by d.busi_scene_code ,d.display_order ";
		return this.getJdbcTemplate().query(sql, new RowMapper<DictDto>(){
			
			@Override
			public DictDto mapRow(ResultSet rs, int rowNum) throws SQLException {
				DictDto dict = new DictDto();
				dict.setBusiSceneCode(rs.getString("busi_scene_code"));
				dict.setDicCode(rs.getString("dic_code"));
				dict.setDicName(rs.getString("dic_name"));
				return dict;
			}
			
		});
	}

}
