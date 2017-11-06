package usi.sys.dao;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import usi.sys.dto.PageObj;

/**
 * mysql dao的父类
 * @author chen.kui
 * @date 2014年9月28日15:26:57
 */
@Repository
public class JdbcDaoSupport4mysql{
	
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
		pageObj.setTotal(this.getJdbcTemplate().queryForObject("select count(*) total from ("+sql+")page_", params, Integer.class));
		String pageSql = "select page_.* from("+sql+" )page_ limit "+startIndex+","+pageObj.getRows();
		return this.jdbcTemplate.queryForList(pageSql, params);
	}
	
	public List<Map<String, Object>> queryByPageForList(String sql, PageObj pageObj) {
		int startIndex = (pageObj.getPage()-1) * pageObj.getRows();
		pageObj.setTotal(this.getJdbcTemplate().queryForObject("select count(*) total from ("+sql+")page_", Integer.class));
		String pageSql = "select page_.* from("+sql+" )page_ limit "+startIndex+","+pageObj.getRows();
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
		pageObj.setTotal(this.getJdbcTemplate().queryForObject("select count(*) total from ("+sql+") page_", params, Integer.class));
		String pageSql = "select page_.* from("+sql+" )page_ limit "+startIndex+","+pageObj.getRows();
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
		pageObj.setTotal(this.getJdbcTemplate().queryForObject("select count(*) total from ("+sql+")page_", Integer.class));
		String pageSql = "select page_.* from("+sql+" )page_ limit "+startIndex+","+pageObj.getRows();
		return this.jdbcTemplate.query(pageSql, rowMapper);
	}
	
}
