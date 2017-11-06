package usi.sys.dao.impl4mysql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import usi.common.annotation.MysqlDb;
import usi.sys.dao.JdbcDaoSupport4mysql;
import usi.sys.dao.RetrievalCodeDao;
import usi.sys.dto.RetrievalCode;
/**
 * @author chen.kui
 * @date 2014年9月28日15:28:43
 */
@MysqlDb
@Repository
public class RetrievalCodeDaoImpl extends JdbcDaoSupport4mysql implements
		RetrievalCodeDao {

	@Override
	public List<RetrievalCode> getNameIndexCode(String q) {
		if (null == q || "".equals(q.trim())) {
			List<RetrievalCode> nullLst = new ArrayList<RetrievalCode>();
			return nullLst;
		}
		String sql = "select ssa.index_code,ss.operator_name,so.org_name "
				+ "from sys_org so,"
				+ "sys_staff ss,"
				+ "sys_staff_attr ssa "
				+ " where so.org_id=ss.org_id and ss.staff_id=ssa.staff_id "
				+ "and ss.status=1 and ssa.index_code like ?||'%'";
		return this.getJdbcTemplate().query(sql, new Object[] { q },
				new RowMapper<RetrievalCode>() {
					public RetrievalCode mapRow(ResultSet rs, int rowNum)
							throws SQLException {
						RetrievalCode retrievalCode = new RetrievalCode();
						retrievalCode.setIndexCode(rs.getString("index_code"));
						retrievalCode.setOperatorName(rs
								.getString("operator_name"));
						retrievalCode.setOrgName(rs.getString("org_name"));
						return retrievalCode;
					}
				});
	}

}
