<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
	<%-- 如果是360浏览器使用webkit内核，其他浏览器无视此标签 --%>
	<meta name="renderer" content="webkit">
	<meta charset="UTF-8">
   		<title></title>
   		<%@ include file="/WEB-INF/views/common/taglibs.jsp"%>
<script>
/*
 * 文档加载完成后，为检索码
 *1、绑定点击事件：点击就选中整个combobox的文本（原因：解决单个字backspace频繁调用后台逻辑）
 *2、绑定回车事件：当使用上下箭头导航访问下框内容时，点击回车，可将combobox的文本显示为value（原来的回车事件，点击后combobox显示检索码）
 */
$(function () {
	//绑定click
  	$('#myid').combobox('textbox').bind('click', function(e){
 		$('#myid').combobox('textbox').select();
	}); 
	
	//绑定回车事件
 	$('#myid').combobox('textbox').bind('keydown', function(e){
		if(e.keyCode==13){
			$('#myid').combobox('setText',$("#myid").combobox('getValue'));
		}
	}); 
})

//format函数供渲染combobox时使用
 function formatItem(row){
     var s = '<span style="font-weight:bold;color:#5CB85C;width:40%">' + row.operatorName + '</span>'+ '<span style="color:#888;float:right;width:60%">' + row.orgName + '</span>';
     return s;
 }
 

 function doit(){
 	alert($("#myid").combobox('getValue'));
 	alert($("#myid").combobox('getText'));
 }
</script>
</head>
<body>
    <h2>姓名检索码</h2>
    <p>easyui-combobox实现</p>
    <div style="margin:20px 0"></div>
    
    <%--使用easyui的combobox实现姓名检索码，1、mode:remote表示每次从后台获取，用户的输入参数被以get请求发送，参数名为q；2、onSelect为回调函数，可做定制化(业务处理都在这个地方)，比如为其他dom对象赋值
    3、onBeforeLoad在加载下拉框数据前，做一些动态，返回false不加载数据也就不会发送http请求url
    --%>
    <input class="easyui-combobox"  style="width:200px"  id="myid" name="operatorName" data-options="
                url: 'codeData.do',
                method: 'get',
                valueField: 'operatorName',
                textField: 'indexCode',
                panelWidth: 200,
                panelHeight: 100,
                selectOnNavigation:false,
                mode:'remote',
                formatter: formatItem,
                onSelect:function(rec){
                	$('#myid').combobox('setText',rec.operatorName);
                },
                onBeforeLoad:function(param){
                	if($('#myid').combobox('getValue')==''){
                		return false;
                	}
                }
              
            ">
            
           <input type="button" value="click" onclick="doit()" >
</body>
</html>