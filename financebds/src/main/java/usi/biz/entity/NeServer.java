package usi.biz.entity;

/**
 * 网元信息表对象
 *
 * @author
 */
public class NeServer {
    //ID
    private Long serverId;
    //机构ID
    private Long orgId;
    //机构名称
    private String orgName;
    //设备名称
    private String deviceName;
    //设备类型
    private String deviceType;
    //备注
    private String remarks;
    //设备地址
    private String deviceAddr;
    //设备端口
    private Long devicePort;
    //备份路径
    private String bakPath;
    //用户名
    private String userName;
    //密码
    private String passWord;
    //备份类型(0被动取1主动推)
    private String bakType;
    // 保存类型(D按天W按周)
    private String saveType;
    // 保存份数
    private Long saveDay;
    // 用户数据路径
    private String bakUserdata;
    // 系统数据路径
    private String bakSystem;
    // 厂家
    private String firms;
    // 关联ID
    private String neServerModuleId;
    // 模块数
    private int moduleNum;

    public Long getServerId() {
        return serverId;
    }

    public void setServerId(Long serverId) {
        this.serverId = serverId;
    }

    public Long getOrgId() {
        return orgId;
    }

    public void setOrgId(Long orgId) {
        this.orgId = orgId;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getDeviceAddr() {
        return deviceAddr;
    }

    public void setDeviceAddr(String deviceAddr) {
        this.deviceAddr = deviceAddr;
    }

    public String getBakPath() {
        return bakPath;
    }

    public void setBakPath(String bakPath) {
        this.bakPath = bakPath;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    public String getBakType() {
        return bakType;
    }

    public void setBakType(String bakType) {
        this.bakType = bakType;
    }

    public Long getSaveDay() {
        return saveDay;
    }

    public void setSaveDay(Long saveDay) {
        this.saveDay = saveDay;
    }

    public String getBakUserdata() {
        return bakUserdata;
    }

    public void setBakUserdata(String bakUserdata) {
        this.bakUserdata = bakUserdata;
    }

    public String getBakSystem() {
        return bakSystem;
    }

    public void setBakSystem(String bakSystem) {
        this.bakSystem = bakSystem;
    }

    public String getSaveType() {
        return saveType;
    }

    public void setSaveType(String saveType) {
        this.saveType = saveType;
    }

    public String getFirms() {
        return firms;
    }

    public void setFirms(String firms) {
        this.firms = firms;
    }

    public Long getDevicePort() {
        return devicePort;
    }

    public void setDevicePort(Long devicePort) {
        this.devicePort = devicePort;
    }

    public String getNeServerModuleId() {
        return neServerModuleId;
    }

    public void setNeServerModuleId(String neServerModuleId) {
        this.neServerModuleId = neServerModuleId;
    }

    public int getModuleNum() {
        return moduleNum;
    }

    public void setModuleNum(int moduleNum) {
        this.moduleNum = moduleNum;
    }
}
