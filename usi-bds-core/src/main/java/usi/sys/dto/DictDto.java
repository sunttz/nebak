package usi.sys.dto;

import usi.sys.util.JacksonUtil;

public class DictDto {
	private String dicCode;			//字典编码
	private String dicName;			//字典名称
	private String busiSceneCode;	//业务场景编码
	
	@Override
	public String toString(){
		String result = "";
		try {
			result = JacksonUtil.obj2json(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public String getDicCode() {
		return dicCode;
	}
	public void setDicCode(String dicCode) {
		this.dicCode = dicCode;
	}
	public String getDicName() {
		return dicName;
	}
	public void setDicName(String dicName) {
		this.dicName = dicName;
	}
	public String getBusiSceneCode() {
		return busiSceneCode;
	}
	public void setBusiSceneCode(String busiSceneCode) {
		this.busiSceneCode = busiSceneCode;
	}
	
	
}
