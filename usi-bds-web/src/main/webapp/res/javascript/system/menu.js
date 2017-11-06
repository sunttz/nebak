var menuData = null;
var url = null;
function append(){
	$('#menuUpdate').form('clear');
	var node = $('#menuTree').tree('getSelected');
	var seq=node.menuSeq;
	var seqs= new Array(); //定义一数组 
	seqs=seq.split("."); //字符分割
	var tab = $('#menuTab').tabs('getTab', 0);
	$('#menuTab').tabs('update', {
		tab:tab,
		options:{
			title:'添加菜单'
		}
	});
	$('#menuUpdate').form('load',{
		parentName:node.text,
		menuSeq:node.menuSeq,
		parentId:node.menuId,
		isLeaf:1
	});
	document.getElementById('menuAction').disabled = '';
	url='insertMenu.do';
	if(seqs.length==4){
		$('input:radio[value=0]').attr("disabled","disabled");
	}
	ret=true;
	document.getElementById('saveButton').disabled = false;
	document.getElementById('resetButton').disabled = false;
}
function removeit(){
	var node = $('#menuTree').tree('getSelected');
//	var childs= $('#menuTree').tree('getChildren',node.target);
//	var count = childs.length;
	if (node.childMenu == 0){
		$.messager.confirm('确认','确定要删除吗?',function(r){
		if (r){
			$.post('deleteMenu.do',{menuId:node.menuId},function(result){
				if (result == "success"){
					$.messager.alert('成功','删除成功','info');
					$('#menuTree').tree('reload');
					$('#menuUpdate').form('clear');
					$('#menuUpdate').form('load',{isLeaf:1});
					
				}else {
					$.messager.alert('失败','删除失败','error');
//					$('#menuTree').tree('reload');
				}
			});
		}
		});
	}else{
		$.messager.alert('失败','删除失败,该菜单下存在子菜单!','error');
	}
}
//是否是叶子菜单单选按钮的点击事件
function checkAction(){
	var chkObjs = document.getElementsByName("isLeaf");
    for(var i=0;i<chkObjs.length;i++){
        if(chkObjs[i].checked){
        	var ret =chkObjs[i].value;
        	if(ret == '0'){
        		document.getElementById('menuAction').disabled = 'disabled'; 
        		document.getElementById('menuAction').value='';
        		$('#isLfStar').hide();
        	}else{
        		document.getElementById('menuAction').disabled = '';
        		$('#isLfStar').show();
        	}
            break;
        }
    }
}
function isEmptyObject(obj){
	for(var n in obj){
		return false;
	}
	return true; 
}
var ret=true;
function saveUser(){
	$('#menuUpdate').form('submit',{
		url: url,
		onSubmit: function(param){
			var node = $('#menuTree').tree('getSelected');
			var a=$('input:radio[name=isLeaf]:checked').val();
			if(!ret){
				var seq=node.menuSeq;
				var seqs= new Array(); //定义一数组 
				seqs=seq.split("."); //字符分割
				if(node.childMenu!=0 & a==1){
					$.messager.alert('提示','不可修改菜单为子节点！','info');
					return false;
				}else if(seqs.length==5 & a!=1){
					$.messager.alert('提示','该节点只能是子节点！','info');
					return false;
				}
			}
			if($('#menuUpdate input[name="menuId"]').val()=='') {
				$('#menuUpdate input[name="menuId"]').val('-1');
			}
			if($('#menuUpdate input[name="displayOrder"]').val()=='') {
				$('#menuUpdate input[name="displayOrder"]').val('1');
			}
			if(document.getElementById("parentName").value==''){
				$.messager.alert('提示','请选择父菜单！','info');
				return false;
			}
			if(document.getElementById("menuName").value==''){
				$.messager.alert('提示','菜单名称不能为空！','info');
				return false;
			}
			if(document.getElementById("displayOrder").value > 999){
				$.messager.alert('提示','菜单显示顺序不能大于三位数！','info');
				return false;
			}
			if($.trim($("#menuAction").val()) == '' && a == '1' ){
				$.messager.alert('提示','菜单动作不能为空！','info');
				return false;
			}
		},
		success: function(result){
			if (result == "success"){
				 $.messager.alert('成功','保存成功','info');
				 $('#menuTree').tree('reload');
				 $('#menuUpdate').form('clear');
				 $('#menuUpdate').form('load',{isLeaf:1});
			} else {
				$.messager.alert('失败','保存失败','error');
//				$('#menuTree').tree('reload');
			}
		}
	});
}

