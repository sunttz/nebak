package usi.biz.dao.impl;

import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import usi.biz.dao.NeServerDao;
import usi.biz.entity.AutoLogDto;
import usi.biz.entity.NeServer;
import usi.biz.entity.NeServerPojo;
import usi.common.annotation.OracleDb;
import usi.sys.dao.JdbcDaoSupport4oracle;
import usi.sys.dto.PageObj;
import usi.sys.entity.BusiDict;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@OracleDb
@Repository
public class NeServerDaoImpl extends JdbcDaoSupport4oracle implements NeServerDao {

    @Override
    public List<NeServer> getAllOrg() {
        String sql = " select  distinct t.org_id,t.org_name  from ne_server t order by t.org_id asc";

        return this.getJdbcTemplate().query(sql, new RowMapper<NeServer>() {
            @Override
            public NeServer mapRow(ResultSet rs, int rowNum) throws SQLException {
                NeServer record = new NeServer();
                record.setOrgId(rs.getLong(1));
                record.setOrgName(rs.getString(2));
                return record;
            }
        });
    }

    @Override
    public List<NeServer> getAllOrg2() {
        String sql = "SELECT ORG_ID,replace(ORG_NAME,'分公司','') ORG_NAME FROM SYS_ORG WHERE PARENT_ORG_ID = 0 AND ORG_ID <> 1 AND STATUS = 1";
        return this.getJdbcTemplate().query(sql, new RowMapper<NeServer>() {
            @Override
            public NeServer mapRow(ResultSet rs, int rowNum) throws SQLException {
                NeServer record = new NeServer();
                record.setOrgId(rs.getLong(1));
                record.setOrgName(rs.getString(2));
                return record;
            }
        });
    }

    @Override
    public List<BusiDict> getAllFirms() {
        String sql = "SELECT DIC_CODE,DIC_NAME FROM SYS_DIC WHERE BUSI_SCENE_CODE = 'FIRMS' AND STATUS = 1";
        return this.getJdbcTemplate().query(sql, new RowMapper<BusiDict>() {
            @Override
            public BusiDict mapRow(ResultSet rs, int i) throws SQLException {
                BusiDict record = new BusiDict();
                record.setDicCode(rs.getString(1));
                record.setDicName(rs.getString(2));
                return record;
            }
        });
    }

    @Override
    public List<BusiDict> getAllDeviceType() {
        String sql = "SELECT DIC_CODE,DIC_NAME FROM SYS_DIC WHERE BUSI_SCENE_CODE = 'DEVICE_TYPE' AND STATUS = 1";
        return this.getJdbcTemplate().query(sql, new RowMapper<BusiDict>() {
            @Override
            public BusiDict mapRow(ResultSet rs, int i) throws SQLException {
                BusiDict record = new BusiDict();
                record.setDicCode(rs.getString(1));
                record.setDicName(rs.getString(2));
                return record;
            }
        });
    }

