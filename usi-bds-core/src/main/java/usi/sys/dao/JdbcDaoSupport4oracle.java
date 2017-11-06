package usi.sys.dao;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import usi.sys.dto.PageObj;

/**
 * @author fan.fan
 * @date 2014-3-27 下午1:48:48
 */
public class JdbcDaoSupport4oracle{
	
	@Resource
	private JdbcTemplate jdbcTemplate;

	/**
	 * 为当前的DAO返回 JdbcTemplate
	 */
	public final JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}
	
	public List<Map<String, Object>> queryByPageForList(String sql, PageObj pageObj, Object... params) {
		int startIndex = (pageObj.getPage()-1) * pageObj.getRows();
		int endIndex = startIndex + pageObj.getRows();
		pageObj.setTotal(this.getJdbcTemplate().queryForObject("select count(*) from ("+sql+")", params, Integer.class));
		String pageSql = "select *"+
				  " from (select page_.*, rownum rn"+
				          " from ("+sql+") page_"+
				         " where rownum <= "+endIndex+")"+
				 " where rn > "+startIndex;
		return this.jdbcTemplate.queryForList(pageSql, params);
	}
	
	public List<Map<String, Object>> queryByPageForList(String sql, PageObj pageObj) {
		int startIndex = (pageObj.getPage()-1) * pageObj.getRows();
		int endIndex = startIndex + pageObj.getRows();
		pageObj.setTotal(this.getJdbcTemplate().queryForObject("select count(*) from ("+sql+")", Integer.class));
		String pageSql = "select *"+
				  " from (select page_.*, rownum rn"+
				          " from ("+sql+") page_"+
				         " where rownum <= "+endIndex+")"+
				 " where rn > "+startIndex;
		return this.jdbcTemplate.queryForList(pageSql);
	}
	
	/**
	 * 通用的分页查询
	 * @param sql
	 * @param rowMapper
	 * @param pageObj
	 * @return
	 */
	public <T> List<T> queryByPage(String sql, Object[] params, RowMapper<T> rowMapper, PageObj pageObj) {
		int startIndex = (pageObj.getPage()-1) * pageObj.getRows();
		int endIndex = startIndex + pageObj.getRows();
		pageObj.setTotal(this.getJdbcTemplate().queryForObject("select count(*) from ("+sql+")", params, Integer.class));
		String pageSql = "select *"+
				  " from (select page_.*, rownum rn"+
				          " from ("+sql+") page_"+
				         " where rownum <= "+endIndex+")"+
				 " where rn > "+startIndex;
		return this.jdbcTemplate.query(pageSql, params, rowMapper);
	}
	
	/**
	 * 通用的分页查询
	 * @param sql
	 * @param params
	 * @param rowMapper
	 * @param pageObj
	 * @return
	 */
	public <T> List<T> queryByPage(String sql, RowMapper<T> rowMapper, PageObj pageObj) {
		int startIndex = (pageObj.getPage()-1) * pageObj.getRows();
		int endIndex = startIndex + pageObj.getRows();
		pageObj.setTotal(this.getJdbcTemplate().queryForObject("select count(*) from ("+sql+")", Integer.class));
		String pageSql = "select *"+
				  " from (select page_.*, rownum rn"+
				          " from ("+sql+") page_"+
				         " where rownum <= "+endIndex+")"+
				 " where rn > "+startIndex;
		return this.jdbcTemplate.query(pageSql, rowMapper);
	}
}
