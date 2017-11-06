
var items = new Array();

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

function getTotalItemNum() {
	return $('.item .checkBox').length;
}

function getSelectedItemNum() {
	return $('.item .checked').length;
}

function getSelectedItemIds() {
	var itemIds = [];
	$('.item .checked').each(function() {
		itemIds.push($(this).parents('.item').attr('noticeId'));
	});
	return itemIds;
}

function selectItem($obj) {
	$obj.find('.item_mask').addClass('selected').find('.checkBox').addClass('checked');
	if(getTotalItemNum() == getSelectedItemNum()) {
		$('.title1 .checkBox').addClass('checked');
	} else {
		$('.title1 .checkBox').removeClass('checked');
	}
}

function unselectItem($obj) {
	$obj.find('.item_mask').removeClass('selected').find('.checkBox').removeClass('checked');
	$('.title1 .checkBox').removeClass('checked');
}

function itemIsSelected($obj) {
	return $obj.find('.item_mask').hasClass('selected');
}

function totalIsSelected() {
	return $('.title1 .checkBox').hasClass('checked');
}

function checkBox(srcElement, e) {
	if(itemIsSelected($(srcElement).parents('.item'))) {
		unselectItem($(srcElement).parents('.item'));
	} else {
		selectItem($(srcElement).parents('.item'));
	}
	stopBubble(e);
}

//弹出选择下线时间对话框
function showMsgbox(title) {	
	$('#publishBox').show().find('.msgbox-title').html(title);
	$('.mask').show();
}

//关闭选择下线时间对话框
function closeMsgbox() {
	$('#loseTime').val('');
	$('#publishBox').hide().find('.msgbox-title').html('');
	$('.mask').hide();
}

//下线
function downline(obj, offLine) {
	openLoad('处理中...');
	var noticeId = $(obj).parents('.item').attr('noticeId');
	$.ajax({
		type: 'POST',
		url: 'downlineNotice.do',
		data: {
			noticeId:noticeId
		},
		success : function(msgMap) {
			if(msgMap.msgFlag) {
				refreshItem(noticeId, {
					offline:'y',
					loseTime:msgMap.loseTime
				});
				closeLoad();
				showDialog('成功下线');
			}
		}
	});
}

//置顶
function stick(obj) {
	openLoad('处理中...');
	var noticeId = $(obj).parents('.item').attr('noticeId');
	$.ajax({
		type: 'GET',
		url: 'stickNotice.do?ss=' + new Date().getTime(),
		data: {
			noticeId:noticeId
		},
		success : function(data) {
			if(data) {
				refreshItem(noticeId, {
					stickFlag:1
				});
				closeLoad();
				showDialog('公告已被置顶');
			}
		}
	});
}

//取消置顶
function unStick(obj) {
	openLoad('处理中...');
	var noticeId = $(obj).parents('.item').attr('noticeId');
	$.ajax({
		type: 'GET',
		url: 'unStickNotice.do?ss=' + new Date().getTime(),
		data: {
			noticeId:noticeId
		},
		success : function(data) {
			if(data) {
				refreshItem(noticeId, {
					stickFlag:0
				});
				closeLoad();
				showDialog('公告置顶已被取消');
			}
		}
	});
}

/**
 * 打开回复功能
 * @param obj
 */
function openReply(obj, e) {
	openLoad('处理中...');
	var noticeId = $(obj).parents('.item').attr('noticeId');
	$.ajax({
		type: 'GET',
		url: 'openReply.do?ss=' + new Date().getTime(),
		data: {
			noticeId:noticeId
		},
		success : function(data) {
			if(data) {
				refreshItem(noticeId, {
					replyFlag:1
				});
				closeLoad();
				showDialog('回复功能已被开启');
			}
		}
	});
	stopBubble(e);
}

/**
 * 关闭回复功能
 * @param obj
 */
function closeReply(obj, e) {
	openLoad('处理中...');
	var noticeId = $(obj).parents('.item').attr('noticeId');
	$.ajax({
		type: 'GET',
		url: 'closeReply.do?ss=' + new Date().getTime(),
		data: {
			noticeId:noticeId
		},
		success : function(data) {
			if(data) {
				refreshItem(noticeId, {
					replyFlag:0
				});
				closeLoad();
				showDialog('回复功能已被关闭');
			}
		}
	});
	stopBubble(e);
}

