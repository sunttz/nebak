package usi.sys.dao.impl4oracle;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import usi.common.annotation.OracleDb;
import usi.sys.dao.JdbcDaoSupport4oracle;
import usi.sys.dao.RetrievalCodeDao;
import usi.sys.dto.RetrievalCode;

/**
 * 
 * @author lmwang
 * 创建时间：2014-4-16 上午11:05:57
 */
@OracleDb
@Repository
public class RetrievalCodeDaoImpl extends JdbcDaoSupport4oracle implements RetrievalCodeDao{

	public List<RetrievalCode> getNameIndexCode(String q){
		if(null==q||"".equals(q.trim())) {
			List<RetrievalCode> nullLst = new ArrayList<RetrievalCode>();
			return nullLst;
		}
		String sql = "select c.index_code,b.operator_name,a.org_name from sys_org a,sys_staff b,sys_staff_attr c\n" +
						" where a.org_id=b.org_id and b.staff_id=c.staff_id and b.status=1 and c.index_code like ?||'%'";
		return this.getJdbcTemplate().query(sql,new Object[]{q}, new RowMapper<RetrievalCode>(){
			public RetrievalCode mapRow(ResultSet rs, int rowNum) throws SQLException {
				RetrievalCode retrievalCode = new RetrievalCode();
				retrievalCode.setIndexCode(rs.getString("index_code"));
				retrievalCode.setOperatorName(rs.getString("operator_name"));
				retrievalCode.setOrgName(rs.getString("org_name"));
				return retrievalCode;
			}
		});

	}
}
