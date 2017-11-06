
function getMenuByTabId(tabId) {
	for(var i = 0, menus = authMenus, n = menus.length; i < n; i++) {
		var menu = menus[i];
		if(menu.menuId + '_tab' == tabId) {
			return menu;
		}
	}
}

function toUrl(menuAction) {
	if(!(menuAction.startWith('http://') || menuAction.startWith('HTTP://'))) {
		return ctx + menuAction;
	}
	return menuAction;
}

var pwdFlag = true;
function resetPwd() {
	$('#pwd_panel input[type="password"]').val('');
};

function showPwdPanel () {
	$('#pwd_panel').show();
	$('#oldPwd').focus();
}

function hidePwdPanel () {
	$('#pwd_panel').hide();
	resetPwd();
	$('#warn_area').hide().find('i').removeClass('pwd_right');
	$('#warn_tip').html('').removeClass('green');
}

var setting = {
	view: {
		showLine: false,
		showIcon: false,
		selectedMulti: false,
		dblClickExpand: false,
		addDiyDom: addDiyDom
	},
	data: {
		key:{
			name:'menuName'
		}
	},
	callback: {
		beforeClick: beforeClick,
		onClick:function(event, treeId, treeNode) {
			$('iframe[name="c_iframe"]').attr('src', toUrl(treeNode.menuAction));
		}
	}
};

var zNodes = null;

function addDiyDom(treeId, treeNode) {
	var spaceWidth = 15;
	var switchObj = $('#' + treeNode.tId + '_switch'),
	icoObj = $('#' + treeNode.tId + '_ico');
	switchObj.remove();
	icoObj.before(switchObj);

	var spaceStr = '<span style="display: inline-block;width:' + (spaceWidth * treeNode.level)+ 'px"></span>';
	switchObj.before(spaceStr);
}

function beforeClick(treeId, treeNode) {
	if (treeNode.isParent) {
		var zTree = $.fn.zTree.getZTreeObj('treeMenu');
		zTree.expandNode(treeNode);
		return false;
	}
	return true;
}


function stopDefault(e) {  
    //如果提供了事件对象，则这是一个非IE浏览器  
    if(e && e.preventDefault) {  
    	//阻止默认浏览器动作(W3C)
    	e.preventDefault();  
    } else {
    	//IE中阻止函数器默认动作的方式   
    	window.event.returnValue = false;   
    }  
    return false;  
}

function stopBubble(e){
    //一般用在鼠标或键盘事件上
   if(e && e.stopPropagation){
        //W3C取消冒泡事件
       e.stopPropagation();
    }else{
        //IE取消冒泡事件
       window.event.cancelBubble = true;
    }
}

function tabClick(obj) {
	var tabId = $(obj).attr('id');
//	var $content = $('#' + tabId + '_content');
	var menu = getMenuByTabId(tabId);
	$('.h_tab').removeClass('selected');
	$(obj).addClass('selected');
	
	var $l_menu = $('.l_menu');
	if(menu.children.length > 0) {
		//有左菜单
		$('.left').show().find('.l_menu').empty();
		var children = menu.children;
		for(var i = 0, n = children.length; i < n; i++) {
			if(children[i].children.length == 0) {
				//非叶子菜单
				if(children[i].isLeaf == '0'){
					var content = '<li class="menu_item_one hasc expand">'+
									'<a class="onehref" href="javascript:;">' + children[i].menuName + '</a>'+
								'</li>';
					$l_menu.append(content);
				} else {
					var content = '<li class="menu_item_one noc">'+
									'<a class="onehref" href="' + toUrl(children[i].menuAction) + '" target="mainFrame">' + children[i].menuName + '</a>'+
								'</li>';
					$l_menu.append(content);
				}
			} else {
				var content = '<li class="menu_item_one hasc expand">'+
								'<a class="onehref" href="javascript:;">' + children[i].menuName + '</a>'+
								'<ul class="c_menu">';
				for(var j = 0, m = children[i].children.length; j < m; j++) {
					content += '<li class="menu_item_two">'+
									'<a class="twohref" href="' + toUrl(children[i].children[j].menuAction) + '" target="mainFrame">' + children[i].children[j].menuName + '</a>'+
								'</li>';
				}
				content += '</ul></li>';
				$l_menu.append(content);
			}
		}
		//叶子菜单
		var $leafMenu = $('.noc .onehref,.twohref');
		if($leafMenu.length > 0){
			$leafMenu.get(0).click();
		} else {
			$('.center').addClass('noLeft').find('iframe').get(0).src = '';
		}
		$('.center').removeClass('noLeft');
	} else {
		//没有左菜单
		$('.left').hide().find('.l_menu').empty();
		var iframeUrl = '';
		if(menu.isLeaf == '1'){
			iframeUrl = toUrl(menu.menuAction);
		}
		$('.center').addClass('noLeft').find('iframe').get(0).src = iframeUrl;
	}
	$('.center .pullbar_r').hide();
	var menuId = $(obj).attr('menuId');
	$('.xia_item').removeClass('curr');
	$('.xia_item[menuId="' + menuId + '"]').addClass('curr');
}

