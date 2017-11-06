package usi.sys.script.dto;

/**
 * @Description 执行脚本
 * @author hu.yuchen	
 * @date 2015年8月21日11:29:20
 */
public class SysScriptsDto {
	//主键 执行脚本编码
	private String scriptCode;
	//脚本名称
	private String scriptName;
	//脚本内容
	private String scriptContent;
	//所属模块
	private Integer module;
	//脚本类型
	private Integer scriptType;
	//发布状态
	private Integer isDeploy;
	//模拟入参
	private String mockInparam;
	//备注
	private String memo;
	
	public String getScriptCode() {
		return scriptCode;
	}
	public void setScriptCode(String scriptCode) {
		this.scriptCode = scriptCode;
	}
	public String getScriptName() {
		return scriptName;
	}
	public void setScriptName(String scriptName) {
		this.scriptName = scriptName;
	}
	public String getScriptContent() {
		return scriptContent;
	}
	public void setScriptContent(String scriptContent) {
		this.scriptContent = scriptContent;
	}
	public Integer getModule() {
		return module;
	}
	public void setModule(Integer module) {
		this.module = module;
	}
	public Integer getScriptType() {
		return scriptType;
	}
	public void setScriptType(Integer scriptType) {
		this.scriptType = scriptType;
	}
	public Integer getIsDeploy() {
		return isDeploy;
	}
	public void setIsDeploy(Integer isDeploy) {
		this.isDeploy = isDeploy;
	}
	public String getMockInparam() {
		return mockInparam;
	}
	public void setMockInparam(String mockInparam) {
		this.mockInparam = mockInparam;
	}
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}
}