    @Override
    public List<NeServer> getPageAllNE(PageObj pageObj, Long orgId, String deviceType, String deviceName, String bakType, String saveType, String saveDay, String createDate) {
        String sql = "select t.server_id,t.org_id,t.org_name,t.device_name,t.device_type,t.remarks,t.device_addr,t.bak_path,t.user_name,t.pass_word,t.bak_type,t.save_day,t.bak_userdata,t.bak_system,t.save_type,t.firms,t.device_port,t.neserver_moduleid,case when t.neserver_moduleid is not null then (select count(*) num from ne_server_module where neserver_module_id = t.neserver_moduleid) else 0 end modulenum,to_char(t.create_date, 'yyyy-mm-dd') as create_date from ne_server t"
                + " WHERE 1=1";

        if (orgId != null && !orgId.equals("") && orgId != -1L) {
            sql += " and t.org_id = " + orgId;
        }
        if (deviceType != null && !deviceType.equals("-1")) {
            sql += " and t.device_type = '" + deviceType + "'";
        }
        if (deviceName != null && !"".equals(deviceName)) {
            sql += " and t.device_name like '%" + deviceName + "%'";
        }
        if (bakType != null && !"".equals(bakType)) {
            sql += " and t.bak_type = '" + bakType + "'";
        }
        if (saveType != null && !"".equals(saveType)) {
            sql += " and t.save_type = '" + saveType + "'";
        }
        if (saveDay != null && !"".equals(saveDay)) {
            sql += " and t.save_day = '" + saveDay + "'";
        }
        if(createDate != null && !"".equals(createDate)){
            sql += " and to_char(t.create_date, 'yyyy-mm-dd') ='" + createDate + "'";
        }
        sql += " order by t.device_type,t.firms,t.server_id";
        return this.queryByPage(sql, new RowMapper<NeServer>() {
            @Override
            public NeServer mapRow(ResultSet rs, int rowNum) throws SQLException {
                NeServer record = new NeServer();
                record.setServerId(rs.getLong(1));
                record.setOrgId(rs.getLong(2));
                record.setOrgName(rs.getString(3));
                record.setDeviceName(rs.getString(4));
                record.setDeviceType(rs.getString(5));
                record.setRemarks(rs.getString(6));
                record.setDeviceAddr(rs.getString(7));
                record.setBakPath(rs.getString(8));
                record.setUserName(rs.getString(9));
                record.setPassWord(rs.getString(10));
                record.setBakType(rs.getString(11));
                record.setSaveDay(rs.getLong(12));
                record.setBakUserdata(rs.getString(13));
                record.setBakSystem(rs.getString(14));
                record.setSaveType(rs.getString(15));
                record.setFirms(rs.getString(16));
                record.setDevicePort(rs.getLong(17));
                record.setNeServerModuleId(rs.getString(18));
                record.setModuleNum(rs.getInt(19));
                record.setCreateDate(rs.getString(20));
                return record;
            }
        }, pageObj);
    }

    @Override
    public List<NeServer> getNeServerById(Long serverId) {
        String sql = "select t.server_id,t.org_id,t.org_name,t.device_name,t.device_type,t.remarks,t.device_addr,t.bak_path,t.user_name,t.pass_word,t.bak_type,t.save_day,t.bak_userdata,t.bak_system,t.save_type,t.device_port,t.neserver_moduleid from ne_server t"
                + " WHERE t.server_id=" + serverId;
        return this.getJdbcTemplate().query(sql, new RowMapper<NeServer>() {
            @Override
            public NeServer mapRow(ResultSet rs, int rowNum) throws SQLException {
                NeServer record = new NeServer();
                record.setServerId(rs.getLong(1));
                record.setOrgId(rs.getLong(2));
                record.setOrgName(rs.getString(3));
                record.setDeviceName(rs.getString(4));
                record.setDeviceType(rs.getString(5));
                record.setRemarks(rs.getString(6));
                record.setDeviceAddr(rs.getString(7));
                record.setBakPath(rs.getString(8));
                record.setUserName(rs.getString(9));
                record.setPassWord(rs.getString(10));
                record.setBakType(rs.getString(11));
                record.setSaveDay(rs.getLong(12));
                record.setBakUserdata(rs.getString(13));
                record.setBakSystem(rs.getString(14));
                record.setSaveType(rs.getString(15));
                record.setDevicePort(rs.getLong(16));
                record.setNeServerModuleId(rs.getString(17));
                return record;
            }
        });
    }

    @Override
    public List<NeServer> getAllNE() {
        // TODO Auto-generated method stub
        String sql = "select t.server_id,t.org_id,t.org_name,t.device_name,t.device_type,t.remarks,t.device_addr,t.bak_path,t.user_name,t.pass_word,t.bak_type from ne_server t"
                + " WHERE 1=1";
        sql += " order by t.org_id asc";
        return this.getJdbcTemplate().query(sql, new RowMapper<NeServer>() {
            @Override
            public NeServer mapRow(ResultSet rs, int rowNum) throws SQLException {
                NeServer record = new NeServer();
                record.setServerId(rs.getLong(1));
                record.setOrgId(rs.getLong(2));
                record.setOrgName(rs.getString(3));
                record.setDeviceName(rs.getString(4));
                record.setDeviceType(rs.getString(5));
                record.setRemarks(rs.getString(6));
                record.setDeviceAddr(rs.getString(7));
                record.setBakPath(rs.getString(8));
                record.setUserName(rs.getString(9));
                record.setPassWord(rs.getString(10));
                record.setBakType(rs.getString(11));
                return record;
            }
        });
    }

