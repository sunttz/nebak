package usi.sys.controller;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import usi.sys.dto.AuthInfo;
import usi.sys.dto.OrgInfo;
import usi.sys.dto.PageObj;
import usi.sys.dto.RoleGrantInfo;
import usi.sys.dto.StaffInfo;
import usi.sys.entity.Org;
import usi.sys.entity.Staff;
import usi.sys.service.BusiDictService;
import usi.sys.service.OrgService;
import usi.sys.service.StaffService;
import usi.sys.service.SysOptLogService;
import usi.sys.util.ConstantUtil;
import usi.sys.util.IpAddressUtil;
import usi.util.PropertyUtil;

/**
 * 
 * @author lmwang
 * 创建时间：2014-3-26 下午2:54:48
 */
@Controller
@RequestMapping("/orgAndStaff")
public class StaffOrgController {
	
	@Resource
	private OrgService orgService;
	@Resource
	private StaffService staffService;
	@Resource
	private BusiDictService busiDictService;
	@Resource
	private SysOptLogService sysOptLogService;
	
	/**
	 * 机构员工页面
	 * @return
	 */
	@RequestMapping(value = "/getStaffAndOrgPage.do", method = RequestMethod.GET)
	public String getStaffAndOrgPage(Model model){
		model.addAttribute("orgTypes", busiDictService.getDictByCode("ORG_TYPE"));
		model.addAttribute("orgGrades", busiDictService.getDictByCode("ORG_GRADE"));
		model.addAttribute("isLeafs", busiDictService.getDictByCode("IS_LEAF"));
		model.addAttribute("genders", busiDictService.getDictByCode("GENDER"));
		return "system/orgAndStaff";
	}
	
	/**
	 * 查询机构树
	 * @param orgId
	 * @return
	 */
	@RequestMapping(value = "/getOrgTree.do", method = RequestMethod.POST)
	@ResponseBody
	public List<OrgInfo> getOrgTree(Long id){
		//加载根节点时，无id传入
		Long orgId = id == null ? ConstantUtil.ORG_ROOT_PARENT : id;
		return orgService.getOrgTree(orgId);
	}
	
