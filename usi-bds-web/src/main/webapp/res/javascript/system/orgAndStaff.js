
//回车提交查询
$(document).keyup(function(event){
	if(event.keyCode ==13){
		if($('#grantRole_window').parent().css('display') == 'block'){
			$('#role_search_btn').trigger("click");
		} else if(tabIndex == 0){
	  		$('#staff_search_btn').trigger("click");
	  	}
	}
});

$(document).ready(function(){
	
//	resetStaffSearch();	//重置基本查询中的查询条件
	
	//查询员工信息表
	$('#userSearchTable').datagrid({
		url:'queryPageStaffByCondition.do',
		pagination:true,
		pageSize:20,
		striped:true,
		rownumbers:true,
		fit:true,
		fitColumns:true,
		singleSelect:true,
		frozenColumns:[[
		        {field:'ra',
		        	formatter: function(value, row, index) {
						return '<input id="radio_'+index+'" name="radio_name" type="radio" />';
					}
				}
	    ]],
		columns:[[
		        {field:'userId',title:'登陆账号',halign:'center',align:'center',width:70,
		        	formatter: function(value, row, index) {
						return '<a href="javascript:void(0);" class="easyui-linkbutton aBtn" onclick="watchDetail(\''
							+row.staffId+'\')">'+value+'</a>';
					}
				},
		        {field:'operatorName',title:'用户姓名',halign:'center',align:'center',width:80},
		        {field:'gender',title:'性别',halign:'center',align:'center',width:40, formatter: genderFormatter},
		        {field:'orgName',title:'所属机构',halign:'center',align:'center',width:120,formatter: titleFormatter},
		        {field:'mobileNum',title:'手机号',halign:'center',align:'center',width:70},
		        {field:'cz',title:'操作',halign:'center',align:'center',width:60,
		        	formatter: function(value, row, index) {
						return '<a href="javascript:void(0);" class="easyui-linkbutton aBtn" onclick="openGrantRoleWindow(\''
							+row.staffId+'\')">角色修改</a>';
					}
				}
		]],
		onClickRow : function(rowIndex, rowData){
			$('#radio_' + rowIndex).attr('checked',true);
		}
	}).datagrid('getPager').pagination({
		buttons: [{
				text:'修改',
				iconCls: 'icon-edit',
				handler: updateStaff
		}]
	});
	
	//机构树
	$('#orgTree').tree({
		url: "getOrgTree.do",
		animate: true,
		lines: true,
    	onClick: function(node){
    		nodeId = node.id;
    		$('#userTable').datagrid('options').url = "queryPageStaffByCondition.do";
    		var currentTab = $('#center_tab').tabs('getSelected').panel('options').title;
    		if(currentTab == '基本查询'){	//点击机构节点跳到机构信息tab页
	    		$('#center_tab').tabs('select','机构信息');
    			loadOrgInfo(node);
    		} else if(currentTab == '机构信息'){
    			//加载机构信息
    			loadOrgInfo(node);
    		} else {
    			//刷新机构人员列表
    			$('#userTable').datagrid('load',{orgId: nodeId});
    		}
    	},
    	onLoadSuccess: function(node,data){
    		if(node == null){
	    		$('#orgTree').tree('expand',$('#orgTree').tree('getRoot').target);
    		}
    	},
    	onContextMenu: function(e,node){
			e.preventDefault();
			$(this).tree('select', node.target);
			if(isTreeRoot(node)){ //根节点只能添加子节点，不能修改删除
				$('#mm0').menu('show',{
					left: e.pageX,
					top: e.pageY
				});
			} else {
				$('#mm').menu('show',{
					left: e.pageX,
					top: e.pageY
				});
			}
		},
   		onBeforeExpand:function(node) {
   			$('#orgTree').tree('update', {
				target: node.target,
				iconCls: 'icon-treeopen'
			});
			return true;
   		},
   		onBeforeCollapse:function(node) {
   			$('#orgTree').tree('update', {
				target: node.target,
				iconCls: 'icon-treeclose'
			});
			return true;
   		}
	});
	
	//机构员工表
	$('#userTable').datagrid({
		url: '',
		pagination:true,
		pageSize:20,
		striped:true,
		rownumbers:true,
		fit:true,
		fitColumns:true,
		singleSelect:false,
		frozenColumns:[[
		        {field:'ck',checkbox:true}
	    ]],
		columns:[[
		        {field:'userId',title:'登陆账号',halign:'center',align:'center',width:70},
		        {field:'operatorName',title:'用户姓名',halign:'center',align:'center',width:80},
		        {field:'gender',title:'性别',halign:'center',align:'center',width:40,formatter: genderFormatter},
		        {field:'orgName',title:'所属机构',halign:'center',align:'center',width:120,formatter: titleFormatter},
		        {field:'mobileNum',title:'手机号',halign:'center',align:'center',width:70},
		        {field:'cz',title:'操作',halign:'center',align:'center',width:60,
		        	formatter: function(value, row, index) {
						return '<a href="javascript:void(0);" class="easyui-linkbutton aBtn" onclick="openGrantRoleWindow(\''
							+row.staffId+'\')">角色修改</a>';
					}
				}
		]]
	}).datagrid('getPager').pagination({
		buttons: [{
				text:'新增',
				iconCls: 'icon-add',
				handler: addStaff
		},'-',{
				text:'修改',
				iconCls: 'icon-edit',
				handler: updateStaff
		},'-',{
				text:'删除',
				iconCls: 'icon-remove',
				handler: delStaff
		}]
	});
	
	//当选择tab页时
	$('#center_tab').tabs({
		onSelect: function(title,index){
			tabIndex = index;
			var orgnode = $('#orgTree').tree('getSelected');
			var node = $('#orgTree').tree('getRoot');	//取得根节点
			if(title == '机构人员'){
				if(orgnode == null){
					$('#orgTree').tree('select',node.target);
					nodeId = node.orgId;
					$('#userTable').datagrid('options').url = "queryPageStaffByCondition.do";
					$('#userTable').datagrid('load',{orgId: node.orgId});
				} else {
					$('#userTable').datagrid('load',{orgId: orgnode.orgId});
				}
			} else if(title == '机构信息'){
				if(orgnode == null){	//没有选择时默认选择根节点
					loadOrgInfo(node);
					$('#orgTree').tree('select',node.target);
					nodeId = node.orgId;
				} else {
					loadOrgInfo(orgnode);
				}
			}
		}
	});
	
	//员工查询按钮
	$('#staff_search_btn').click(function(){
		$('#userSearchTable').datagrid('load', {
			userId : $.trim($('#userId_input').val()),
			operatorName: $.trim($('#operatorName_input').val())
		});
	});
	
	/**************下拉框初始化***************/
	$('#add_orgTypeId').combobox(combooption);
	$('#add_orgGrade').combobox(combooption);
	$('#add_isLeaf').combobox(combooption);
	$('#edit_gender').combobox({
		 valueField: 'dicCode',
		 textField: 'dicName',
		 panelHeight:'auto',
		 editable:false,
		 width:206
	});
	
	//机构编辑窗口
	$('#orgDialog').dialog({
		width: 760,
		height: 390,
		iconCls: 'icon-edit',
		closed: true,
		cache: false,
		href: '',
		modal: true,
		buttons: '#org_add_btns'
	});
	
	//员工信息编辑窗口
	$('#staffDialog').dialog({
		width: 760,
		height: 'auto',
		iconCls: 'icon-edit',
		closed: true,
		cache: false,
		href: '',
		modal: true,
		buttons: '#staff_btns'
	});
	
	//授予角色窗口
	$('#grantRole_window').dialog({
		title: '授予角色',
		width: 800,
		height: 460,
		iconCls: 'icon-edit',
		closed: true,
		cache: false,
		href: '',
		modal: true,
		onBeforeClose: resetRoleSearch
	});
	
	//查看员工信息窗口
	$('#watchStaffDetailDialog').dialog({
		width: 760,
		height: 'auto',
		title: '详细信息',
		iconCls: 'icon-save',
		closed: true,
		cache: false,
		href: '',
		modal: true,
		onBeforeClose: clearWatchDialog
	});
	
	//员工角色授予列表
	$('#grantRole2UserTable').datagrid({
		url:'',
		striped:true,
		rownumbers:true,
		pagination:true,
		pageSize:10,
		fit:true,
		fitColumns:true,
		singleSelect:false,
		frozenColumns:[[
		        {field:'ck',checkbox:true}
	    ]],
		columns:[[
		        {field:'roleId',title:'角色ID',halign:'center',align:'center',width:40},
		        {field:'roleName',title:'角色名称',halign:'center',align:'center',width:90},
//		        {field:'roleCode',title:'角色编码',halign:'center',align:'center',width:90},
		        {field:'roleDesc',title:'角色说明',halign:'center',align:'center',width:120},
		        {field:'isGranted',title:'是否授予',halign:'center',align:'center',width:80,
		        	formatter:function(value,row,index){
		        		if(value == '0'){
		        			return '<div style="color:green;">已授予</div>';
		        		} else {
		        			return '<div style="color:red;">未授予</div>';
		        		}
		        	}
		        }
		]]
	}).datagrid('getPager').pagination({
		buttons: [{
				text:'授予角色',
				iconCls: 'icon-add',
				handler: grantRole2Staff
		},'-',{
				text:'取消授予',
				iconCls: 'icon-remove',
				handler: revokeRole
		}]
	});
	
	//角色查询按钮
	$('#role_search_btn').click(function(){
		var isgranted =  $('#isgranted_input').combobox('getValue');
		$('#grantRole2UserTable').datagrid('load', {
//			roleCode : $('#roleCode_input').val().trim(),
			roleName: $.trim($('#roleName_input').val()),
			isGranted: isgranted,
			staffId: choosedstaff
		});
	});
});