    @Override
    public List<AutoLogDto> getAutoResult(String dateTime, PageObj pageObj) {
        String sql = "select a.server_id," +
                "a.org_id," +
                "a.org_name," +
                "a.device_name," +
                "a.device_type," +
                "a.remarks," +
                "a.device_addr," +
                "a.bak_path," +
                "a.user_name," +
                "a.pass_word," +
                "b.log_id," +
                "b.bak_flag," +
                "a.bak_type," +
                "a.save_day," +
                "a.save_type," +
                "to_char(b.create_date, 'yyyy-mm-dd') as create_date," +
                "case when a.neserver_moduleid is not null then (select count(*) num from ne_server_module where neserver_module_id = a.neserver_moduleid) else 0 end modulenum" +
                " from ne_server a, biz_auto_log b" +
                " where a.server_id = b.server_id";
        if (!dateTime.equals("")) {
            sql += " and to_char(b.create_date, 'yyyymmdd') ='" + dateTime + "'";
        }
        //sql+=" order by a.org_id";
        return this.queryByPage(sql, new RowMapper<AutoLogDto>() {
            @Override
            public AutoLogDto mapRow(ResultSet rs, int rowNum) throws SQLException {
                AutoLogDto record = new AutoLogDto();
                record.setServerId(rs.getLong(1));
                record.setOrgId(rs.getLong(2));
                record.setOrgName(rs.getString(3));
                record.setDeviceName(rs.getString(4));
                record.setDeviceType(rs.getString(5));
                record.setRemarks(rs.getString(6));
                record.setDeviceAddr(rs.getString(7));
                record.setBakPath(rs.getString(8));
                record.setUserName(rs.getString(9));
                record.setPassWord(rs.getString(10));
                record.setLogId(rs.getLong(11));
                record.setBakFlag(rs.getInt(12));
                record.setBakType(rs.getString(13));
                record.setSaveDay(rs.getLong(14));
                record.setSaveType(rs.getString(15));
                record.setCreateDate(rs.getString(16));
                record.setModuleNum(rs.getInt(17));
                return record;
            }
        }, pageObj);
    }

