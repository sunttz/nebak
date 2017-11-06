package usi.sys.script.dao.impl4oracle;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import usi.common.annotation.OracleDb;
import usi.sys.script.dao.SysScriptsDao;
import usi.sys.script.dto.SysScriptsDto;
import usi.sys.dao.JdbcDaoSupport4oracle;
import usi.sys.dto.PageObj;
import usi.sys.util.CommonUtil;

@OracleDb
@Repository
public class SysScriptsDaoImpl  extends JdbcDaoSupport4oracle implements SysScriptsDao{
	
	/**
	 * 分页查询执行脚本
	 * @param scriptCode 脚本编码
	 * @param scriptName 脚本名称
	 * @param scriptContent  脚本内容	
	 * @param module  所属模块
	 * @param scriptType  脚本类型
	 * @param isDeploy  发布状态
	 * @param mockInparam  模拟入参
	 * @param memo  备注
	 * @return
	 */
	@Override
	public List<SysScriptsDto> getScrConfig(String scriptCode, String scriptName, String scriptContent,Integer module,Integer scriptType,Integer isDeploy,String mockInparam,String memo,PageObj pageObj) {
		String sql = "select script_code,script_name,script_content,module,script_type,is_deploy,mock_inparam,memo" +
					 " from sys_scripts " +
					 " where del_flag=0";
		if(CommonUtil.hasValue(scriptCode)) sql+=" and script_code like '%"+scriptCode+"%'";
		if(CommonUtil.hasValue(scriptName)) sql+=" and script_name like '%"+scriptName+"%'";
		if(CommonUtil.hasValue(scriptContent)) sql+=" and script_content like '%"+scriptContent+"%'";
//		if(module!=null){ sql += " and module = "+module ;}
//		if(scriptType!=null){sql += " and script_type = "+scriptType;}
		if(module!= null) sql+=" and module ="+module;
		if(scriptType!= null) sql+=" and script_type ="+scriptType;
		sql+=" order by script_code";
		return this.queryByPage(sql, new RowMapper<SysScriptsDto>(){
			@Override
			public SysScriptsDto mapRow(ResultSet rs, int rowNum)
					throws SQLException {
				SysScriptsDto dto = new SysScriptsDto();
				dto.setScriptCode(rs.getString("script_code"));
				dto.setScriptName(rs.getString("script_name"));
				dto.setScriptContent(rs.getString("script_content"));
				dto.setModule(rs.getInt("module"));
				dto.setScriptType(rs.getInt("script_type"));
				dto.setIsDeploy(rs.getInt("is_deploy"));
				dto.setMockInparam(rs.getString("mock_inparam"));
				dto.setMemo(rs.getString("memo"));
				return dto;
			}}, pageObj);
	}
}