//下拉框的通用设置
var combooption = {
	 valueField: 'dicCode',
	 textField: 'dicName',
	 panelHeight:'auto',
	 editable:false
};

var nodeId = ''; //当前选中的机构节点
var isOrgAdd = true; //当前是增加子节点还是修改节点,true增加,false修改
var tabIndex = 0;	//tab页索引

//增加子机构
function addSubOrg(){
 	 $('#add_orgTypeId').combobox('loadData',_orgTypes);
 	 $('#add_orgGrade').combobox('loadData',_orgGrades);
 	 $('#add_isLeaf').combobox('loadData',_isLeafs);
	 isOrgAdd = true;
	 var orgInfo = $('#orgTree').tree('getSelected');
	 if(orgInfo) {
	 	 if(orgInfo.isLeaf == '1') {
	 	 	$.messager.alert('提示','已经是叶子机构，不能添加子机构！','warning');
	 	 	return;
	 	 }
		 $('#orgForm').form('clear').form('load',{
		 	parentOrgName : orgInfo.orgName,
		 	parentOrgId : orgInfo.orgId,
		 	orgSeq : orgInfo.orgSeq
		 });
		 $('#add_orgTypeId').combobox('select',_orgTypes[0].dicCode);
		 $('#add_orgGrade').combobox('select',_orgGrades[0].dicCode);
		 $('#add_isLeaf').combobox('select',_isLeafs[0].dicCode);
		 $('#orgDialog').dialog('setTitle', '添加子机构').dialog('open');
	 } else {
	 	$.messager.alert('提示','请先选择一个父机构！','info');
	 }
}

