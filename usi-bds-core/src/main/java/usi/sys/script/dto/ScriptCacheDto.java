package usi.sys.script.dto;

/**
 * 放在脚本缓存中的数据结构
 * scriptContent：当是xquery脚本时是String类型，当是groovy时是class类型
 * @author zhang.dechang
 */
public class ScriptCacheDto implements java.io.Serializable {
	private static final long serialVersionUID = -7443814006639196193L;
	
	private Integer scriptType;
	private Object scriptContent;
	
	public Integer getScriptType() {
		return scriptType;
	}
	public void setScriptType(Integer scriptType) {
		this.scriptType = scriptType;
	}
	public Object getScriptContent() {
		return scriptContent;
	}
	public void setScriptContent(Object scriptContent) {
		this.scriptContent = scriptContent;
	}
	
}