    @Override
    public int saveNeServer(final NeServer neServer) {
        String sql = "INSERT INTO NE_SERVER " +
                " (SERVER_ID,ORG_NAME,DEVICE_NAME,DEVICE_TYPE,REMARKS,DEVICE_ADDR,BAK_PATH,USER_NAME,PASS_WORD,ORG_ID,BAK_TYPE,SAVE_DAY,BAK_USERDATA,BAK_SYSTEM,SAVE_TYPE,FIRMS,DEVICE_PORT,NESERVER_MODULEID)" +
                " VALUES (NE_SERVER_SEQ.nextval, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?,?,?,?,?,?,?) ";
        return this.getJdbcTemplate().update(sql, new PreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps) throws SQLException {
                ps.setString(1, neServer.getOrgName());
                ps.setString(2, neServer.getDeviceName());
                ps.setString(3, neServer.getDeviceType());
                ps.setString(4, neServer.getRemarks());
                ps.setString(5, neServer.getDeviceAddr());
                ps.setString(6, neServer.getBakPath());
                ps.setString(7, neServer.getUserName());
                ps.setString(8, neServer.getPassWord());
                ps.setLong(9, neServer.getOrgId());
                ps.setString(10, neServer.getBakType());
                ps.setLong(11, neServer.getSaveDay());
                ps.setString(12, neServer.getBakUserdata());
                ps.setString(13, neServer.getBakSystem());
                ps.setString(14, neServer.getSaveType());
                ps.setString(15, neServer.getFirms());
                ps.setLong(16, neServer.getDevicePort());
                ps.setString(17, neServer.getNeServerModuleId());
            }
        });
    }

    @Override
    public int updateNeServer(final NeServer neServer) {
        String sql = "UPDATE NE_SERVER SET ORG_NAME=?,DEVICE_NAME=?,DEVICE_TYPE=?,REMARKS=?,DEVICE_ADDR=?,BAK_PATH=?,USER_NAME=?,PASS_WORD=?,ORG_ID=?,BAK_TYPE=?,SAVE_DAY=?,BAK_USERDATA=?,BAK_SYSTEM=?,SAVE_TYPE=?,FIRMS=?,DEVICE_PORT=?,CREATE_DATE=sysdate WHERE SERVER_ID=?";
        return this.getJdbcTemplate().update(sql, new PreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps) throws SQLException {
                ps.setString(1, neServer.getOrgName());
                ps.setString(2, neServer.getDeviceName());
                ps.setString(3, neServer.getDeviceType());
                ps.setString(4, neServer.getRemarks());
                ps.setString(5, neServer.getDeviceAddr());
                ps.setString(6, neServer.getBakPath());
                ps.setString(7, neServer.getUserName());
                ps.setString(8, neServer.getPassWord());
                ps.setLong(9, neServer.getOrgId());
                ps.setString(10, neServer.getBakType());
                ps.setLong(11, neServer.getSaveDay());
                ps.setString(12, neServer.getBakUserdata());
                ps.setString(13, neServer.getBakSystem());
                ps.setString(14, neServer.getSaveType());
                ps.setString(15, neServer.getFirms());
                ps.setLong(16, neServer.getDevicePort());
                //ps.setString(17, neServer.getNeServerModuleId());
                ps.setLong(17, neServer.getServerId());
            }
        });
    }

    @Override
    public int deleteNeServer(final Long serverId) {
        String sql = "DELETE FROM NE_SERVER WHERE SERVER_ID = ?";
        return this.getJdbcTemplate().update(sql, new PreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps) throws SQLException {
                ps.setLong(1, serverId);
            }
        });
    }

    @Override
    public List<AutoLogDto> getFailResult(String dateTime, PageObj pageObj) {
        String sql = "select a.server_id," +
                "a.org_id," +
                "a.org_name," +
                "a.device_name," +
                "a.device_type," +
                "a.remarks," +
                "a.device_addr," +
                "a.bak_path," +
                "a.user_name," +
                "a.pass_word," +
                "b.log_id," +
                "b.bak_flag," +
                "a.bak_type," +
                "to_char(b.create_date, 'yyyy-mm-dd') as create_date," +
                "case when a.neserver_moduleid is not null then (select count(*) num from ne_server_module where neserver_module_id = a.neserver_moduleid) else 0 end modulenum" +
                " from ne_server a, biz_auto_log b" +
                " where a.server_id = b.server_id and b.bak_flag = 0";
        if (!dateTime.equals("")) {
            sql += " and to_char(b.create_date, 'yyyymmdd') ='" + dateTime + "'";
        }
        //sql+=" order by a.org_id";
        return this.queryByPage(sql, new RowMapper<AutoLogDto>() {
            @Override
            public AutoLogDto mapRow(ResultSet rs, int rowNum) throws SQLException {
                AutoLogDto record = new AutoLogDto();
                record.setServerId(rs.getLong(1));
                record.setOrgId(rs.getLong(2));
                record.setOrgName(rs.getString(3));
                record.setDeviceName(rs.getString(4));
                record.setDeviceType(rs.getString(5));
                record.setRemarks(rs.getString(6));
                record.setDeviceAddr(rs.getString(7));
                record.setBakPath(rs.getString(8));
                record.setUserName(rs.getString(9));
                record.setPassWord(rs.getString(10));
                record.setLogId(rs.getLong(11));
                record.setBakFlag(rs.getInt(12));
                record.setBakType(rs.getString(13));
                record.setCreateDate(rs.getString(14));
                record.setModuleNum(rs.getInt(15));
                return record;
            }
        }, pageObj);
    }

    @Override
    public String getPinYinHeadChar(String str) {
        String sql = "SELECT MEMO FROM SYS_ORG WHERE ORG_NAME LIKE '%" + str + "%'";
        List<String> results = this.getJdbcTemplate().query(sql, new RowMapper<String>() {
            @Override
            public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                return rs.getString(1);
            }
        });
        if (results != null && results.size() > 0) {
            return results.get(0);
        }
        return "";
    }

    @Override
    public String getNameByHeadchar(String str) {
        String sql = "SELECT replace(ORG_NAME,'分公司','') ORG_NAME FROM SYS_ORG WHERE MEMO = '" + str + "'";
        List<String> results = this.getJdbcTemplate().query(sql, new RowMapper<String>() {
            @Override
            public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                return rs.getString(1);
            }
        });
        if (results != null && results.size() > 0) {
            return results.get(0);
        }
        return "";
    }

    @Override
    public List<NeServerPojo> batchSelect(String ids) {
        final StringBuilder query = new StringBuilder();
        query.append("SELECT NS.SERVER_ID,NS.ORG_ID,NS.ORG_NAME,NS.DEVICE_NAME,NS.DEVICE_TYPE,NS.REMARKS,NS.BAK_TYPE,NS.SAVE_DAY,CASE NS.SAVE_TYPE WHEN 'D' THEN '按天' WHEN 'W' THEN '按周' ELSE NS.SAVE_TYPE END SAVE_TYPE,NS.BAK_USERDATA,NS.BAK_SYSTEM,NS.FIRMS,NS.NESERVER_MODULEID,");
        query.append("NSM.MODULE_ID,NSM.MODULE_NAME,NSM.DEVICE_ADDR,NSM.DEVICE_PORT,NSM.USER_NAME,NSM.PASS_WORD,NSM.BAK_PATH,CASE NSM.BAK_PROTOCOL WHEN '0' THEN 'FTP' WHEN '1' THEN 'SFTP' ELSE NSM.BAK_PROTOCOL END BAK_PROTOCOL");
        query.append(" FROM NE_SERVER NS");
        query.append(" LEFT JOIN NE_SERVER_MODULE NSM ON NS.NESERVER_MODULEID = NSM.NESERVER_MODULE_ID");
        query.append(" WHERE NS.SERVER_ID IN (");
        final String[] neServerIds = ids.split(",");
        for (int i = 0; i < neServerIds.length; i++) {
            query.append(" ?");
            if (i != neServerIds.length - 1) {
                query.append(",");
            }
        }
        query.append(")");
        query.append(" ORDER BY NS.SERVER_ID");
        return this.getJdbcTemplate().query(query.toString(), new PreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps) throws SQLException {
                for (int i = 0; i < neServerIds.length; i++) {
                    ps.setString(i + 1, neServerIds[i]);
                }
            }
        }, new RowMapper<NeServerPojo>() {
            @Override
            public NeServerPojo mapRow(ResultSet rs, int rowNum) throws SQLException {
                NeServerPojo record = new NeServerPojo();
                record.setServerId(rs.getLong(1));
                record.setOrgId(rs.getLong(2));
                record.setOrgName(rs.getString(3));
                record.setDeviceName(rs.getString(4));
                record.setDeviceType(rs.getString(5));
                record.setRemarks(rs.getString(6));
                record.setBakType(rs.getString(7));
                record.setSaveDay(rs.getLong(8));
                record.setSaveType(rs.getString(9));
                record.setBakUserdata(rs.getString(10));
                record.setBakSystem(rs.getString(11));
                record.setFirms(rs.getString(12));
                record.setNeServerModuleId(rs.getString(13));
                record.setModuleId(rs.getLong(14));
                record.setModuleName(rs.getString(15));
                record.setDeviceAddr(rs.getString(16));
                record.setDevicePort(rs.getLong(17));
                record.setUserName(rs.getString(18));
                record.setPassWord(rs.getString(19));
                record.setBakPath(rs.getString(20));
                record.setBakProtocol(rs.getString(21));
                return record;
            }
        });
    }

    @Override
    public String getOrgIdByName(String orgName) {
        String sql = "SELECT ORG_ID FROM SYS_ORG WHERE PARENT_ORG_ID = 0 AND ORG_ID <> 1 AND STATUS = 1 AND ORG_NAME LIKE '%" + orgName + "%'";
        List<String> results = this.getJdbcTemplate().query(sql, new RowMapper<String>() {
            @Override
            public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                return rs.getString(1);
            }
        });
        if (results != null && results.size() > 0) {
            return results.get(0);
        }
        return "";
    }
}
