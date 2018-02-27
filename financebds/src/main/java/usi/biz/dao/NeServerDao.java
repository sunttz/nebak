package usi.biz.dao;

import usi.biz.entity.AutoLogDto;
import usi.biz.entity.NeServer;
import usi.sys.dto.PageObj;
import usi.sys.entity.BusiDict;

import java.util.List;
public interface NeServerDao {
	public List<NeServer> getAllOrg();
	List<NeServer> getAllOrg2();
	List<BusiDict> getAllFirms();
	public List<NeServer> getPageAllNE(PageObj pageObj,Long orgId,String deviceType,String deviceName,String bakType,String saveType,String saveDay);
	public List<NeServer> getAllNE();
	public List<NeServer> getNeServerById(Long serverId);
	public List<AutoLogDto> getAutoResult(String dateTime,PageObj pageObj);

	/**
	 * 新增网元
	 * @param neServer
	 */
	int saveNeServer(NeServer neServer);

	/**
	 * 修改网元
	 * @param neServer
	 * @return
	 */
	int updateNeServer(NeServer neServer);

	/**
	 * 删除网元
	 * @param serverId
	 */
	int deleteNeServer(Long serverId);

	/**
	 * 获得失败列表
	 * @param dateTime
	 * @param pageObj
	 * @return
	 */
	List<AutoLogDto> getFailResult(String dateTime,PageObj pageObj);

	/**
	 * 根据安徽地市中文返回安徽地市字母缩写
	 * @param str
	 * @return
	 */
	String getPinYinHeadChar(String str);

	/**
	 * 根据安徽地市字母缩写返回安徽地市中文
	 * @param str
	 * @return
	 */
	String getNameByHeadchar(String str);

}
