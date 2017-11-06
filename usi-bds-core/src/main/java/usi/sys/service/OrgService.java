package usi.sys.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import usi.sys.dao.OrgDao;
import usi.sys.dto.OrgDto;
import usi.sys.dto.OrgInfo;
import usi.sys.entity.Org;
import usi.sys.util.ConstantUtil;

@Service
public class OrgService {

	@Resource
	private OrgDao orgDao;
	
	/**
	 * 加载机构树
	 * @param orgId
	 * @return
	 */
	public List<OrgInfo> getOrgTree(Long orgId){
		List<OrgInfo> orgList = orgDao.getOrgTree(orgId);
		return orgList;
	}
	
	/**
	 * 加载机构树(同步)
	 * @return
	 */
	public List<OrgDto> queryAllOrg(){
		List<OrgDto> nodes = new ArrayList<OrgDto>();
		List<OrgDto> orgs = orgDao.queryAllOrg();
		OrgDto orgDtoRoot = new OrgDto(); //根节点
		for(OrgDto org : orgs){
			for(OrgDto org1 : orgs){
				if(org1.getParentOrgId().equals(org.getId())){
					org.getChildren().add(org1);
				}
			}
			if(org.getParentOrgId() == ConstantUtil.ORG_ROOT_PARENT){
				orgDtoRoot = org;
			}
		}
		nodes.add(orgDtoRoot);
		return nodes;
	}
	
	/**
	 * 新增子机构
	 * @param org
	 */
	@Transactional(rollbackFor=Exception.class)
	public void saveSubOrg(Org org){
		long orgId = orgDao.saveSubOrg(org);
		String seq = org.getOrgSeq() + orgId + ".";
//		org.setOrgSeq(seq);
		orgDao.saveOrgSeq(seq, orgId);
	}
	
	/**
	 * 更新机构信息
	 * @param org
	 */
	@Transactional(rollbackFor=Exception.class)
	public void updateOrg(Org org){
		orgDao.updateOrg(org);
	}
	
	/**
	 * 逻辑删除机构，删除前检查是否存在子机构或下属员工
	 * @param orgId
	 * @return
	 */
	@Transactional(rollbackFor=Exception.class)
	public int delOrg(Long orgId){
		int a = orgDao.checkHasSubOrg(orgId);
		int b = orgDao.checkHasStaff(orgId);
		if(a + b > 0){
			return a + b;
		} else {
			orgDao.delOrg(orgId);
			return 0;
		}
	}
	
	/**
	 * 取得所有的机构二级节点
	 * @return
	 */
	public List<Org> getSecLevelOrg(){
		return orgDao.getSecLevelOrg();
	}
	
	/**
	 * 查询根节点信息
	 * @return
	 */
	public Org getOrgRoot(){
		return orgDao.getOrgRoot();
	}
	
}