//拷贝到草稿箱
function copy(obj) {
	openLoad('处理中...');
	var noticeId = $(obj).parents('.item').attr('noticeId');
	$.ajax({
		type: 'POST',
		url: 'copyToDraft.do',
		data: {
			noticeId:noticeId
		},
		success : function(data) {
			if(data) {
				closeLoad();
				showDialog('成功拷贝至草稿箱');
			}
		}
	});
}

//当前操作的公告ID
var currNoticeId = null;
//操作类型：0：发布，1：修改下线时间
var operType = null;

//再次发布
function publish(obj) {
	currNoticeId = $(obj).parents('.item').attr('noticeId');
	operType = 0;
	showMsgbox('再&nbsp;次&nbsp;发&nbsp;布');
}

//下线时间设置
function updateLoseDate(obj) {
	currNoticeId = $(obj).parents('.item').attr('noticeId');
	operType = 1;
	showMsgbox('下&nbsp;线&nbsp;时&nbsp;间&nbsp;设&nbsp;置');
}

//下线时间表单提交
function submitMsgbox() {
	openLoad('处理中...');
	var url = operType == 0?'publishNotice.do':'updateLoseDate.do';
	var loseTime = $('#loseTime').val();
	var msg = operType == 0?'成功发布':'成功修改';
	$.ajax({
		type: 'POST',
		url: url,
		data: {
			noticeId:currNoticeId,
			loseTime:loseTime
		},
		success : function(data) {
			if(data) {
				refreshItem(currNoticeId, {
					offline:'n',
					loseTime:loseTime
				});
				closeLoad();
				showDialog(msg);
				closeMsgbox();
			}
		}
	});
}

function showDialog(msg) {
	$('#msgDialog').html(msg).show();
	setTimeout(function() {
		$('#msgDialog').hide().html('');
	}, 3000);
}

function openLoad(msg) {
	$('#loadDialog').show().find('span').html(msg);
}

function closeLoad() {
	$('#loadDialog').hide().find('span').html('');
}

function getItem(noticeId) {
	for(var i = 0, n = items.length; i < n; i++) {
		if(items[i].noticeId == noticeId) {
			return items[i];
		}
	}
}

//刷新一行
function refreshItem(noticeId, newItem) {
	var item = getItem(noticeId);
	for(var s in newItem) {
		item[s] = newItem[s];
	}
	var offLine = item.offline == 'y';
	var lineIcon = offLine?'<b title="此公告已下线" class="status offline"></b>':'<b title="此公告已上线" class="status online"></b>';
	var stickIcon = item.stickFlag == 0?'':'<b title="此公告已置顶" class="stick"></b>';
	var title = item.noticeTitle;
	var noticeTitle = (title == null || title == '')?'(无标题)':title;
	var loseTime = (item.loseTime==null || item.loseTime == '')?'永不失效':item.loseTime;
	var fileFlag = item.fileNum > 0?'<b title="此公告包含附件"></b>':'';
	var replyIcon = item.replyFlag == 1?'<b title="点击关闭回复功能" onclick="closeReply(this, event);" class="reply"></b>':'<b title="点击开启回复功能" onclick="openReply(this, event);" class="unreply"></b>';
	var oper = '';
	if(offLine) {
		//下线的公告拥有的操作
		oper = 	'<div class="xiaitem" onclick="publish(this);">再次发布</div>';
	} else {
		//在线的公告拥有的操作
		var stickOper = '';
		if(item.stickFlag == 0) {
			stickOper = '<div class="xiaitem" onclick="stick(this);">置顶</div>';
		} else {
			stickOper = '<div class="xiaitem" onclick="unStick(this);">取消置顶</div>';
		}
		oper = 	stickOper+
				'<div class="xiaitem" onclick="downline(this);">手动下线</div>'+
				'<div class="xiaitem" onclick="updateLoseDate(this);">下线时间修改</div>';
	}
	
	$('.item[noticeId="' + item.noticeId +'"]').empty()
				.append('<div class="item_mask">'+
						'<div class="cols cols1">'+
							'<b onclick="checkBox(this, event);" class="checkBox"></b>'+
							lineIcon+
						'</div>'+
						'<div class="cols cols2" title="' + noticeTitle + '">' + 
							stickIcon + 
							noticeTitle + 
						'</div>'+
						'<div class="cols colsRe">'+
							replyIcon+
						'</div>'+
						'<div class="cols cols3">'+
							item.publishTime+
						'</div>'+
						'<div class="cols cols4">'+
							loseTime+
						'</div>'+
						'<div class="cols cols5">'+
							item.visitNum+
						'</div>'+
						'<div class="cols cols6">'+
							item.replyNum+
						'</div>'+
						'<div class="cols cols7">'+
							item.staffName+
						'</div>'+
						'<div class="cols cols8">'+
							fileFlag+
						'</div>'+
						'<div class="cols9">'+
							'<div tabindex="0" class="anniu">'+
								'<img class="anniu_icon" src="' + ctx + '/res/theme-' + theme + '/images/notice/anniu.png" />'+
								'<div class="rightpanel">'+
									oper+
								'</div>'+
							'</div>'+
						'</div>');
	$('.item[noticeId="' + item.noticeId + '"] .anniu').click(function(e) {
		var $panel = $(this).find('.rightpanel');
		$panel.is(':visible')?$panel.hide():$panel.show();
		e.stopPropagation();
	}).blur(function() {
		$(this).find('.rightpanel').hide();
	});
	//擦除补偿
	$('.item[noticeId="' + item.noticeId + '"] .anniu .rightpanel').hover(function() {
		$(this).parents('.anniu').unbind('blur');
	},function() {
		$(this).parents('.anniu').blur(function() {
			$(this).find('.rightpanel').hide();
		});
	});
}