//加载机构树信息用于修改
function loadOrgInfo(node){
	isOrgAdd = false;
	if(isTreeRoot(node)){
		$('#orgForm0').form('clear').form('load',node);
		$('#edit_org_savebtn').attr("disabled",true);
		$('#edit_org_resetbtn').attr("disabled",true);
	} else {
		var parent = $('#orgTree').tree('getParent',node.target);
		$('#orgForm0').form('clear').form('load',{'parentOrgName' : parent.orgName}).form('load',node);
		$('#edit_org_savebtn').attr("disabled",false);
		$('#edit_org_resetbtn').attr("disabled",false);
	}
}

//保存新增加的或修改过的机构
function saveOrg(){
	var node = $('#orgTree').tree('getSelected');
	var parent = $('#orgTree').tree('getParent',node.target);
	if(isOrgAdd){	//增加子机构
		$('#orgForm').form('submit', {
			url: 'saveOrg.do',
			onSubmit: function(){
				if($(this).form('validate') && validateOrgAddForm()){
					return true;
				} else {
					$.messager.alert('提示','请完成表单填写','info');
					return false;
				}
			},
			success:function(data){
				if (data == "success") {
					$.messager.alert('提示','保存成功','info');
					$('#orgDialog').dialog('close');
					reloadNode(node);	//刷新节点
				} else {
					$.messager.alert('提示','保存失败','error');
				}
			}
		});
	} else {	//修改机构
		$('#orgForm0').form('submit', {
			url: 'saveOrg.do',
			onSubmit: function(){
				if($(this).form('validate') && validateOrgEditForm()){
					return true;
				} else {
					$.messager.alert('提示','请完成表单填写','info');
					return false;
				}
			},
			success:function(data){
				if (data == "success") {
					$.messager.alert('提示','保存成功','info');
					reloadNode(parent);	//刷新节点
					$('#orgTree').tree('select',parent.target);
					loadOrgInfo(parent);//表单置为父节点
				} else {
					$.messager.alert('提示','保存失败','error');
				}
			}
		});
	}
}

