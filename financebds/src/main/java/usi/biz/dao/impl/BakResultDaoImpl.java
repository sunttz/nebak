package usi.biz.dao.impl;

import org.apache.commons.lang.StringUtils;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import usi.biz.dao.BakResultDao;
import usi.biz.entity.BakResult;
import usi.common.annotation.OracleDb;
import usi.sys.dao.JdbcDaoSupport4oracle;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;

@OracleDb
@Repository
public class BakResultDaoImpl extends JdbcDaoSupport4oracle implements BakResultDao{
    @Override
    public BakResult queryByTime(String createDate) {
        BakResult bakResult = null;
        String sql = "select t.pk_no,t.succ_num,t.fail_num,t.create_date from biz_bak_result t where to_char(t.create_date,'yyyymmdd') = '"+createDate+"'";
        List<BakResult> results = this.getJdbcTemplate().query(sql, new RowMapper<BakResult>() {
            @Override
            public BakResult mapRow(ResultSet rs, int rowNum) throws SQLException {
                BakResult item = new BakResult();
                item.setPkNo(rs.getLong(1));
                item.setSuccNum(rs.getLong(2));
                item.setFailNum(rs.getLong(3));
                item.setCreateDate(rs.getDate(4));
                return item;
            }
        });
        if(results.size() > 0){
            bakResult = results.get(0);
        }
        return bakResult;
    }

    @Override
    public List<BakResult> queryBakResult(String startDate, String endDate) {
        String sql = "select t.pk_no,t.succ_num,t.fail_num,t.create_date from biz_bak_result t where 1=1";
        if(StringUtils.isNotEmpty(startDate)){
            sql += " and t.create_date >= to_date('"+startDate+" 00:00:00','yyyy-mm-dd hh24:mi:ss')";
        }
        if(StringUtils.isNotEmpty(endDate)){
            sql += " and t.create_date < to_date('"+endDate+" 23:59:59','yyyy-mm-dd hh24:mi:ss')";
        }
        return this.getJdbcTemplate().query(sql, new RowMapper<BakResult>() {
            @Override
            public BakResult mapRow(ResultSet rs, int rowNum) throws SQLException {
                BakResult item = new BakResult();
                item.setPkNo(rs.getLong(1));
                item.setSuccNum(rs.getLong(2));
                item.setFailNum(rs.getLong(3));
                item.setCreateDate(rs.getDate(4));
                return item;
            }
        });
    }

    @Override
    public int saveBakResult(final BakResult bakResult) {
        String sql = "insert into biz_bak_result (pk_no,succ_num,fail_num,create_date) values (BAK_RESULT_SEQ.nextval,?,?,sysdate)";
        return this.getJdbcTemplate().update(sql, new PreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps) throws SQLException {
                ps.setLong(1,bakResult.getSuccNum());
                ps.setLong(2,bakResult.getFailNum());
            }
        });
    }

    @Override
    public int deleteBakResultByTime() {
        Calendar now = Calendar.getInstance();
        String year=String.valueOf(now.get(Calendar.YEAR));
        String month= String.valueOf(now.get(Calendar.MONTH) + 1);
        String day=String.valueOf(now.get(Calendar.DAY_OF_MONTH));
        month=month.length()<2?'0'+month:month;
        day=day.length()<2?'0'+day:day;
        String createDate=year+month+day;
        String sql = " delete from biz_bak_result t where to_char(t.create_date,'yyyymmdd')= '"+createDate+"'";
        return this.getJdbcTemplate().update(sql);
    }
}
