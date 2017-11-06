<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
	<head>
		<%-- 如果是360浏览器使用webkit内核，其他浏览器无视此标签 --%>
		<meta name="renderer" content="webkit">		
		<title>机构和人员配置</title>
		<%@ include file="/WEB-INF/views/common/taglibs.jsp"%>
		<script type="text/javascript" src="${ctx }/res/javascript/system/orgAndStaff.js"></script>
		<link rel="stylesheet" type="text/css" href="${ctx}/res/theme-${uiInfo.theme }/comm.css">
		<style type="text/css">
		</style>
		<script type="text/javascript">
			var _orgTypes = <c:out value="${orgTypes}"  escapeXml="false"/>;
			var _isLeafs = <c:out value="${isLeafs}"  escapeXml="false"/>;
			var _orgGrades = <c:out value="${orgGrades}"  escapeXml="false"/>;
			var _genders = <c:out value="${genders}"  escapeXml="false"/>;
		</script>
	</head>
	<body class="easyui-layout">
		<!-- 机构树 -->
	    <div data-options="region:'west', border:true, title:'组织机构',split:true" style="width:300px;padding:1px 1px 1px 0px;">
    		<ul id="orgTree"></ul>
	    </div>
	    
	    <!-- 员工查询与管理tab页 -->
	    <div data-options="region:'center', border:false" >
	 		<div id="center_tab" class="easyui-tabs" data-options="fit:true, border:false">
				<div title="基本查询">
					<div class="easyui-layout" data-options="border:false, fit:true">
						<div data-options="region:'north', border:false" style="height:40px;padding:1px 1px 1px 0;">
							<div id="searchPanel" class="easyui-panel" data-options="fit:true" style="background:#F4F4F4;">
								<table style="height:100%;">
									<tr align="center">
										<td align="right" style="width: 80px;">登陆账号：</td>
										<td align="left"  style="width: 80px;"><input id="userId_input" /></td>
										<td align="right" style="width: 80px;">员工姓名：</td>
										<td align="left"  style="width: 80px;"><input id="operatorName_input" /></td>
										<td style="padding-left: 20px;">
											<a id="staff_search_btn" href="javascript:void(0);" class="easyui-linkbutton" data-options="iconCls:'icon-search'">查询</a>
										</td>
									</tr>
								</table>
							</div>
						</div>
						<div data-options="region:'center', border:false" style="padding: 0 1px 1px 0;">
							<table id="userSearchTable" ></table>
			            </div>
					</div>
				</div>
				<div title="机构信息">
					<div style="padding:5px 0 10px 0;">
						<form id="orgForm0" method="post" >
							<input name="parentOrgId" type="hidden" />
							<input name="orgSeq" type="hidden" />
							<input name="orgId" type="hidden" />
							<table class="info_table" style="width:100%;margin:auto;border:1px solid #DCDCDC;">
								<tr style="height:35px;white-space: nowrap;">
									<td style="width:20%; background:#FAFAFA;" align="right"><span style="color:red;">*</span>机构名称：</td>
									<td>
										<input name="orgName" class="inp" type="text" />
									</td>
								</tr>
								<tr style="height:35px;white-space: nowrap;">
									<td align="right" style="width:20%; background:#FAFAFA;">机构序列号：</td>
									<td>
										<input name="orgSeq" disabled="disabled" class="inp" type="text" />
									</td>
								</tr>
								<tr style="height:35px;white-space: nowrap;">
									<td align="right" style="width:20%; background:#FAFAFA;">父机构：</td>
									<td>
										<input name="parentOrgName" disabled="disabled" class="inp" type="text" />
									</td>
								</tr>
								<tr style="height:35px;white-space: nowrap;">
									<td align="right" style="width:20%; background:#FAFAFA;"><span style="color:red;">*</span>是否叶子节点：</td>
									<td>
										<select id="edit_isLeaf" name="isLeaf" style="width: 81%;height: 25px;" disabled="disabled">
									        <c:forEach items="${isLeafs }" var="temp" varStatus="status">
									             <option value="${temp.dicCode }" >${temp.dicName }</option>
									        </c:forEach>
										</select>
									</td>
								</tr>
								<tr style="height:35px;white-space: nowrap;">
									<td align="right" style="width:20%; background:#FAFAFA;"><span style="color:red;">*</span>显示顺序：</td>
									<td>
										<input name="displayOrder" maxlength="3" class="easyui-numberbox inp easyui-tooltip" 
											title="请输入一个不大于999的数值" data-options="position: 'right'"/>
									</td>
								</tr>
								<tr style="height:35px;white-space: nowrap;">
									<td align="right" style="width:20%; background:#FAFAFA;"><span style="color:red;">*</span>机构类型：</td>
									<td>
										<select id="edit_orgTypeId" name="orgTypeId" style="width: 81%;height: 25px;">
									        <c:forEach items="${orgTypes }" var="temp" varStatus="status">
									             <option value="${temp.dicCode }" >${temp.dicName }</option>
									        </c:forEach>
										</select>
									</td>
								</tr>
								<tr style="height:35px;white-space: nowrap;">
									<td align="right" style="width:20%; background:#FAFAFA;"><span style="color:red;">*</span>机构级别：</td>
									<td>
										<select id="edit_orgGrade" name="orgGrade" style="width: 81%;height: 25px;">
									        <c:forEach items="${orgGrades }" var="temp" varStatus="status">
									             <option value="${temp.dicCode }" >${temp.dicName }</option>
									        </c:forEach>
										</select>
									</td>
								</tr>
								<tr style="height:35px;white-space: nowrap;">
									<td align="right" style="width:20%; background:#FAFAFA;">联系人：</td>
									<td>
										<input name="orgContacter" class="inp" type="text"  />
									</td>
								</tr>
								<tr style="height:35px;white-space: nowrap;">
									<td align="right" style="width:20%; background:#FAFAFA;">联系电话：</td>
									<td>
										<input name="contactTel" class="inp easyui-tooltip" type="text" 
											title="电话号码最多16位" data-options="position: 'right'"/>
									</td>
								</tr>
								<tr style="height:60px;white-space: nowrap;">
									<td align="right" style="width:20%; background:#FAFAFA;">机构地址：</td>
									<td>
									    <textarea name="orgAddress" id="orgAddress" rows="3" style="width:80%;overflow:auto;resize:none;"></textarea>
									</td>
								</tr>
								<tr style="height:60%;white-space: nowrap;">
									<td align="right" style="width:20%; background:#FAFAFA;">机构说明：</td>
									<td>
										<textarea name="memo" id="memo" rows="5" style="width:80%;overflow:auto;resize:none;"></textarea>
									</td>
								</tr>
							</table>
						</form>
					</div>
					<div style="text-align:center;padding-top:5px;">
						<input style="width:60px;" id="edit_org_savebtn" type="button" value="保存" onclick="saveOrg();" />&nbsp;&nbsp;&nbsp;&nbsp;
						<input style="width:60px;" id="edit_org_resetbtn" type="button" value="重置" onclick="resetOrgForm();" />
					</div>
				</div>
				<div title="机构人员">
					<div id="watch_staff_panel" class="easyui-panel" data-options="fit:true,border:false">
						<table id="userTable" style="display:none;"></table>
					</div>
				</div>
			</div>
		</div>
		
		<!-- 机构树的右键菜单：-->
		<div id="mm" class="easyui-menu" style="width:120px;">
			<div onclick="reloadOrg()" data-options="iconCls:'icon-reload'">刷新</div>
			<div onclick="addSubOrg()" data-options="iconCls:'icon-add'">增加机构</div>
			<div onclick="removeOrg()" data-options="iconCls:'icon-remove'">删除机构</div>
		</div>
		<!-- 根节点的右键菜单 -->
		<div id="mm0" class="easyui-menu" style="width:120px;">
			<div onclick="reloadOrg()" data-options="iconCls:'icon-reload'">刷新</div>
			<div onclick="addSubOrg()" data-options="iconCls:'icon-add'">增加机构</div>
		</div>
		
		<!-- 机构表单 -->
		<div style="display:none;">
			<div id="orgDialog" style="padding:0px">
				<form id="orgForm" method="post" >
					<input name="parentOrgId" type="hidden" />
					<input name="orgSeq" type="hidden" />
	 				<table style="height:100%;width:100%;" class="info_table" >
						<tr style="height:36px;white-space: nowrap;">
							<td class="label" align="right">父机构：</td>
							<td class="attr">
								<input name="parentOrgName" disabled="disabled" style="height:15px;line-height:15px;width:238px;" />
							</td>
							<td class="label" align="right"><span style="color:red;">*</span>机构名称：</td>
							<td class="attr">
								<input name="orgName" id="add_orgName" style="height:15px;line-height:15px;width:238px;" />
							</td>
						</tr>
						<tr style="height:36px;white-space: nowrap;">
							<td class="label" align="right"><span style="color:red;">*</span>机构类型：</td>
							<td class="attr">
								<input name="orgTypeId" id="add_orgTypeId" style="width:244px;" />
							</td>
							<td class="label" align="right"><span style="color:red;">*</span>机构级别：</td>
							<td class="attr">
								<input name="orgGrade" id="add_orgGrade" style="width:244px;" />
							</td>
						</tr>
						<tr style="height:36px;white-space: nowrap;">
							<td class="label" align="right">联系人：</td>
							<td class="attr">
								<input name="orgContacter" style="height:15px;line-height:15px;width:238px;" />
							</td>
							<td class="label" align="right">联系电话：</td>
							<td class="attr">
								<input name="contactTel" class="easyui-tooltip" style="height:15px;line-height:15px;width:238px;" title="电话号码最多16位"/>
							</td>
						</tr>
						<tr style="height:36px;white-space: nowrap;">
							<td class="label" align="right"><span style="color:red;">*</span>是否叶子节点：</td>
							<td class="attr">
								<input name="isLeaf" id="add_isLeaf" style="width:244px;" />
							</td>
							<td class="label" align="right"><span style="color:red;">*</span>显示顺序：</td>
							<td class="attr" style="position: relative;">
								<input name="displayOrder" class="easyui-numberbox easyui-tooltip" maxlength="3"  title="请输入一个不大于999的数值"
									style="height:15px;line-height:15px;width:238px;" />
							</td>
						</tr>
						<tr style="height:60px;white-space: nowrap;">
							<td class="label" align="right">机构地址：</td>
							<td class="attr" style="height:60px;" colspan="3">
								<textarea name="orgAddress" style="height:96%;width:99%;overflow:auto;resize:none;"></textarea>
							</td>
						</tr>
						<tr style="height:100px;white-space: nowrap;" >
							<td class="label" align="right">机构说明：</td>
							<td class="attr" style="height:100px" colspan="3">
								<textarea name="memo" style="height:96%;width:99%;overflow:auto;resize:none;"></textarea>
							</td>
						</tr>
					</table>
				</form>
				<div style="text-align:center;padding:5px" id="org_add_btns">
					<a href="javascript:void(0)" class="easyui-linkbutton" data-options="iconCls:'icon-save'" onclick="saveOrg()">保存</a>
					<a href="javascript:void(0)" class="easyui-linkbutton" data-options="iconCls:'icon-cancel'" onclick="cancelSave()">取消</a>
				</div>
			</div>
		</div>
		
		<!-- 员工编辑窗口 -->
		<div style="display:none;">
			<div id="staffDialog" style="padding:0 0 20px;">
				<div>
					<form id="staffForm" method="post" style="margin:0;padding:0;" enctype="multipart/form-data">
						<input name="staffId" type="hidden" />
						<input name="orgId" type="hidden" />
						<div style="width:744px;margin: 20px auto 0;">
							<div class="tab_title">基本信息</div>
						</div>
						<table class="info_table" style="width:744px;margin:auto;border:1px solid #DCDCDC;">
							<tr style="height:35px;">
								<td class="label" align="right"><span style="color:red;">*</span>登录帐号：</td>
								<td class="attr"><input name="userId" type="text" /></td>
								<!-- 用户头像照片显示路径 -->
								<td style="display: none;"><input id="photo" name="photo" type="file"
								style="width: 150px;"onchange="document.getElementById('picture').value=this.value;checkPicture()"/>
								<input id="picture" name="picture" type="hidden"/>
								<!-- 图片初始化加载临时变量 -->
								<input id="pictureTemp" name="pictureTemp" type="hidden"/>
								</td>
								<td colspan="2" rowspan="5" align="center"><img id="photoImg"
									style="width: 118px; height: 140px;"
								src="${ctx }/res/images/upload/staff/meinv.jpg"  onclick="document.getElementById('photo').click();"/>
								</td>
							</tr>
							<tr style="height:35px;">
								<td class="label" align="right"><span style="color:red;">*</span>用户姓名：</td>
								<td class="attr"><input name="operatorName" type="text" /></td>
							</tr>
							<tr style="height:35px;">
								<td class="label" align="right"><span style="color:red;">*</span>用户性别：</td>
								<td class="attr"><input name="gender" id="edit_gender" /></td>
							</tr>
							<tr style="height:35px;">
								<td class="label" align="right"><span style="color:red;">*</span>密码有效期(天)：</td>
								<td class="attr">
									<input name="duration" class="easyui-numberbox" maxlength="3" style="width:140px;float:left;" />
									<div onclick="resetPwd()" class="resetBtn">密码重置</div>
								</td>
							</tr>
						</table>
						<div style="width:744px;margin: 20px auto 0;">
							<div class="tab_title">详细信息</div>
						</div>
						<table class="info_table" style="width:744px;margin:auto;border:1px solid #DCDCDC;">
							<tr style="height:35px;">
								<td class="label" align="right"><span style="color:red;">*</span>手机号码：</td>
								<td class="attr">
									<input name="mobileNum" type="text" />
								</td>
								<td class="label" align="right">电子邮箱：</td>
								<td class="attr">
									<input name="email" type="text" class="easyui-validatebox inp" data-options="validType:'email'"/>
								</td>
							</tr>
							<tr style="height:35px;">
								<td class="label" align="right">办公电话：</td>
								<td class="attr"><input name="otel" class="inp" type="text" /></td>
								<td class="label" align="right">办公邮编：</td>
								<td class="attr"><input name="ozipCode" type="text" class="easyui-numberbox inp" maxlength="6" /></td>
							</tr>
							<tr style="height:60px;">
								<td class="label" align="right">办公地址：</td>
								<td colspan="5" style="padding:0 2px;">
									<textarea name="oaddres" style="height:40px;width:93%;margin:0;overflow:auto;font-size:12px;resize:none;"></textarea>
								</td>
							</tr>
						</table>
					</form>
				</div>
			</div>
			<div style="text-align:center;padding-top:20px;" id="staff_btns">
				<a href="javascript:void(0)" class="easyui-linkbutton" data-options="iconCls:'icon-save'" onclick="saveSatff()">保存</a>
				<a href="javascript:void(0)" class="easyui-linkbutton" data-options="iconCls:'icon-cancel'" onclick="$('#staffDialog').dialog('close');">取消</a>
			</div>
		</div>
		
		<!-- 查看员工详细信息窗口 -->
		<div style="display:none;">
			<div id="watchStaffDetailDialog" style="padding:0 0 20px;">
				<div>
					<div style="width:744px;margin: 10px auto 0;">
						<div class="table_title">基本信息</div>
					</div>
					<table class="info_table" style="width:744px;margin:auto;border:1px solid #DCDCDC;">
						<tr style="height:35px;">
							<td class="label" align="right">登录帐号：</td>
							<td class="attr"><div id="check_userId" class="content"></div></td>
							<td class="label" align="right">用户姓名：</td>
							<td class="attr"><div id="check_operatorName" class="content"></div></td>
						</tr>
						<tr style="height:35px;">
							<td class="label" align="right">用户性别：</td>
							<td class="attr"><div id="check_gender" class="content"></div></td>
							<td class="label" align="right">密码有效期(天)：</td>
							<td class="attr"><div id="check_duration" class="content"></div></td>
						</tr>
					</table>
					<div style="width:744px;margin: 20px auto 0;">
						<div class="table_title">详细信息</div>
					</div>
					<table class="info_table" style="width:744px;margin:auto;border:1px solid #DCDCDC;">
						<tr style="height:35px;">
							<td class="label" align="right">手机号码：</td>
							<td class="attr"><div id="check_mobileNum" class="content"></div></td>
							<td class="label" align="right">电子邮箱：</td>
							<td class="attr"><div id="check_email" class="content"></div></td>
						</tr>
						<tr style="height:35px;">
							<td class="label" align="right">办公电话：</td>
							<td class="attr"><div id="check_otel" class="content"></div></td>
							<td class="label" align="right">办公邮编：</td>
							<td class="attr"><div id="check_ozipCode" class="content"></div></td>
						</tr>
						<tr style="height:60px;">
							<td class="label" align="right">办公地址：</td>
							<td class="attr" colspan="5" style="padding:0 2px;">
								<div id="check_oaddres" style="height:60px;overflow:auto;word-break:break-all;" ></div>
							</td>
						</tr>
						<tr style="height:60px;">
							<td class="label" align="right">所属机构：</td>
							<td id="check_orgName" colspan="5" style="padding:0 2px;">
							</td>
						</tr>
					</table>
				</div>
			</div>
		</div>
		
		<!-- 授予角色窗口  -->
		<div style="display:none;">
			<div id="grantRole_window" >
				<div class="easyui-layout" data-options="fit:true,border:false">
					<div data-options="region:'north', border:false" style="height:60px;padding:1px;" >
						<div id="role_searchPanel" class="easyui-panel" data-options="fit:true" style="background:#F4F4F4;">
							<table style="height:100%;">
								<tr align="center" >
									<td align="right" style="width: 65px;">角色名称：</td>
									<td align="left"  style="width: 70px;"><input id="roleName_input" /></td>
<!-- 									<td align="right" style="width: 65px;">角色编码：</td>
									<td align="left"  style="width: 70px;"><input id="roleCode_input" /></td> -->
									<td align="right" style="width: 65px;">是否授予:</td>
									<td align="left" style="width: 45px;">
									   <input class="easyui-combobox" id="isgranted_input" value=""
										data-options=" valueField:'value', textField:'text', panelHeight:'auto',
											data: [{text:'==请选择==',value:''},{ text: '已授予', value: '0' },{ text: '未授予', value: '1' }]" >
									</td>
									<td style="padding-left: 10px;">
										<a id="role_search_btn" href="javascript:void(0);" class="easyui-linkbutton" data-options="iconCls:'icon-search'">查询</a>
									</td>
								</tr>
							</table>
						</div>
					</div>
		    		<div data-options="region:'center',border:false" style="padding: 0 1px 1px 1px;">
		    			<table id="grantRole2UserTable"></table>
		    		</div>
	    		</div>
			</div>
		</div>
		
	</body>
</html>