function reloadNode(node){
	 $('#orgTree').tree('reload',node.target);
}

//刷新机构节点
function reloadOrg(){
	 var row = $('#orgTree').tree('getSelected');
	 $('#orgTree').tree('reload',row.target);
}

//取消按钮
function cancelSave(){
	$('#orgDialog').dialog('close');
}

//删除机构
function removeOrg(){
	 var row = $('#orgTree').tree('getSelected');
	 var parent = $('#orgTree').tree('getParent',row.target);
	 if(row) {
	 	$.messager.confirm('确认','确定删除选中的机构?',function(r){
			if (r){
				$.ajax({
					async : false,
					cache : false,
					type : 'POST',
					dataType : 'text',
					url : 'delOrg.do',
					data : {
						'orgId' : row.orgId
					},
					success : function(data) { // 请求成功后处理函数。
						if (data == "success") {
							$.messager.alert('提示','操作成功','info');
							reloadNode(parent);
							$('#orgTree').tree('select',parent.target);
							loadOrgInfo(parent);	//重新加载父节点
						} else if (data == 'cascade'){
							$.messager.alert('提示','该机构存在子机构或员工信息，请先删除子机构及下属员工信息','info');
						} else {
							$.messager.alert('提示','操作失败','error');
						}
					}
				});
			}
		});
	 } else {
	 	$.messager.alert('提示','请选择您要删除的机构！','info');
	 }
}

//查看员工详细信息
function watchDetail(staffId){
	 $.ajax({
		async : false,
		cache : false,
		type : 'POST',
		dataType : 'json',
		url : 'getStaffDetailById.do',
		data : {
			'staffId' : staffId
		},
		success : function(data) {
			setValue2WatchDetail(data);
		    $('#watchStaffDetailDialog').dialog('open');
		}
	 });
}

//清空员工详细信息窗口
function clearWatchDialog(){
	$('#check_userId').html('');
	$('#check_operatorName').html('');
	$('#check_gender').html('');
	$('#check_mobileNum').html('');
	$('#check_duration').html('');
	$('#check_otel').html('');
	$('#check_email').html('');
	$('#check_idCardNum').html('');
	$('#check_oaddres').html('');
	$('#check_orgName').html('');
}

//员工详细信息窗口赋值
function setValue2WatchDetail(data){
	$('#check_userId').html(data.userId);
	$('#check_operatorName').html(data.operatorName);
	$('#check_gender').html(translate(data.gender,_genders));
	$('#check_mobileNum').html(data.mobileNum);
	$('#check_duration').html(data.duration);
	$('#check_otel').html(data.otel);
	$('#check_email').html(data.email);
	$('#check_ozipCode').html(data.ozipCode);
	$('#check_oaddres').html(data.oaddres);
	$('#check_orgName').html(data.orgName);
}

//删除员工信息
function delStaff(){
	var rows = $('#userTable').datagrid('getSelections');
	var ids = [];
	if(rows.length > 0) {
		for(var i=0; i< rows.length; i++) ids.push(rows[i].staffId);
		$.messager.confirm('确认','确定删除选中的员工?',function(r){
			if (r){
				$.ajax({
					async : false,
					cache : false,
					type : 'POST',
					dataType : 'text',
					url : 'delStaff.do',
					data : {
						'staffIds' : ids
					},
					success : function(data) {
						if (data == "success") {
							$.messager.alert('提示','操作成功','info');
							$('#userTable').datagrid('load');
						} else {
							$.messager.alert('提示','操作失败','error');
						}
					}
				});
			}
		});
	} else {
		$.messager.alert('提示','请至少选中一行','info');
	}
}

//新增员工信息
function addStaff(){
	$('#edit_gender').combobox('loadData',_genders);
	if(nodeId != ''){
		$('#staffForm').form('clear').form('load',{ orgId : nodeId });
		//显示默认的头像照片
		$('#photoImg').attr("src", "/financebds/res/images/upload/staff/meinv.jpg");
		$('#edit_gender').combobox('select',_genders[0].dicCode);
		$('#staffForm input[name="userId"]').attr("disabled",false);
		$('#staffDialog').dialog('setTitle','增加员工信息').dialog('open');
	} else {
		$.messager.alert('提示','请先选择所属机构','info');
	}
}

