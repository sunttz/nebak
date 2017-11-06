<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html>
	<head>
		<%-- 如果是360浏览器使用webkit内核，其他浏览器无视此标签 --%>
		<meta name="renderer" content="webkit">	
		<title>新建公告</title>
		<link rel="stylesheet" type="text/css" href="${ctx }/res/theme-${uiInfo.theme }/notice/notice-new.css" >
		<!-- umeditor -->
		<link href="${ctx }/res/umeditor/themes/default/css/umeditor.css" type="text/css" rel="stylesheet">
	    <script type="text/javascript" src="${ctx}/res/javascript/common/jquery-1.11.2.min.js"></script>
	    <script type="text/javascript" charset="utf-8" src="${ctx }/res/umeditor/umeditor.config.js"></script>
	    <script type="text/javascript" charset="utf-8" src="${ctx }/res/umeditor/umeditor.min.js"></script>
	    <script type="text/javascript" src="${ctx }/res/umeditor/lang/zh-cn/zh-cn.js"></script>
	    <!-- My97DatePicker -->
	    <script type="text/javascript" src="${ctx }/res/My97DatePicker/WdatePicker.js"></script>
	    <!-- File Upload -->
	    <link rel="stylesheet" type="text/css" href="${ctx}/res/jQuery-File-Upload-9.9.3/css/jquery.fileupload.css">
		<link rel="stylesheet" type="text/css" href="${ctx}/res/jQuery-File-Upload-9.9.3/css/jquery.fileupload-ui.css">
	    <script type="text/javascript" src="${ctx}/res/jQuery-File-Upload-9.9.3/js/vendor/jquery.ui.widget.js"></script>
		<script type="text/javascript" src="${ctx}/res/jQuery-File-Upload-9.9.3/js/jquery.iframe-transport.js"></script>
		<script type="text/javascript" src="${ctx}/res/jQuery-File-Upload-9.9.3/js/jquery.fileupload.js"></script>
	   	<!-- artDialog -->
		<%-- 	
		<script type="text/javascript" src="${ctx}/res/artDialog/jquery.artDialog.js?skin=aero"></script>
		<script type="text/javascript" src="${ctx}/res/artDialog/plugins/iframeTools.js"></script> 
		--%>
	    <!-- self -->
	    <script type="text/javascript">
	    	var ctx = '${ctx }';
	    	var staffId = '${sessionScope.authInfo.staffId }';
	    	var relationId = ${notice.noticeId };
	    </script>
	    <script type="text/javascript" src="${ctx }/res/javascript/system/notice/notice-new.js"></script>
	</head>
	<body>
		<div class="head">
			<div class="top_menu_wrap">
				<div id="publishBtn" class="menu first">发&nbsp;布</div>
				<div id="previewBtn" class="menu">预&nbsp;览</div>
				<div id="saveBtn" class="menu">存草稿</div>
				<c:if test="${empty param.noticeId }">
					<div id="rewriteBtn" class="menu">重&nbsp;写</div>
					<input id="flag" type="hidden" value="new" />
				</c:if>
				<c:if test="${!empty param.noticeId }">
					<div id="returnBtn" class="menu">返&nbsp;回</div>
					<input id="flag" type="hidden" value="edit" />
				</c:if>
			</div>
		</div>
		<div class="main">
			<div class="box fb">
				<label class="label">
					发布人
				</label>
				<div class="inpWrap">
					<c:if test="${empty param.noticeId }">
						${sessionScope.authInfo.userName }
					</c:if>
					<c:if test="${!empty param.noticeId }">
						${notice.staffName }
					</c:if>
					<div id="uploadBtn" class="fb_menu fileinput-button">
						添加附件(最大<span style="color:red;font-weight:bold;">10M</span>)
						<input id="fileupload" type="file" name="files[]" data-url="${ctx }/AttachmentController/upload.do" />
					</div>
					<div id="replyBtn" class="replyField" tabindex="1" value="${notice.replyFlag }">
						<div class="xiala_head"><span><c:if test="${notice.replyFlag eq 1 }">开启回复</c:if><c:if test="${notice.replyFlag eq 0 }">关闭回复</c:if></span><b class="arrow"></b></div>
						<div class="xiala_panel">
							<div value="1" class="xia_item <c:if test="${notice.replyFlag eq 1 }">curr</c:if>">开启回复</div>
							<div value="0" class="xia_item <c:if test="${notice.replyFlag eq 0 }">curr</c:if>">关闭回复</div>
						</div>
					</div>
				</div>
			</div>
			<div class="box">
				<label class="label">
					公告标题
				</label>
				<div class="inpWrap">
					<input id="title" value="${notice.noticeTitle }" style="width:100%;height:20px;line-height:20px;border:1px solid #C4C4C4;color:#555555;" />
				</div>
			</div>
			<div class="box cm">
				<label class="label">
					下线时间
				</label>
				<div class="inpWrap">
					<input id="loseTime" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm'});" value="${notice.loseTime }" style="width:100%;height:20px;line-height:20px;border:1px solid #C4C4C4;color:#555555;" class="Wdate" readonly="readonly" />
				</div>
			</div>
			<div id="fileArea" class="fileArea clearfix">
				<c:forEach items="${notice.files }" var="file">
					<div class="fileItem">
       					<b class="fileIcon"></b>
       					<div class="fileName" title="${file.fileName }">
       						${file.fileName }
       					</div>
       					<div class="fileSize">
       						${file.fileSize }<span class="fileMsg">上传完成</span>
       					</div>
       					<div onclick="removeFile(this, '${file.fileId }');" class="fileBtn">
       						删除
       					</div>
       				</div>
				</c:forEach>
			</div>
			<div class="division"></div>
			<form id="previewForm" target="_blank" action="previewNotice.do" method="post">
				<input name="noticeId" type="hidden" />
				<input name="noticeTitle" type="hidden" />
				<%-- 不被js过滤器转义< >等特殊字符（即使过滤器不开，对功能没有影响，另外只要检查到这个参数，就不转义，而不管这个参数值是什么） --%>
				<input name="inXssWhiteListQ" value="true" type="hidden" />
				<script name="noticeContent" type="text/plain" id="myEditor">${notice.noticeContent }</script>
			</form>
			<!-- <div class="tail_menu_wrap">
				<div class="menu first">发&nbsp;布</div>
				<div id="previewBtn" class="menu">预&nbsp;览</div>
				<div id="saveBtn" class="menu">存草稿</div>
				<div class="menu">取&nbsp;消</div>
			</div> -->
		</div>
		<div id="msgDialog" class="msgDialog">
		</div>
		<div id="loadDialog" class="loadDialog">
			<b></b><span></span>
		</div>
	</body>
</html>