package usi.sys.script.dao.impl4oracle;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import usi.common.annotation.OracleDb;
import usi.sys.dao.JdbcDaoSupport4oracle;
import usi.sys.script.dao.ScriptConfDao;
import usi.sys.script.dto.ScriptDto;

/**
 * @Description 
 * @author zhang.dechang
 * @date 2015年8月20日 上午10:21:24
 */
@OracleDb
@Repository
public class ScriptConfDaoImpl extends JdbcDaoSupport4oracle implements ScriptConfDao {

	@Override
	public void insertScriptFromExternal(ScriptDto scriptDto) {
		String sql = "insert into sys_scripts(script_code,script_name,script_content,module,script_type,is_deploy) "+
					"values(?,?,?,?,?,1)";
		Object[] params = new Object[]{
				scriptDto.getScriptCode(),scriptDto.getScriptName(),
				scriptDto.getScriptContent(),scriptDto.getScriptModule(),
				scriptDto.getScriptType()
		};
		this.getJdbcTemplate().update(sql, params);
	}

	@Override
	public void updateScriptFromExternal(ScriptDto scriptDto) {
		String sql = "update sys_scripts t set t.script_content=?, t.script_type=? where t.script_code=?";
		Object[] params = new Object[]{
				scriptDto.getScriptContent(),
				scriptDto.getScriptType(),
				scriptDto.getScriptCode()
		};
		this.getJdbcTemplate().update(sql, params);
	}

	@Override
	public void removeScriptFromExternal(final String scriptCode) {
		String sql = "update sys_scripts t set t.del_flag=1 where t.script_code=?";
		this.getJdbcTemplate().update(sql, new PreparedStatementSetter(){

			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setString(1, scriptCode);
			}});
	}

	@Override
	public List<ScriptDto> getScriptDtoByscriptCode(final String scriptCode) {
		String sql = "select t.script_name,t.script_content,t.script_type from sys_scripts t where t.script_code=? and t.del_flag=0";
		return this.getJdbcTemplate().query(sql, new Object[]{scriptCode}, new RowMapper<ScriptDto>(){

			@Override
			public ScriptDto mapRow(ResultSet rs, int rowNum) throws SQLException {
				ScriptDto item = new ScriptDto();
				item.setScriptCode(scriptCode);
				item.setScriptName(rs.getString("script_name"));
				item.setScriptContent(rs.getString("script_content"));
				item.setScriptType(rs.getInt("script_type"));
				return item;
			}
			
		});
	}

	@Override
	public List<ScriptDto> getAllScript() {
		String sql = "select t.script_name,t.script_code,t.script_content,t.script_type from sys_scripts t where t.del_flag=0";
		return this.getJdbcTemplate().query(sql, new RowMapper<ScriptDto>(){

			@Override
			public ScriptDto mapRow(ResultSet rs, int rowNum) throws SQLException {
				ScriptDto item = new ScriptDto();
				item.setScriptCode(rs.getString("script_code"));
				item.setScriptName(rs.getString("script_name"));
				item.setScriptContent(rs.getString("script_content"));
				item.setScriptType(rs.getInt("script_type"));
				return item;
			}
		});
	}

}
