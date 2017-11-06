//验证用户名有没有输入
function validateUserId(userId) {
	return !(userId.length == 0 || userId == '用户名');
}
//验证密码有没有输入
function validatePassword(password) {
	return !(password.length == 0 || password == '密码');
}

//文档加载完...
$(document).ready(function() {
	
	$('#userId').focus().keydown(function(event) {
		if(event.keyCode == 13) {
			$("#password1").focus();
		}
	});

	$("#password1").focus(function(){
	    $("#password1").val(""); 
	    $("#password").val(""); 
		$("#login_pass_tips").hide();
	}).blur(function(){
		var thistext = $(this).val();
		if(thistext==""){
			$("#login_pass_tips").show();
		}
	}).keyup(function(event) {
		var pass = document.getElementById("password1"); 
		var j_pass = document.getElementById("password"); 
		var keycode=event.keyCode;
		//回车
		if(keycode == 13) {
			$('#loginBtn').click();
		//退格
		}else if(keycode == 8){
			if(pass.value.length==0){
				 j_pass.value="";
			}else{
				var l_j_pass = j_pass.value.length;
				if(l_j_pass>0){
					 j_pass.value=j_pass.value.substring(0,l_j_pass-1);	
				}
			}
		}else{
			if(pass.value.substring(pass.value.length-1)!="*"){
				j_pass.value=j_pass.value+pass.value.substring(pass.value.length-1);	
			}
		}
		pass.value=pass.value.replace(/./g,'*');
	});
	
	//密码提示的点击事件
	$("#login_pass_tips").click(function(){
		$(this).hide();
		$("#password1").focus();
	});
	
	//登录按钮
	$('#loginBtn').click(function() {
		var userId = $('#userId').val();
		var password = $('#password').val();
		if(!validateUserId(userId)) {
			$('#err_area').show();
			$('#err_tip').html('您还没有输入用户名！');
			return;
		}
		if(!validatePassword(password)) {
			$('#err_area').show();
			$('#err_tip').html('您还没有输入密码！');
			return;
		}
		//密码加盐加密并把原来值替换，提交加密参数以提供安全
		password = md5(userId+""+password);
		$('#password').val(password);
		
		$.ajax({
			'url': ctx + "/login/doLogin.action",
	        'type': 'post',
	        'data': {
	        	userId: userId,
	        	password: password
	        },
	        'success': function(msgMap) {
	        	if(msgMap.msgFlag) {
	        		window.location = ctx + "/index.do";
	        	} else {
	        		$('#err_area').show();
	    			$('#err_tip').html(msgMap.msgDesc);
	    		    $("#password1").val(""); 
	    		    $("#password").val(""); 
	        	}
	        }
		});
	});
});