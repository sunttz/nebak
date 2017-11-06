package usi.sys.script.dto;
/**
 * @Description 
 * @author zhang.dechang
 * @date 2015年8月20日 上午10:14:29
 */
public class ScriptDto {
	private String scriptCode;
	private String scriptName;
	private String scriptContent;
	private Integer scriptModule;
	private Integer scriptType;
	private Integer scriptDeploy;
	private String scriptMemo;
	private String scriptMockInparam;
	
	public String getScriptMockInparam() {
		return scriptMockInparam;
	}
	public void setScriptMockInparam(String scriptMockInparam) {
		this.scriptMockInparam = scriptMockInparam;
	}
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
	public Integer getScriptModule() {
		return scriptModule;
	}
	public void setScriptModule(Integer scriptModule) {
		this.scriptModule = scriptModule;
	}
	public Integer getScriptType() {
		return scriptType;
	}
	public void setScriptType(Integer scriptType) {
		this.scriptType = scriptType;
	}
	public Integer getScriptDeploy() {
		return scriptDeploy;
	}
	public void setScriptDeploy(Integer scriptDeploy) {
		this.scriptDeploy = scriptDeploy;
	}
	public String getScriptMemo() {
		return scriptMemo;
	}
	public void setScriptMemo(String scriptMemo) {
		this.scriptMemo = scriptMemo;
	}
	
}