function tabClose(obj, e) {
	stopBubble(e);
	var flag = $(obj).parents('.h_tab').hasClass('selected');
	$(obj).parents('.h_tab').remove();
	if(flag) {
		$('.h_tab').get(0).click();
	}
}

$(document).ready(function() {
	//给left及center赋自适应高度跟宽度
	function autoHeightOrWidth(){
		h=$(window).height()-82;
		if($('.left').is(':hidden')){
			wCenter=$(window).width();
		}else{
			wCenter=$(window).width()-$('.left').width()-1;
		}
		$('.left').height(h);
		$('.center').height(h).width(wCenter);
	}
	autoHeightOrWidth();
	$(window).resize(autoHeightOrWidth);
	
	//没有菜单时也不显示更多
	if(authMenus.length == 0) {
		$('.h_xiala').remove();
		$('.left').remove();
		$('.center').remove();
	} else {
		for(var i = 0, n = authMenus.length; i < n; i++) {
			var menu = authMenus[i];
			if(i < lv1MenuNum) {
				//多加一个a标签只是为了在ie6下能够让伪类hover有效果，没有其他任何作用
				$('.default_items').append('<a style="color:#6A6A6A" href="####"><div id="' + menu.menuId + '_tab_map" menuId="' + menu.menuId + '" class="xia_item">' + menu.menuName + '</div></a>');
			} else {
				if(i == lv1MenuNum) {
					$('.h_xiala_panel').append(
							'<div class="extend_items">'+
							'<div class="xia_split"></div>'+
					'</div>');
				}
				//多加一个a标签只是为了在ie6下能够让伪类hover有效果，没有其他任何作用
				$('.extend_items').append('<a style="color:#6A6A6A" href="####"><div id="' + menu.menuId + '_tab_map" menuId="' + menu.menuId + '" class="xia_item">' + menu.menuName + '</div></a>');
			}
		}
	}
	
	$('#close_item').click(function(){
		var flag = $('.h_tab.selected').hasClass('extend');
		$('.h_tab.extend').remove();
		if(flag) {
			$('.h_tab').get(0).click();
		}
		$('.h_xiala').blur();
	});
	
	$('.h_xiala').click(function() {
		//alert();
		$('.h_xiala_panel').is(':visible')?$('.h_xiala_panel').hide():$('.h_xiala_panel').show();
	}).blur(function() {
		$('.h_xiala_panel').hide();
	});
	
	//擦除补偿
	$('.h_xiala_panel').hover(function() {
		$('.h_xiala').unbind('blur');
	},function() {
		$('.h_xiala').blur(function() {
			$('.h_xiala_panel').hide();
		});
	});
	
	$('.default_items .xia_item').click(function() {
		var menuId = $(this).attr('menuId');
		var $menuTab = $('.h_tab[menuId="' + menuId + '"]');
		if(!$menuTab.hasClass('selected')) {
			$menuTab.click();
		}
		$('.h_xiala').blur();
	});
	
	$('.extend_items .xia_item').click(function() {
		var menuId = $(this).attr('menuId');
		var $menuTab = $('.h_tab[menuId="' + menuId + '"]');
		if($menuTab.length == 0) {
			//追加标签页
			var menuName = $(this).html();
			$('.h_xiala').before('<div onclick="tabClick(this);" menuId="' + menuId + '" id="' + menuId + '_tab" title="' + menuName + '" class="h_tab extend">' + menuName +
									'<b onclick="tabClose(this, event);" title="点击关闭">x</b>'+
								'</div>');
			$('#' + menuId + '_tab').click();
		} else {
			if(!$menuTab.hasClass('selected')) {
				$menuTab.click();
			}
		}
		$('.h_xiala').blur();
	});
	
	$('.l_menu').on('click','.hasc .onehref',function(){
		var $obj = $(this).parent();
		$obj.hasClass('expand') ? $obj.removeClass('expand') : $obj.addClass('expand');
	}).on('click','.noc .onehref,.twohref',function(){	//一级的叶子菜单和二级的叶子菜单
		$('.noc .onehref,.twohref').removeClass('curr');
		$(this).addClass('curr');
	});
	
	/******************/
	$('#pwd_btn').click(function() {
		$('#pwd_panel').is(':visible')?hidePwdPanel():showPwdPanel();
	})
		$('#pwd_close').click(function() {
		hidePwdPanel();
	});

	$('#pwd_submit').click(function() {
		if(!pwdFlag) {
			return;
		}
		var oldPwd = $('#oldPwd').val();
		var newPwd = $('#newPwd').val();
		var againPwd = $('#againPwd').val();
		
		if(oldPwd.length == 0) {
			$('#warn_area').show();
			$('#warn_tip').html('旧的密码不能为空！');
			$('#oldPwd').focus();
			return;
		}
		
		if(newPwd.length < 6) {
			$('#warn_area').show();
			$('#warn_tip').html('密码长度至少为6位！');
			$('#newPwd').focus();
			return;
		}
		
		if(againPwd.length < 6) {
			$('#warn_area').show();
			$('#warn_tip').html('密码长度至少为6位！');
			$('#againPwd').focus();
			return;
		}
		
		if(newPwd != againPwd) {
			$('#warn_area').show();
			$('#warn_tip').html('新密码两次输入不一致！');
			$('#newPwd').val('').focus();
			$('#againPwd').val('');
			return;
		}
		var userId = $('#userId').text();
		$.ajax( {
			type:'post',
			url:"updatePwd.do",
			data:{
				oldPwd: md5(userId+''+oldPwd),
				newPwd: md5(userId+''+newPwd)
			},
			success:function(msgMap) {
				if(msgMap.msgFlag) {
					pwdFlag = false;
					$('#warn_area').show().find('i').addClass('pwd_right');
					$('#warn_tip').addClass('green').html(msgMap.msgDesc + '(3)');
					var i = 2;
					var timer = null;
					timer=setInterval(function() {
						if(i == 0) {
							clearInterval(timer);
							hidePwdPanel();
							pwdFlag = true;
						} else {
							$('#warn_area').show();
							$('#warn_tip').html(msgMap.msgDesc + '(' + i + ')');
						}
						i--;
					},1000);
				} else {
					resetPwd();
					$('#oldPwd').focus();
					$('#warn_area').show();
					$('#warn_tip').html(msgMap.msgDesc);
				}
			},
			error:function() {
			}
		});
	});
	
	$('#oldPwd').keydown(function(event) {
		if(event.keyCode == 13) {
			$("#newPwd").focus();
		}
	});
	
	$('#newPwd').keydown(function(event) {
		if(event.keyCode == 13) {
			$("#againPwd").focus();
		}
	});
	
	$('#againPwd').keydown(function(event) {
		if(event.keyCode == 13) {
			$('#pwd_submit').click();
		}
	});
	/******************/
	
	/**
	 * 退出按钮的点击事件
	 */
	$('#logout_btn').click(function() {
		location.href = ctx + "/login/logout.do?ss=" + new Date().getTime();
	});
	
	//展开收缩按钮
	$('.left .pullbar_l').click(function() {
		$('.left').hide();
		$('.center').addClass('noLeft').find('.pullbar_r').show();
		$('.center').width($(window).width());
	});
	$('.center .pullbar_r').click(function() {
		$('.left').show();
		$('.center').removeClass('noLeft').find('.pullbar_r').hide();
		$('.center').width($(window).width()-$('.left').width()-1);
	});
	
	$('.h_tab:first').click();
});