<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="UTF-8"%>
<!DOCTYPE HTML>
<html>
	<head>
	   	<title>任务配置</title>
	  	<%@ include file="/WEB-INF/views/common/taglibs.jsp"%>
	  	<script type="text/javascript" src="${ctx }/res/javascript/common/json2.js"></script>
	  	<script type="text/javascript" src="${ctx }/res/javascript/system/sysTask/taskCfg.js"></script>
	  	<style type="text/css">
	  		.info_table{border-collapse:collapse;border-spacing:0;}
	  		.info_table td{padding:0 15px;border:1px solid #DCDCDC;}
	  		.input{height:20px;line-height:15px;font-size:12px;}
	  		.validate_box{color:#b11516;margin-left:5px;display:none;}
	  	</style>
	  	<script type="text/javascript">
	  		//所登录的服务器的ip
	  		var serverips = '<c:out value="${serverips}" escapeXml="false"/>';
	  	</script>
	</head>
	<body class="easyui-layout">
		<div data-options="region:'center',border:false" style="padding:3px;">
			<table id="taskTable"></table>
		</div>
		<div data-options="region:'south', border:false"
		style="height: 250px; padding: 1px 1px 0px 0px;">
		<div id="searchPanel1" class="easyui-panel" data-options="fit:true,title:'备注'"
			style="background: #F4F4F4;">
			<pre>
CRON表达式：
	一个cron表达式有至少6个（也可能7个）有空格分隔的时间元素。
	按顺序依次为
		秒（0~59） 
		分钟（0~59） 
		小时（0~23） 
		天（月）（0~31，但是你需要考虑你月的天数） 
		月（0~11） 
		天（星期）（1~7 1=SUN 或 SUN，MON，TUE，WED，THU，FRI，SAT） 
		年份（1970~2099）
	其中每个元素可以是一个值(如6),一个连续区间(9-12),一个间隔时间(18/4)(从18开始每隔4),一个列表(1,3,5),通配符。
	由于"月份中的日期"和"星期中的日期"这两个元素互斥的,如果其中一个有值就必须要对另一个设置“?”</pre>
		</div>
	</div>
		<div style="display:none;">
			<div id="taskDialog" style="padding:5px;">
				<form id="taskForm" method="post">
					<input name="jobId" style="display:none;" />
					<table style="width:100%;" class="info_table">
						<tr style="height:30px;white-space: nowrap;">
							<td style="width:16%;background:#FAFAFA;" ><span style="color:red;">*</span>任务名称</td>
							<td id="jobName_box">
								<input id="jobName" name="jobName" style="width:420px;" class="input" size="30" onblur="validateTaskName();" />
								<span class="validate_box">
									<span style="margin-right:5px;">●</span><span class="validate_msg"></span>
								</span>
							</td>
						</tr>
						<tr style="height:30px;white-space: nowrap;">
							<td style="width:16%;background:#FAFAFA;" ><span style="color:red;">*</span>任务分组</td>
							<td id="jobGroup_box">
								<select style="width:425px;border:1px solid #DCDCDC;" id="jobGroup" name="jobGroup" >
									<option value="" >==请选择==</option>
							        <c:forEach items="${jobGroupList}" var="temp">
							             <option value="${temp.dicCode}">${temp.dicName}</option>
							        </c:forEach>
								</select>
								<span class="validate_box">
									<span style="margin-right:5px;">●</span><span class="validate_msg"></span>
								</span>
							</td>
						</tr>
						<tr style="height:30px;white-space: nowrap;">
							<td style="width:16%;background:#FAFAFA;" ><span style="color:red;">*</span>任务实现类</td>
							<td id="jobBeanName_box">
								<select style="width:425px;border:1px solid #DCDCDC;" id="jobBeanName" name="jobBeanName" >
									<option value="" >==请选择==</option>
							        <c:forEach items="${beans}" var="temp">
							             <option value="${temp}">${temp}</option>
							        </c:forEach>
								</select>
								<span class="validate_box">
									<span style="margin-right:5px;">●</span><span class="validate_msg"></span>
								</span>
							</td>
						</tr>
						<tr style="height:30px;white-space: nowrap;">
							<td style="width:16%;background:#FAFAFA;" ><span style="color:red;">*</span>服务器ip</td>
							<td id="ipAddr_box">
								<input id="ipAddr" name="ipAddr"  style="width:420px;" class="input" size="30" onblur="validateIpAddr();" />
								<span class="validate_box">
									<span style="margin-right:5px;">●</span><span class="validate_msg"></span>
								</span>
							</td>
						</tr>
						<tr style="height:30px;white-space: nowrap;">
							<td style="width:16%;background:#FAFAFA;" ><span style="color:red;">*</span>定时表达式</td>
							<td id="cronExpr_box">
								<input id="cronExpr" name="cronExpr" style="width:420px;" class="input" size="40" onblur="validateCronExpr();"/>
								<span class="validate_box">
									<span style="margin-right:5px;">●</span><span class="validate_msg"></span>
								</span>
							</td>
						</tr>
						<tr style="height:30px;white-space: nowrap;">
							<td style="width:16%;background:#FAFAFA;" ><span style="color:red;">*</span>随工程启动</td>
							<td id="isAutoStart_box">
								<input id="y0" name="isAutoStart" type="radio" value="1" style="margin-left:20px;" checked="checked" />
								<label for="y0">是</label>
								<input id="y1" name="isAutoStart" type="radio" value="0" style="margin-left:30px;" />
								<label for="y1">否</label>
							</td>
						</tr>
						<tr style="white-space: nowrap;">
							<td valign="middle" style="width:16%;background:#FAFAFA;" >任务参数</td>
							<td>
								<textarea id="inParam" name="inParam" class="txt" style="width:420px;height:140px;overflow:auto;resize:none;"></textarea>
							</td>
						</tr>
						<tr style="white-space: nowrap;">
							<td valign="middle" style="width:16%;background:#FAFAFA;" >备注</td>
							<td>
								<textarea id="jobMemo" name="jobMemo" class="txt" style="width:420px;height:40px;overflow:auto;resize:none;"></textarea>
							</td>
						</tr>
					</table>
				</form>
			</div>
			<div style="text-align:center;padding-top:10px;" id="add_btns">
				<a href="javascript:void(0)" style="margin-right: 10px;" class="easyui-linkbutton" 
					data-options="iconCls:'icon-save',plain:true" onclick="saveJob()">保存</a>
				<a href="javascript:void(0)" style="margin-left: 10px;" class="easyui-linkbutton" 
					data-options="iconCls:'icon-cancel',plain:true" onclick="$('#taskDialog').dialog('close');">取消</a>
			</div>
		</div>
	</body>
</html>