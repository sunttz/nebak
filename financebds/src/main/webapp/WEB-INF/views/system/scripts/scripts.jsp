<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
	<%-- 如果是360浏览器使用webkit内核，其他浏览器无视此标签 --%>
	<meta name="renderer" content="webkit">
	<title>执行脚本配置</title>
	<%@ include file="/WEB-INF/views/common/taglibs.jsp"%> 
  	<script type="text/javascript" src="${ctx }/res/javascript/system/scripts/scripts.js"></script>
 	<link rel="stylesheet" type="text/css" href="${ctx}/res/theme-${uiInfo.theme }/comm.css">
	<script type="text/javascript">
		var _modules = <c:out value="${module}"  escapeXml="false"/>;
		var _scriptTypes = <c:out value="${scriptType}"  escapeXml="false"/>;
	</script>
	<style>
		html, body {
			overflow: hidden;
			height: 100%;
		}
		#textArea2 .testBtn{
			/*border: 1px solid #efefef;*/
			border: 1px solid #6a6a6a;
			background-color: #f2f2f2;
			margin: 10px auto;
			width: 60px;
			height: 25px;
			cursor: pointer;
			text-align: center;
			line-height: 25px;
			border-radius: 5px;
		}
		#textArea2 .testBtn:hover{
			background-color: #dcefff;
		}
		.row{
			display:none;
		}
		.mask {
  			 background: #a3a3a3 none repeat scroll 0 0;
  			  bottom: 0;
   			 display: none;
   			 left: 0;
   			 opacity: 0.5;
   			 position: absolute;
  			  right: 0;
  			  top: 0;
  			  z-index: 1001;
		}
		#faBuZhong { background: #FFFFFF;font-size:21px;}
	</style>
</head>
<!-- 执行脚本查询条件列表start -->
<body class="easyui-layout">
<div data-options="region:'center', border:false" >
	<div id="tabs" class="easyui-tabs" data-options="fit:true, border:false" >
		<div title="执行脚本配置" >
		    <div class="easyui-layout" data-options="border:false, fit:true">
	      		<div data-options="region:'north', border:false" style="height:75px;">
					<div id="searchPanel" class="easyui-panel" data-options="fit:true" style="background:#F4F4F4;overflow:hidden;" >
						<table style="height:100%;">
							<tr align="center" >
								<td style="padding-left:15px;">脚本编码：</td>
								<td style="padding-left:15px;">
									<input id="cScriptCode" name="scriptCode" type="text" style="width:150px"/>
								</td>
								<td style="padding-left:15px;">脚本名称：</td>
								<td style="padding-left:15px;">
									<input id="cScriptName" name="scriptName" style="width:150px" />
								</td>
								<td style="padding-left:15px;">脚本内容：</td>
								<td style="padding-left:15px;">
									<input id="cScriptContent" name="scriptContent" type="text" style="width:150px"/>
								</td>
							</tr>
							<tr align="center">
	                             <td style="padding-left:15px;">所属模块:</td>
	                             <td style="padding-left:15px;">
	                                 <input id="cModule" name="module" style="width:150px"/>
	                             </td>
	                             <td style="padding-left:15px;">脚本类型:</td>
	                             <td style="padding-left:15px;">
	                                 <input id="cScriptType" name="scriptType" style="width:150px"/>
	                             </td>
	                             <td style="padding-left:30px;"colspan="2">
									 <a id="scriptsSearchBtn" href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-search" >查询</a>&nbsp;
									 <a id="reset" href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-reload" >重置</a>
								 </td>
	                         </tr>           						
						</table>
					</div>
				</div>
				<div data-options="region:'center'">
					<table id="scriptsGrid"></table>
				</div> 
	        </div>
	    </div>
	</div>
</div>
<!-- 执行脚本查询条件列表end -->
</body>
</html>