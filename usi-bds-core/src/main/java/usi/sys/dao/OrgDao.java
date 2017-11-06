package usi.sys.dao;

import java.util.List;

import usi.sys.dto.OrgDto;
import usi.sys.dto.OrgInfo;
import usi.sys.entity.Org;

public interface OrgDao {
	
	/**
	 * 逐级加载机构树
	 * @param orgId
	 * @return
	 */
	public List<OrgInfo> getOrgTree(Long orgId);
	
	
	/**
	 * 获取机构树数据(用于同步树)
	 * @return
	 */
	public List<OrgDto> queryAllOrg();
	
	/**
	 * 保存子机构
	 * @param org
	 */
	public long saveSubOrg(final Org org);
	
	/**
	 * 保存机构序列
	 * @param seq
	 * @param orgId
	 */
	public void saveOrgSeq(final String seq,final long orgId);
	
	/**
	 * 更新机构信息
	 * @param org
	 */
	public void updateOrg(final Org org);
	
	/**
	 * 根据主键逻辑删除机构
	 * @param orgId
	 */
	public void delOrg(final Long orgId);
	
	/**
	 * 检查机构是否存在子机构
	 * @param orgId
	 * @return
	 */
	public int checkHasSubOrg(final Long orgId);
	
	/**
	 * 检查机构下是否存在员工
	 * @param orgId
	 * @return
	 */
	public int checkHasStaff(final Long orgId);
	
	/**
	 * 取得所有的机构二级节点
	 * @return
	 */
	public List<Org> getSecLevelOrg();
	
	/**
	 * 查询机构根节点信息
	 * @return
	 */
	public Org getOrgRoot();

}