//删除一行
function deleteItem(noticeId) {
	$('.item[noticeId="' + noticeId + '"]').remove();
	$('.title1 .checkBox').removeClass('checked');
	for(var i = 0; i < items.length; i++) {
		if(items[i].noticeId==noticeId) {
			items.splice(i, 1);
			break;
		}
	}
}

function previewNotice(obj) {
	var noticeId = $(obj).attr('noticeId');
	window.open("readNotice.do?noticeId=" + noticeId);
}

//追加一行
function appendItem(item) {
	var offLine = item.offline == 'y';
	var lineIcon = offLine?'<b title="此公告已下线" class="status offline"></b>':'<b title="此公告已上线" class="status online"></b>';
	var stickIcon = item.stickFlag == 0?'':'<b title="此公告已置顶" class="stick"></b>';
	var title = item.noticeTitle;
	var noticeTitle = (title == null || title == '')?'(无标题)':title;
	var loseTime = (item.loseTime==null || item.loseTime == '')?'永不失效':item.loseTime;
	var fileFlag = item.fileNum > 0?'<b title="此公告包含附件"></b>':'';
	var replyIcon = item.replyFlag == 1?'<b title="点击关闭回复功能" onclick="closeReply(this, event);" class="reply"></b>':'<b title="点击开启回复功能" onclick="openReply(this, event);" class="unreply"></b>';
	var oper = '';
	if(offLine) {
		//下线的公告拥有的操作
		oper = 	'<div class="xiaitem" onclick="publish(this);">再次发布</div>';
	} else {
		//在线的公告拥有的操作
		var stickOper = '';
		if(item.stickFlag == 0) {
			stickOper = '<div class="xiaitem" onclick="stick(this);">置顶</div>';
		} else {
			stickOper = '<div class="xiaitem" onclick="unStick(this);">取消置顶</div>';
		}
		oper = 	stickOper+
				'<div class="xiaitem" onclick="downline(this);">手动下线</div>'+
				'<div class="xiaitem" onclick="updateLoseDate(this);">下线时间修改</div>';
	}
	
	$('.items').append('<div class="item" onclick="previewNotice(this);" noticeId="' + item.noticeId + '">'+
							'<div class="item_mask">'+
							'<div class="cols cols1">'+
								'<b onclick="checkBox(this, event);" class="checkBox"></b>'+
								lineIcon+
							'</div>'+
							'<div class="cols cols2" title="' + noticeTitle + '">'+
								stickIcon+
								noticeTitle+ 
							'</div>'+
							'<div class="cols colsRe">'+
								replyIcon+
							'</div>'+
							'<div class="cols cols3">'+
								item.publishTime+
							'</div>'+
							'<div class="cols cols4">'+
								loseTime+
							'</div>'+
							'<div class="cols cols5">'+
								item.visitNum+
							'</div>'+
							'<div class="cols cols6">'+
								item.replyNum+
							'</div>'+
							'<div class="cols cols7">'+
								item.staffName+
							'</div>'+
							'<div class="cols cols8">'+
								fileFlag+
							'</div>'+
							'<div class="cols9">'+
								'<div tabindex="0" class="anniu">'+
									'<img class="anniu_icon" src="' + ctx + '/res/theme-' + theme + '/images/notice/anniu.png" />'+
									'<div class="rightpanel">'+
										oper+
									'</div>'+
								'</div>'+
							'</div>'+
						'</div>');
	$('.item[noticeId="' + item.noticeId + '"] .anniu').click(function(e) {
		var $panel = $(this).find('.rightpanel');
		$panel.is(':visible')?$panel.hide():$panel.show();
		e.stopPropagation();
	}).blur(function() {
		$(this).find('.rightpanel').hide();
	});
	//擦除补偿
	$('.item[noticeId="' + item.noticeId + '"] .anniu .rightpanel').hover(function() {
		$(this).parents('.anniu').unbind('blur');
	},function() {
		$(this).parents('.anniu').blur(function() {
			$(this).find('.rightpanel').hide();
		});
	});
	items.push(item);
}

