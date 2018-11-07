package usi.biz.entity;

public class NeServerModule {
    //ID
    private Long moduleId;
    //关联ID
    private String neServerModuleId;
    //模块名
    private String moduleName;
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
    //备份协议(0=ftp、1=sftp)
    private String bakProtocol;

    public Long getModuleId() {
        return moduleId;
    }

    public void setModuleId(Long moduleId) {
        this.moduleId = moduleId;
    }

    public String getNeServerModuleId() {
        return neServerModuleId;
    }

    public void setNeServerModuleId(String neServerModuleId) {
        this.neServerModuleId = neServerModuleId;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getDeviceAddr() {
        return deviceAddr;
    }

    public void setDeviceAddr(String deviceAddr) {
        this.deviceAddr = deviceAddr;
    }

    public Long getDevicePort() {
        return devicePort;
    }

    public void setDevicePort(Long devicePort) {
        this.devicePort = devicePort;
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

    public String getBakProtocol() {
        return bakProtocol;
    }

    public void setBakProtocol(String bakProtocol) {
        this.bakProtocol = bakProtocol;
    }
}
