package usi.biz.dao;

import usi.biz.entity.AutoLogDto;
import usi.biz.entity.NeServer;
import usi.sys.dto.PageObj;

import java.util.List;
public interface NeServerDao {
	public List<NeServer> getAllOrg();
	public List<NeServer> getPageAllNE(PageObj pageObj,Long orgId,String deviceType);
	public List<NeServer> getAllNE();
	public List<NeServer> getNeServerById(Long serverId);
	public List<AutoLogDto> getAutoResult(String dateTime);

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

}
