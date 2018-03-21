package usi.biz.dao.impl;

import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import usi.biz.dao.NeServerModuleDao;
import usi.biz.entity.NeServerModule;
import usi.common.annotation.OracleDb;
import usi.sys.dao.JdbcDaoSupport4oracle;
import usi.sys.dto.PageObj;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
@OracleDb
public class NeServerModuleDaoImpl extends JdbcDaoSupport4oracle implements NeServerModuleDao {

    @Override
    public List<NeServerModule> getPageAllModule(PageObj pageObj, String neServerModuleId) {
        String sql = "SELECT MODULE_ID,MODULE_NAME,DEVICE_ADDR,DEVICE_PORT,USER_NAME,PASS_WORD,BAK_PATH,NESERVER_MODULE_ID FROM NE_SERVER_MODULE WHERE NESERVER_MODULE_ID = '"+neServerModuleId+"' ORDER BY MODULE_ID";
        return this.queryByPage(sql, new RowMapper<NeServerModule>() {
            @Override
            public NeServerModule mapRow(ResultSet rs, int i) throws SQLException {
                NeServerModule nsm = new NeServerModule();
                nsm.setModuleId(rs.getLong(1));
                nsm.setModuleName(rs.getString(2));
                nsm.setDeviceAddr(rs.getString(3));
                nsm.setDevicePort(rs.getLong(4));
                nsm.setUserName(rs.getString(5));
                nsm.setPassWord(rs.getString(6));
                nsm.setBakPath(rs.getString(7));
                nsm.setNeServerModuleId(rs.getString(8));
                return nsm;
            }
        },pageObj);
    }

    @Override
    public List<NeServerModule> getAllModule(String neServerModuleId) {
        String sql = "SELECT MODULE_ID,MODULE_NAME,DEVICE_ADDR,DEVICE_PORT,USER_NAME,PASS_WORD,BAK_PATH,NESERVER_MODULE_ID FROM NE_SERVER_MODULE WHERE NESERVER_MODULE_ID = '"+neServerModuleId+"' ORDER BY MODULE_ID";
        return this.getJdbcTemplate().query(sql, new RowMapper<NeServerModule>() {
            @Override
            public NeServerModule mapRow(ResultSet rs, int i) throws SQLException {
                NeServerModule nsm = new NeServerModule();
                nsm.setModuleId(rs.getLong(1));
                nsm.setModuleName(rs.getString(2));
                nsm.setDeviceAddr(rs.getString(3));
                nsm.setDevicePort(rs.getLong(4));
                nsm.setUserName(rs.getString(5));
                nsm.setPassWord(rs.getString(6));
                nsm.setBakPath(rs.getString(7));
                nsm.setNeServerModuleId(rs.getString(8));
                return nsm;
            }
        });
    }

    @Override
    public int saveNeServerModule(final NeServerModule neServerModule) {
        String sql = "INSERT INTO NE_SERVER_MODULE (MODULE_ID,MODULE_NAME,DEVICE_ADDR,DEVICE_PORT,USER_NAME,PASS_WORD,BAK_PATH,NESERVER_MODULE_ID) VALUES (NE_SERVER_MODULE_SEQ.nextval,?,?,?,?,?,?,?)";
        return this.getJdbcTemplate().update(sql, new PreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps) throws SQLException {
                ps.setString(1,neServerModule.getModuleName());
                ps.setString(2,neServerModule.getDeviceAddr());
                ps.setLong(3,neServerModule.getDevicePort());
                ps.setString(4,neServerModule.getUserName());
                ps.setString(5,neServerModule.getPassWord());
                ps.setString(6,neServerModule.getBakPath());
                ps.setString(7,neServerModule.getNeServerModuleId());
            }
        });
    }

    @Override
    public int updateNeServerModule(final NeServerModule neServerModule) {
        String sql = "UPDATE NE_SERVER_MODULE SET MODULE_NAME=?,DEVICE_ADDR=?,DEVICE_PORT=?,USER_NAME=?,PASS_WORD=?,BAK_PATH=? WHERE MODULE_ID = ?";
        return this.getJdbcTemplate().update(sql, new PreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps) throws SQLException {
                ps.setString(1,neServerModule.getModuleName());
                ps.setString(2,neServerModule.getDeviceAddr());
                ps.setLong(3,neServerModule.getDevicePort());
                ps.setString(4,neServerModule.getUserName());
                ps.setString(5,neServerModule.getPassWord());
                ps.setString(6,neServerModule.getBakPath());
                ps.setLong(7,neServerModule.getModuleId());
            }
        });
    }

    @Override
    public int deleteNeServerModule(final Long moduleId) {
        String sql = "DELETE FROM NE_SERVER_MODULE WHERE MODULE_ID = ?";
        return this.getJdbcTemplate().update(sql, new PreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps) throws SQLException {
                ps.setLong(1, moduleId);
            }
        });
    }

    @Override
    public String getModuleNum(String neServerModuleId) {
        String sql = "SELECT count(*) NUM FROM NE_SERVER_MODULE WHERE NESERVER_MODULE_ID = '"+neServerModuleId+"'";
        List<String> results = this.getJdbcTemplate().query(sql, new RowMapper<String>() {
            @Override
            public String mapRow(ResultSet rs, int i) throws SQLException {
                return rs.getString(1);
            }
        });
        if (results != null && results.size() > 0) {
            return results.get(0);
        }
        return "";
    }
}