function reload() {
	$('#menuTree').tree('reload', menuData.target);
}

function clearForm(){
	if(ret){
		document.getElementById('menuAction').value='';
		document.getElementById('menuName').value='';
		document.getElementById('displayOrder').value='';
		document.getElementById('menuMemo').value='';
//		$('input:radio[name=isLeaf]:checked').val('');
		$('input:radio[value=1]').attr('checked', true);
		document.getElementById('menuAction').disabled = '';
	}else{
		var node = $('#menuTree').tree('getSelected');
		$('#menuUpdate').form('load',{
			menuId:node.menuId,
			parentName:node.parentName,
			menuName:node.text,
			menuAction:node.menuAction,
			displayOrder:node.displayOrder,
			isLeaf:node.isLeaf,
			menuMemo:node.menuMemo,
			menuSeq:node.menuSeq,
			parentId:node.parentId
		});
		if(node.isLeaf == '0'){
			document.getElementById('menuAction').disabled = 'disabled';  
		}else{
			document.getElementById('menuAction').disabled = '';
		}
		$('input:radio[value=0]').attr("disabled",false);
	}
}

$(document).ready(function() {
	
	//刷新时清空表单
	$('#menuUpdate').form('clear');
	$('#menuUpdate').form('load',{isLeaf:1});
	
	//加载菜单树
	$('#menuTree').tree({
		url: 'getMenuTree.do',
	    method: 'get',
	    animate: true,
	    onContextMenu: function(e,node){
	    	e.preventDefault();
	    	$(this).tree('select',node.target);
	    	if(node.id == '0'){
	    		$('#mm2').menu('show',{
	    			left: e.pageX,
	    			top: e.pageY
	    		});
	    	}else if(!isEmptyObject(node.children) ||  node.isLeaf == '0'){
	    		$('#mm').menu('show',{
	    			left: e.pageX,
	    			top: e.pageY
	    		});
	    	}else{
	    		$('#mm1').menu('show',{
	    			left: e.pageX,
	    			top: e.pageY
	    		});
	    	}
	    },
	    onBeforeLoad:function(node, param) {
	    	param.ss = new Date().getTime();
	    },
		onLoadSuccess:function(node,data) {
//			var rootNode1 = $('#menuTree').tree('getRoot');
//			if(rootNode1) {
//				var node1 = $('#menuTree').tree('getChildren', rootNode1.target)[0];
//				$('#menuTree').tree('select', node1.target);
//			}
//			var rootNode = $('#menuTree').tree('getRoot');
//			$('#menuTree').tree('select',rootNode.target);
    		if(node == null){
	    		$('#menuTree').tree('expand',$('#menuTree').tree('getRoot').target);
    		}
		},
		onSelect:function(){
			var node = $('#menuTree').tree('getSelected');
			menuData = node;
			var tab = $('#menuTab').tabs('getTab', 0);
			$('#menuTab').tabs('update', {
				tab:tab,
				options:{
					title:node.text
				}
			});
			$('#menuUpdate').form('load',{
				menuId:node.menuId,
				parentName:node.parentName,
				menuName:node.text,
				menuAction:node.menuAction,
				displayOrder:node.displayOrder,
				isLeaf:node.isLeaf,
				menuMemo:node.menuMemo,
				menuSeq:node.menuSeq,
				parentId:node.parentId
			});
			if(node.isLeaf == '0'){
				document.getElementById('menuAction').disabled = 'disabled';  
			}else{
				document.getElementById('menuAction').disabled = '';
			}
			if(node.id=='0'){
				document.getElementById('saveButton').disabled = true;
				document.getElementById('resetButton').disabled = true;
			}else{
				document.getElementById('saveButton').disabled = false;
				document.getElementById('resetButton').disabled = false;
			}
			url= 'updateMenu.do';
			$('input:radio[value=0]').attr("disabled",false);
			ret=false;
		}
	});
});