//修改员工信息
function updateStaff(){
	$('#edit_gender').combobox('loadData',_genders);
	var rows;
	if(tabIndex == 0){
		rows = $('#userSearchTable').datagrid('getSelections');
	} else if(tabIndex == 2){
		rows = $('#userTable').datagrid('getSelections');
	}
	if(rows.length < 1){
		$.messager.alert('提示','请先选择一条记录','info');
	} else if(rows.length > 1){
		$.messager.alert('提示','只能选择一条记录','info');
	} else {
		$.ajax({
			async : false,
			cache : false,
			type : 'POST',
			dataType : 'json',
			url : 'getStaffDetailById.do',
			data : {
				'staffId' : rows[0].staffId
			},
			success : function(data) {
				$('#staffForm').form('clear').form('load', data);
				//当用户有头像照片时显示照片
				if(data.picture!="" && data.picture!=null){
					var pic = data.picture.substring(data.picture.lastIndexOf("/"));
					$('#pictureTemp').val(data.picture);//临时记录图片地址
					$('#photoImg').attr("src",  "/financebds/orgAndStaff/images"+pic);	
				}
				$('#staffForm input[name="userId"]').attr("disabled",true);
			 	$('#staffDialog').dialog('setTitle','修改员工信息').dialog('open');
			}
	 });
	}
}

var choosedstaff = '';
//打开授予角色窗口
function openGrantRoleWindow(staffId){
	choosedstaff = staffId;
	$('#grantRole2UserTable').datagrid('options').url = "queryPageRole.do";
	$('#grantRole2UserTable').datagrid('load',{staffId: staffId});
	$('#grantRole_window').dialog('open');
}

//给员工授予角色
function grantRole2Staff(){
	var rows = $('#grantRole2UserTable').datagrid('getSelections');
	var roleIds = [];
	if(rows.length == 0){
		$.messager.alert('提示','请至少选中一行','info');
	} else {
		for(var i=0; i< rows.length; i++){
			if(rows[i].isGranted == 1){
				roleIds.push(rows[i].roleId);
			}
		}
		if(roleIds.length > 0){
			$.ajax({
				async : false,
				cache : false,
				type : 'POST',
				dataType : 'text',
				url : 'saveStaffRole.do',
				data : {
					'opflag' : 0, //操作标识0表示授予1表示取消授予
					'staffId' : rows[0].staffId,
					'roleIds' : roleIds
				},
				success : function(data) {
					if (data == "success") {
						$.messager.alert('提示','操作成功','info');
						$('#grantRole2UserTable').datagrid('load');
					} else {
						$.messager.alert('提示','操作失败','error');
					}
				}
			});
		} else {
			$.messager.alert('提示','请至少选中一行未授予角色','info');
		}
	}
}

//取消对员工的角色授予
function revokeRole(){
	var rows = $('#grantRole2UserTable').datagrid('getSelections');
	var roleIds = [];
	if(rows.length == 0){
		$.messager.alert('提示','请至少选中一行','info');
	} else {
		for(var i=0; i< rows.length; i++){
			if(rows[i].isGranted == 0){
				roleIds.push(rows[i].roleId);
			}
		}
		if(roleIds.length > 0){
			$.ajax({
				async : false,
				cache : false,
				type : 'POST',
				dataType : 'text',
				url : 'saveStaffRole.do',
				data : {
					'opflag' : 1,
					'staffId' : rows[0].staffId,
					'roleIds' : roleIds
				},
				success : function(data) {
					if (data == "success") {
						$.messager.alert('提示','操作成功','info');
						$('#grantRole2UserTable').datagrid('load');
					} else {
						$.messager.alert('提示','操作失败','error');
					}
				}
			});
		} else {
			$.messager.alert('提示','请至少选中一行已授予角色','info');
		}
	}
}
//添加头像照片后预览员工图片
function checkPicture(){
	change('photoImg','photo');
}

