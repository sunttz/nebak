package usi.biz.dao;

import java.util.List;

import usi.biz.entity.AutoLogDto;
import usi.biz.entity.NeServer;
import usi.sys.dto.PageObj;
public interface NeServerDao {
	public List<NeServer> getAllOrg();
	public List<NeServer> getPageAllNE(PageObj pageObj,Long orgId,String deviceType);
	public List<NeServer> getAllNE();
	public List<NeServer> getNeServerById(Long serverId);
	public List<AutoLogDto> getAutoResult(String dateTime);
}
