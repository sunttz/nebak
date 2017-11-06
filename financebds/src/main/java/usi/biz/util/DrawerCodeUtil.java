package usi.biz.util;

import java.util.HashMap;
import java.util.Map;


/**
 * 销货单位代码常量静态类.
* @ClassName: XhdwCodeUtil
* @author 胡军
* @date 2016年6月25日 下午3:29:00
*
 */
public class DrawerCodeUtil {
	
	public static void main(String[] args) {
		System.out.println(DrawerCodeUtil.getDrawerCode("A5201"));
	}
	
	/**
	 * 收入合同管理系统开票人编码与增值税管理系统销货单位代码对应关联map.
	 */
	private static final Map<String,String> xhdwCodeMap = new HashMap<String,String>();
	
	/**
	 * 根据收入合同管理系统销货单位代码获取开票员编码.
	 * @param xhdwCode 销货单位代码
	 * @return String  开票员编码
	 */
	public static String getDrawerCode(String xhdwCode){
		return xhdwCodeMap.get(xhdwCode) == null ? "" : xhdwCodeMap.get(xhdwCode);
	}
	
	/**
	 * 开票员编码静态数据添加.
	 */
	static{
		xhdwCodeMap.put("A52"  ,"HTGL00");
		xhdwCodeMap.put("A5201","HTGL01");
		xhdwCodeMap.put("A5202","HTGL02");
		xhdwCodeMap.put("A5203","HTGL03");
		xhdwCodeMap.put("A5204","HTGL04");
		xhdwCodeMap.put("A5205","HTGL05");
		xhdwCodeMap.put("A5206","HTGL06");
		xhdwCodeMap.put("A5207","HTGL07");
		xhdwCodeMap.put("A5208","HTGL08");
		xhdwCodeMap.put("A5209","HTGL09");
		xhdwCodeMap.put("A5282","HTGL10");
		xhdwCodeMap.put("A5283","HTGL11");
		xhdwCodeMap.put("A5284","HTGL12");
		xhdwCodeMap.put("A5291","HTGL13");
		
	}
}