function change(picId,fileId) {
    var pic = document.getElementById(picId);
    var file = document.getElementById(fileId);
    if(window.FileReader){//chrome,firefox7+,opera,IE10,IE9，IE9也可以用滤镜来实现
       oFReader = new FileReader();
       oFReader.readAsDataURL(file.files[0]);
       oFReader.onload = function (oFREvent) {pic.src = oFREvent.target.result;};        
    }
    else if (document.all) {//IE8-
        file.select();
        var reallocalpath = document.selection.createRange().text//IE下获取实际的本地文件路径
        if (window.ie6) pic.src = reallocalpath; //IE6浏览器设置img的src为本地路径可以直接显示图片
        else { //非IE6版本的IE由于安全问题直接设置img的src无法显示本地图片，但是可以通过滤镜来实现，IE10浏览器不支持滤镜，需要用FileReader来实现，所以注意判断FileReader先
            pic.style.filter = "progid:DXImageTransform.Microsoft.AlphaImageLoader(sizingMethod='image',src=\"" + reallocalpath + "\")";
            pic.src = 'data:image/gif;base64,R0lGODlhAQABAIAAAP///wAAACH5BAEAAAAALAAAAAABAAEAAAICRAEAOw==';//设置img的src为base64编码的透明图片，要不会显示红xx
        }
    }
    else if (file.files) {//firefox6-
        if (file.files.item(0)) {
            url = file.files.item(0).getAsDataURL();
            pic.src = url;
        }
    }
}
//保存员工信息
function saveSatff(){
	$('#staffForm').form('submit', {
		url: 'saveStaff.do',
		onSubmit: function(param){
			if($(this).form('validate') && validateStaffForm()){
				return true;
			} else {
				$.messager.alert('提示','请完成表单填写','info');
				return false;
			}	
		},
		success:function(data){
			if (data == "success") {
				$.messager.alert('提示','保存成功','info');
				if(tabIndex == 0){	//刷新基本查询页的表格
					$('#staff_search_btn').trigger("click");
				} else if(tabIndex == 2){	//刷新机构人员页的表格
					$('#userTable').datagrid('load');
				}
				$('#staffDialog').dialog('close');
			} else if(data == "exist"){
				$.messager.alert('提示','登录帐号已存在，请使用其他帐号','info');
			}else if(data == "errorsuccess"){
				$.messager.alert('提示','头像图片格式不对，请重新选择','error');
			} else {
				$.messager.alert('提示','保存失败','error');
				$('#staffDialog').dialog('close');
			}
		}
	});
}

//重置密码
function resetPwd(){
	$.messager.confirm('确认','确定重置密码?',function(r){
		if (r){
			$.ajax({
				async : false,
				cache : false,
				type : 'POST',
				dataType : 'text',
				url : 'resetPwd.do',
				data : {
					'staffId' : $('#staffForm input[name="staffId"]').val(),
					'userId' : $('#staffForm input[name="userId"]').val()
				},
				success : function(data) {
					if (data == "success") {
						$.messager.alert('提示','操作成功','info');
					} else {
						$.messager.alert('提示','操作失败','error');
					}
				}
			});
		}
	});
}

//验证员工表单
function validateStaffForm(){
	var flag = true;
	if($.trim($('#staffForm input[name="userId"]').val()) == '') flag = false;
	if($.trim($('#staffForm input[name="operatorName"]').val()) == '') flag = false;
	if($.trim($('#staffForm input[name="duration"]').val()) == '') flag = false;
	if($.trim($('#staffForm input[name="mobileNum"]').val()) == '') flag = false;
	return flag;
}

//验证机构表单
function validateOrgAddForm(){
	var flag = true;
	if($.trim($('#orgForm input[name="orgName"]').val()) == '') flag = false;
	if($.trim($('#orgForm input[name="displayOrder"]').val()) == '') flag = false;
	return flag;
}

function validateOrgEditForm(){
	var flag = true;
	if($.trim($('#orgForm0 input[name="orgName"]').val()) == '') flag = false;
	if($.trim($('#orgForm0 input[name="displayOrder"]').val()) == '') flag = false;
	return flag;
}

//字段较长的增加title显示
function titleFormatter(value, row, index){
	if(value) return "<div title='"+value+"'>"+value+"</div>";
	return "";
}

//翻译字典字段
function translate(value,dict){
	for(var i=0;i<dict.length;i++){
		if(value == dict[i].dicCode){
			return dict[i].dicName;
		}
	}
}

//性别
function genderFormatter(value, row, index){
	for(var i = 0; i < _genders.length; i++){
		if(value == _genders[i].dicCode){
			return _genders[i].dicName;
		}
	}
}

//是否根节点
function isTreeRoot(node){
	var is = /^\d+\.$/.test(node.orgSeq);
	return is;
}

//重置机构表单
function resetOrgForm(){
	var orgnode =  $('#orgTree').tree('getSelected');
	loadOrgInfo(orgnode);
}

//重置人员查询条件
function resetStaffSearch(){
	$('#userId_input').val('');
	$('#operatorName_input').val('');
}

//重置角色授予查询条件
function resetRoleSearch(){
//	$('#roleCode_input').val('');
	$('#roleName_input').val('');
	$('#isgranted_input').combobox('setValue','');
}