//加载我的公告的数据
function loadNotices(offline) {
	if(!offline) {
		offline = '';
	}
	openLoad('加载中...');
	$('.items').empty();
	$.ajax({
		type : 'GET',
		url : 'getPublished.do?ss=' + new Date().getTime(),
		data : {
			staffId : -1,//-1表示查出所有
			offline : offline
		},
		success : function(noticeItems) {
			$.each(noticeItems, function(index, item) {
				appendItem(item);
			});
			$('.main').get(0).scrollTop = 0;
			closeLoad();
		}
	});
}

$(document).ready(function() {
	
	loadNotices();
	
	$('.title1 .checkBox').click(function() {
		if(totalIsSelected()) {
			$(this).removeClass('checked');
			unselectItem($('.item'));
		} else {
			$(this).addClass('checked');
			selectItem($('.item'));
		}
	});
	
	//查看按钮
	$('#lookBtn').click(function() {
		var $panel = $(this).find('.xialapanel');
		$panel.is(':visible')?$panel.hide():$panel.show();
	}).blur(function() {
		$(this).find('.xialapanel').hide();
	});
	
	//擦除补偿
	$('.xialapanel').hover(function() {
		$('#lookBtn').unbind('blur');
	},function() {
		$('#lookBtn').blur(function() {
			$(this).find('.xialapanel').hide();
		});
	});
	
	//查看全部公告按钮
	$('#lookAllBtn').click(function() {
		$('#lookBtn').blur();
		$('.title1 .checkBox').removeClass('checked');
		loadNotices();
	});
	
	//查看在线公告按钮
	$('#lookOnBtn').click(function() {
		$('#lookBtn').blur();
		$('.title1 .checkBox').removeClass('checked');
		loadNotices('n');
	});
	
	//查看下线公告按钮
	$('#lookOffBtn').click(function() {
		$('#lookBtn').blur();
		$('.title1 .checkBox').removeClass('checked');
		loadNotices('y');
	});
	
	//删除按钮
	$('#removeBtn').click(function() {
		var itemIds = getSelectedItemIds();
		if(itemIds.length == 0) {
			showDialog('没有选择任何公告，请重新选择');
			return;
		}
		if(window.confirm('是否确认删除？')){
			openLoad('删除中...');
			$.ajax({
				type: 'POST',
				contentType: 'application/json;charset=UTF-8',
				url: 'removePublisheds.do',
				data: JSON.stringify(itemIds),
				success : function(data) {
					if(data) {
						$.each(itemIds, function(index, itemId) {
							deleteItem(itemId);
						});
						closeLoad();
						showDialog('成功删除');
					}
				}
			});
		}
	});
	
	//删除按钮
	$('#moveBtn').click(function() {
		$('#lookBtn').blur();
		var itemIds = getSelectedItemIds();
		if(itemIds.length == 0) {
			showDialog('没有选择任何公告，请重新选择');
			return;
		}
		openLoad('移动中...');
		$.ajax({
			type: 'POST',
			contentType: 'application/json;charset=UTF-8',
			url: 'draftNoticeItems.do',
			data: JSON.stringify(itemIds),
			success : function(data) {
				if(data) {
					$('.item .checked').parents('.item').remove();
					$('.title1 .checkBox').removeClass('checked');
					closeLoad();
					showDialog('成功下架并移动至草稿箱');
				}
			}
		});
	});
});