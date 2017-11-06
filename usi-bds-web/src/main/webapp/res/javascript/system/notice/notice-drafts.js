
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
		itemIds.push($(this).attr('noticeId'));
	});
	return itemIds;
}

function selectItem($obj) {
	$obj.find('.item_mask').addClass('selected').find('.checkBox').addClass('checked');
	if(getTotalItemNum() == getSelectedItemNum()) {
		$('.menu .checkBox').addClass('checked');
	} else {
		$('.menu .checkBox').removeClass('checked');
	}
}

function unselectItem($obj) {
	$obj.find('.item_mask').removeClass('selected').find('.checkBox').removeClass('checked');
	$('.menu .checkBox').removeClass('checked');
}

function itemIsSelected($obj) {
	return $obj.find('.item_mask').hasClass('selected');
}

function totalIsSelected() {
	return $('.menu .checkBox').hasClass('checked');
}

function checkBox(srcElement, e) {
	if(itemIsSelected($(srcElement).parents('.item'))) {
		unselectItem($(srcElement).parents('.item'));
	} else {
		selectItem($(srcElement).parents('.item'));
	}
	stopBubble(e);
}

function showEditView(obj, e) {
	var noticeId = $(obj).find('.checkBox').attr('noticeId');
	window.location.href = ctx + '/notice/new.do?noticeId=' + noticeId;
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

//加载草稿箱的数据
function loadDrafts() {
	openLoad('加载中...');
	$('.items').empty();
	$.ajax({
		type : 'GET',
		url : 'getDrafts.do?ss=' + new Date().getTime(),
		data : {
			staffId : staffId
		},
		success : function(noticeItems) {
			$.each(noticeItems, function(index, noticeItem) {
				var title = noticeItem.noticeTitle;
				var noticeTitle = (title == null || title == '')?'(无标题)':title;
				var fileFlag = noticeItem.fileNum > 0?'<b title="此公告包含附件"></b>':'';
				$('.items').append('<div class="item" onclick="showEditView(this, event);">'+
										'<div class="item_mask">'+
											'<div class="cols1">'+
												'<b onclick="checkBox(this, event);" class="checkBox" noticeId="' + noticeItem.noticeId + '"></b>'+
											'</div>'+
											'<div class="cols2">' + noticeTitle + '</div>'+
											'<div class="cols3">'+
												fileFlag+
												noticeItem.createTime+
											'</div>'+
										'</div>'+
									'</div>');
				});
			$('.main').get(0).scrollTop = 0;
			closeLoad();
		}
	});
}

$(document).ready(function() {
	
	loadDrafts();
	
	$('.menu .checkBox').click(function() {
		if(totalIsSelected()) {
			$(this).removeClass('checked');
			unselectItem($('.item'));
		} else {
			$(this).addClass('checked');
			selectItem($('.item'));
		}
	});
	
	//删除按钮
	$('#removeBtn').click(function() {
		var itemIds = getSelectedItemIds();
		if(itemIds.length == 0) {
			showDialog('没有选择任何草稿，请重新选择');
			return;
		}
		if(window.confirm('是否确认删除？')){
			openLoad('删除中...');
			$.ajax({
				type: 'POST',
				contentType: 'application/json;charset=UTF-8',
				url: 'removeDrafts.do',
				data: JSON.stringify(itemIds),
				success : function(data) {
					if(data) {
						$('.item .checked').parents('.item').remove();
						$('.menu .checkBox').removeClass('checked');
						closeLoad();
						showDialog('成功删除草稿');
					}
				}
			});
		}
	});
	
	//发布按钮
	$('#publishBtn').click(function() {
		var itemIds = getSelectedItemIds();
		if(itemIds.length == 0) {
			showDialog('没有选择任何草稿，请重新选择');
			return;
		}
		openLoad('发布中...');
		$.ajax({
			type: 'POST',
			contentType: 'application/json;charset=UTF-8',
			url: 'publishNoticeItems.do',
			data: JSON.stringify(itemIds),
			success : function(data) {
				if(data) {
					$('.item .checked').parents('.item').remove();
					$('.menu .checkBox').removeClass('checked');
					closeLoad();
					showDialog('成功发布');
				}
			}
		});
	});
	
	//刷新按钮
	$('#reloadBtn').click(function() {
		$('.menu .checkBox').removeClass('checked');
		loadDrafts();
	});
});