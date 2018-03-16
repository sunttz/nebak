<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<!DOCTYPE HTML>
<html>
<head>
    <%@ include file="/WEB-INF/views/common/taglibs.jsp"%>
    <script type="text/javascript" src="${ctx }/res/js/ne_server/neServerModuleConfig.js"></script>
    <script>
        var ctx='${ctx}';
    </script>
    <style type="text/css">
        .info_table{border-collapse:collapse;border-spacing:0;}
        .info_table td{padding:0 15px;border:1px solid #DCDCDC;}
        .input{height:20px;line-height:15px;font-size:12px;}
        .validate_box{color:#b11516;margin-left:5px;display:none;}
    </style>
</head>

<body class="easyui-layout">
    <div data-options="region:'center',border:false" style="padding:3px;">
        <table id="listTable"></table>
    </div>

</body>

<div style="display:none;">
    <div id="neServerModuleDialog" style="padding:5px;">
        <form id="neServerModuleForm" method="post" style="height: 200px;">
            <input name="moduleId" id="moduleId" type="text" style="display: none;" />
            <input name="neServerModuleId" id="neServerModuleId" type="text" style="display: none;" />
            <table style="width:100%;" class="info_table">
                <tr style="height:30px;white-space: nowrap;">
                    <td style="width:16%;background:#FAFAFA;" ><span style="color:red;">*</span>模块名称</td>
                    <td id="moduleName_box">
                        <input id="moduleName" name="moduleName"  style="width:300px;" class="input" size="30" />
                        <span class="validate_box">
								<span style="margin-right:5px;">●</span><span class="validate_msg"></span>
							</span>
                    </td>
                </tr>
                <tr id="deviceAddrTr" style="height:30px;white-space: nowrap;">
                    <td style="width:16%;background:#FAFAFA;" ><span style="color:red;">*</span>设备地址</td>
                    <td id="deviceAddr_box">
                        <input id="deviceAddr" name="deviceAddr" style="width:300px;" class="input" size="40" />
                        <span class="validate_box">
                                <span style="margin-right:5px;">●</span><span class="validate_msg"></span>
                            </span>
                    </td>
                </tr>
                <tr id="devicePortTr" style="height:30px;white-space: nowrap;">
                    <td style="width:16%;background:#FAFAFA;" ><span style="color:red;">*</span>设备端口</td>
                    <td id="devicePort_box">
                        <input id="devicePort" name="devicePort" style="width:300px;" onkeyup="value=value.replace(/[^\d]/g,'')" class="input" size="40" value="21"/>
                        <span class="validate_box">
                                <span style="margin-right:5px;">●</span><span class="validate_msg"></span>
                            </span>
                    </td>
                </tr>
                <tr id="userNameTr" style="height:30px;white-space: nowrap;">
                    <td style="width:16%;background:#FAFAFA;" ><span style="color:red;">*</span>用户名</td>
                    <td id="userName_box">
                        <input id="userName" name="userName" style="width:300px;" class="input" size="30" />
                        <span class="validate_box">
                                <span style="margin-right:5px;">●</span><span class="validate_msg"></span>
                            </span>
                    </td>
                </tr>
                <tr id="passWordTr" style="height:30px;white-space: nowrap;">
                    <td style="width:16%;background:#FAFAFA;" ><span style="color:red;">*</span>密码</td>
                    <td id="passWord_box">
                        <input id="passWord" name="passWord"  style="width:300px;" class="input" size="30" />
                        <span class="validate_box">
                                <span style="margin-right:5px;">●</span><span class="validate_msg"></span>
                            </span>
                    </td>
                </tr>
                <tr id="bakPathTr" style="height:30px;white-space: nowrap;">
                    <td style="width:16%;background:#FAFAFA;" ><span style="color:red;">*</span>备份路径</td>
                    <td id="bakPath_box">
                        <input id="bakPath" name="bakPath" style="width:300px;" class="input" size="40"/>
                        <span class="validate_box">
                                <span style="margin-right:5px;">●</span><span class="validate_msg"></span>
                            </span>
                    </td>
                </tr>
            </table>
        </form>
    </div>
    <div style="text-align:center;padding-top:10px;" id="save_btns">
        <a href="javascript:void(0)" style="margin-right: 10px;" class="easyui-linkbutton"
           data-options="iconCls:'icon-save',plain:true" onclick="saveNeServerModule()">保存</a>
        <a href="javascript:void(0)" style="margin-left: 10px;" class="easyui-linkbutton"
           data-options="iconCls:'icon-cancel',plain:true" onclick="$('#neServerModuleDialog').dialog('close');">取消</a>
    </div>
</div>

</html>