package usi.sys.key.util.impl4oracle;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import usi.common.annotation.OracleDb;
import usi.sys.dao.JdbcDaoSupport4oracle;
import usi.sys.key.util.CacheValue;
import usi.sys.key.util.Primarykey;
import usi.sys.key.util.UniqueException;
import usi.sys.key.util.UniqueTableApp;

@OracleDb
@Repository
public class DefaultUniqueTableApp extends JdbcDaoSupport4oracle implements UniqueTableApp{
	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultUniqueTableApp.class);
	protected String selectSQL = "select code from sys_pkey where name = ? for update";
	protected String updateSQL = "update sys_pkey set code = code + ? where name = ?";
	protected String insertSQL = "insert into sys_pkey (code, name) values (?, ?)";

	private final int initCode = 0;

	@Transactional(propagation=Propagation.REQUIRES_NEW)
	public CacheValue getCacheValue(int cacheNum, String name) {
		CacheValue cache = null;
		try {
			cache = getCurrCode(name);
			
			if(cache == null) {
				insert(name);
		        cache = getCurrCode(name);
			}
			
			update(cacheNum, name);
		    cache.setMaxVal(cache.getMinVal() + cacheNum);
		} catch(Exception e) {
			e.printStackTrace();
			LOGGER.error("获取主键失败", e);
		}
		return cache;
	}
	
	private CacheValue getCurrCode(String name) {
		CacheValue value = null;
		try {
			Long code = this.getJdbcTemplate().queryForObject(this.selectSQL, new Object[] {name},Long.class);
			value = new CacheValue();
			value.setMinVal(code + 1);
		} catch (EmptyResultDataAccessException e) {
			LOGGER.info(name + " 没有找到记录");
		} catch (Exception e) {
			e.printStackTrace();
			throw new UniqueException(name + " 获取主键失败");
		}
		return value;
	}
	
	class PrimarykeyMapper implements RowMapper<Primarykey> {
		@Override
		public Primarykey mapRow(ResultSet rs, int rowNum) throws SQLException {
			Primarykey pkey = new Primarykey();
			pkey.setCode(rs.getLong(1));
			return pkey;
		}
	}
	
	private void insert(String name) {
		this.getJdbcTemplate().update(this.insertSQL,  new Object[] {initCode, name});
	}
	
	private void update(int cacheNum, String name) {
		this.getJdbcTemplate().update(this.updateSQL,  new Object[] {cacheNum, name});
	}
	
	/**
	 * @param selectSQL the selectSQL to set
	 */
	public void setSelectSQL(String selectSQL) {
		this.selectSQL = selectSQL;
	}

	/**
	 * @param updateSQL the updateSQL to set
	 */
	public void setUpdateSQL(String updateSQL) {
		this.updateSQL = updateSQL;
	}

	/**
	 * @param insertSQL the insertSQL to set
	 */
	public void setInsertSQL(String insertSQL) {
		this.insertSQL = insertSQL;
	}

}
