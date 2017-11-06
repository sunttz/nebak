
function showDialog(msg) {
	$('#msgDialog').show().html(msg);
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

//追加回复
function appendReply(reply) {
	$('.reply-list').append('<div class="reply-item">'+
								'<div class="user-info clearfix">'+
									'<div class="user-icon">'+
										'<img src="' + ctx + '/res/images/system/notice/sex1.jpg" />'+
									'</div>'+
									'<div class="user-cont">'+
										'<div class="firstline">'+
										reply.userName+
										'</div>'+
										'<div class="secondline">'+
											'<span class="time">' + reply.publishTime +'</span>'+
										'</div>'+
									'</div>'+
								'</div>'+
								'<div class="msg-info">' + reply.replyContent + '</div>'+
							'</div>');
}

$(document).ready(function() {
	
	$("span.holder").jPages({ 
	  containerID : "itemContainer",
	  //first : "首页",
	  previous : "上页",
	  next : "下页",
	  //last : "最后一页",
	  perPage : 10,
	  minHeight : false
    });
	
	$('body').bind("scroll", function(){
		if($(".reply-title").offset().top <= 0) {
			$('.reply-title').addClass('fix');
		}
		if($('.reply-panel').offset().top >= 40) {
			$('.reply-title').removeClass('fix');
		}
	});
	var um = UM.getEditor('myEditor', {
		readonly:replyFlag == 0,
    	autoHeightEnabled:false,
    	zIndex:400,
    	toolbar:[
    	            'forecolor backcolor | bold italic underline strikethrough | superscript subscript |',
    	            ' emotion cleardoc paragraph | fontfamily fontsize' ,
    	            '| justifyleft justifycenter justifyright justifyjustify |',
    	            'link unlink'
    	        ]
    });
	
//	//快捷回复
//	$('#quickBtn').click(function() {
//		//让编辑器获得焦点
//		um.focus();
//	});
	
	//发表按钮
	$('#publishBtn').click(function() {
		if(replyFlag == 0) {
			alert('回复功能被关闭！');
			return;
		}
		if(!um.hasContents()) {
			alert('回复内容不能为空！');
			return;
		}
		openLoad('处理中...');
		var reply = {
			noticeId:noticeId,
			staffId:user.staffId,
			replyContent:um.getContent(),
			//不被js过滤器转义< >等特殊字符（即使过滤器不开，对功能没有影响，另外只要检查到这个参数，就不转义，而不管这个参数值是什么）
			inXssWhiteListQ:"true"
		};
		$.ajax({
			type : 'POST',
			url : ctx + '/noticeReply/publish.do',
			data : reply,
			success : function(msgMap) {
				closeLoad();
				if(msgMap.flag) {
//					um.setContent('');
//					
//					reply.userName = user.userName;
//					reply.replyId = msgMap.replyId;
//					reply.publishTime = msgMap.publishTime;
//					appendReply(reply);
//					
//					$('.reply-title .total').html($('.reply-title .total').html() - 0 + 1);
					showDialog('回复发表成功 ');
					setTimeout(function() {
						window.location.reload(true);
					}, 1000);
				} else {
					showDialog('回复发表失败 ');
				}
			}
		});
	});
	
});