	/**
	 * 保存机构信息
	 * @param org
	 * @return
	 */
	@RequestMapping(value = "/saveOrg.do",method = RequestMethod.POST)
	public void saveOrg(HttpServletRequest request,HttpSession session,PrintWriter pw,Org org){
		try {
			AuthInfo auth = (AuthInfo)session.getAttribute(ConstantUtil.AUTH_INFO);
			String userId = auth.getUserId();
			if(org.getOrgId() == null){
				orgService.saveSubOrg(org); //新增子机构
				if(ConstantUtil.WRITELOG){
					try {
						sysOptLogService.insertOrg(IpAddressUtil.getReqIp(request),userId,org);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} else {
				Org beforeUpdate = null;
				if(ConstantUtil.WRITELOG){
					beforeUpdate = sysOptLogService.getOrgByOrgId(org.getOrgId());
				}
				orgService.updateOrg(org);	//更新机构信息
				if(ConstantUtil.WRITELOG){
					try {
						sysOptLogService.updateOrg(IpAddressUtil.getReqIp(request),userId,beforeUpdate,org);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			pw.write("success");
		} catch (Exception e) {
			e.printStackTrace();
			pw.write("fail");
		}
	}
	
	/**
	 * 逻辑删除机构，删除前检查是否存在子机构或下属员工
	 * @param orgId
	 * @return
	 */
	@RequestMapping(value = "/delOrg.do", method = RequestMethod.POST)
	@ResponseBody
	public String delOrg(HttpServletRequest request,HttpSession session,Long orgId){
		try {
			AuthInfo auth = (AuthInfo)session.getAttribute(ConstantUtil.AUTH_INFO);
			String userId = auth.getUserId();
			Org beforeDel = null;
			if(ConstantUtil.WRITELOG){
				beforeDel = sysOptLogService.getOrgByOrgId(orgId);
			}
			int i = orgService.delOrg(orgId);
			if(i > 0){
				return "cascade";
			} else {
				if(ConstantUtil.WRITELOG){
					try {
						sysOptLogService.deleteOrg(IpAddressUtil.getReqIp(request),userId,beforeDel);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				return "success";
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "fail";
		}
	}
	
	/**
	 * 分页查询员工信息
	 * @param pageObj
	 * @param staff
	 * @return
	 */
	@RequestMapping(value = "/queryPageStaffByCondition.do",method = RequestMethod.POST)
	@ResponseBody
	public Map<String,Object> queryPageStaffByCondition(PageObj pageObj,Staff staff){
		return staffService.queryPageStaffByCondition(pageObj, staff);
	}
	
	/**
	 * 根据主键加载员工详细信息
	 * @param staffId
	 * @return
	 */
	@RequestMapping(value = "/getStaffDetailById.do",method = RequestMethod.POST)
	@ResponseBody
	public StaffInfo getStaffDetailById(Long staffId){
		return staffService.queryStaffDetailById(staffId);
	}
	
	/**
	 * 重置密码
	 * @param staff
	 * @return
	 */
	@RequestMapping(value = "/resetPwd.do",method = RequestMethod.POST)
	@ResponseBody
	public String resetPwd(Staff staff){
		try {
			staffService.resetPwd(staff);
			return "success";
		} catch (Exception e) {
			e.printStackTrace();
			return "fail";
		}
	}
	
	/**
	 * 逻辑删除员工
	 * @param staffStr
	 * @return
	 */
	@RequestMapping(value = "/delStaff.do",method = RequestMethod.POST)
	@ResponseBody
	public String delStaff(HttpServletRequest request,HttpSession session,@RequestParam(value = "staffIds[]") Long[] staffIds){
		try {
			AuthInfo auth = (AuthInfo)session.getAttribute(ConstantUtil.AUTH_INFO);
			String userId = auth.getUserId();
			staffService.batchDelStaff(staffIds);
			if(ConstantUtil.WRITELOG){
				try {
					sysOptLogService.delStaff(IpAddressUtil.getReqIp(request),userId,staffIds);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return "success";
		} catch (Exception e) {
			e.printStackTrace();
			return "fail";
		}
	}
	
	/**
	 * 保存员工信息
	 * @param staff
	 * @return
	 */
	@RequestMapping(value = "/saveStaff.do",method = RequestMethod.POST)
	public void saveStaff(HttpServletRequest request,HttpSession session, PrintWriter pw,Staff staff,@RequestParam("photo") MultipartFile photo,String pictureTemp){
		try {
			AuthInfo auth = (AuthInfo)session.getAttribute(ConstantUtil.AUTH_INFO);
			String userId = auth.getUserId();
			if(staff.getStaffId() == null){
				if(staffService.checkUserId(staff.getUserId()) > 0){
					pw.print("exist");
					return;
				}
				staff.setStatus(1);
				staff.setIsOnduty(1);
				/*
				 * 添加员工头像照片
				 */
				/************************************/
				if(photo!=null&&photo.getSize()!=0){
					this.addOrUpdatePicture( staff, photo, pw);
				}
				/************************************/
				staffService.addStaff(staff);	//新增员工信息
				if(ConstantUtil.WRITELOG){
					try {
						sysOptLogService.addStaff(IpAddressUtil.getReqIp(request),userId,staff);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} else {
				Staff beforeUpdate = null;
				if(ConstantUtil.WRITELOG){
					try {
						beforeUpdate = sysOptLogService.getStaffByStaffId(staff.getStaffId());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				/*
				 * 判断图片是否发生变更，如果头像变更，上传头像
				 * author：duanchangcai  date:2016/5/23
				 */
				/************************************/
				if(!pictureTemp.equals(staff.getPicture())){
					this.addOrUpdatePicture( staff, photo, pw);
				}
				/************************************/
				staffService.updateStaff(staff);	//更新员工信息
				if(ConstantUtil.WRITELOG){
					try {
						sysOptLogService.updateStaff(IpAddressUtil.getReqIp(request),userId,beforeUpdate,staff);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			pw.print("success");
		} catch (Exception e) {
			e.printStackTrace();
			pw.print("fail");
		}
	}
	
	/**
	 * 添加或更新用户头像图片
	 * @param staff
	 * @param photo
	 * @param pw
	 */
	public void addOrUpdatePicture(Staff staff, MultipartFile photo,PrintWriter pw){
			String picture = staffService.uploadPhoto(photo);
			if(picture.equals("ERROR PHOTO")){
				pw.print("error");
			}else if(picture.equals("ERROR")){
				pw.print("fail");
			}else{
				staff.setPicture(picture);
			}
	}
	
	/**
	 * 在修改时显示图片
	 * @param fileCode
	 * @param suffix
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/images/{fileCode}.{suffix}",method = RequestMethod.GET)
	public void showImage(@PathVariable String fileCode, @PathVariable String suffix, HttpServletRequest request, HttpServletResponse response) {
		try {
			response.setHeader("Content-Type", "application/octet-stream");
			File file = null;
			// 获取文件
			String path= PropertyUtil.getStringValue("upload.img.path");
			file = new File(path + "/"+fileCode +"."+ suffix);
			FileUtils.copyFile(file, response.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 分页查询角色
	 * @param pageObj
	 * @param roleGrantInfo
	 * @return
	 */
	@RequestMapping(value = "/queryPageRole.do",method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> queryPageRole(PageObj pageObj, RoleGrantInfo roleGrantInfo){
		return staffService.queryPageRole(pageObj, roleGrantInfo);
	}
	
	/**
	 * 保存对员工的角色授予或取消授予
	 * @param opflag
	 * @param staffId
	 * @param roleIdStr
	 * @return
	 */
	@RequestMapping(value = "/saveStaffRole.do",method = RequestMethod.POST)
	@ResponseBody
	public String saveStaffRole(HttpServletRequest request,HttpSession session,Integer opflag, Long staffId, @RequestParam(value="roleIds[]") Integer[] roleIds){
		try {
			AuthInfo auth = (AuthInfo)session.getAttribute(ConstantUtil.AUTH_INFO);
			String userId = auth.getUserId();
			if(opflag == 0){ //授予角色
				staffService.batchGrantRole(staffId, roleIds);
				if(ConstantUtil.WRITELOG){
					try {
						sysOptLogService.staffGrantRole(IpAddressUtil.getReqIp(request),userId,roleIds);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} else { //取消授予
				staffService.batchRevokeRole(staffId, roleIds);
				if(ConstantUtil.WRITELOG){
					try {
						sysOptLogService.staffRevokeRole(IpAddressUtil.getReqIp(request),userId,roleIds);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			return "success";
		} catch (Exception e) {
			e.printStackTrace();
			return "fail";
		}
	}
	
}
