var pieChart = null;
var lineChart = null;
$(document).ready(function() {
	//列表初始化
	$('#listTable').datagrid({
		url:'getFailResult.do',
		title:'备份失败列表',
		fit:true,
		fitColumns:true,
		rownumbers:true,
        pageSize : 10,
		striped:true,
		pagination:true,
		singleSelect:false,
		queryParams: {
			dateTime:$('#date_time').val()
		},
		columns:[[
				{field:'serverId',title:'主键ID',hidden:true,halign:'center',align:'center',width:100},
				{field:'orgId',title:'机构ID',hidden:true,halign:'center',align:'center',width:100},
				{field:'orgName',title:'所属地区',halign:'center',align:'center',width:100},
				{field:'deviceName',title:'设备名称',halign:'center',align:'center',width:100},
				{field:'deviceType',title:'网元类型',halign:'center',align:'center',width:100},
				// {field:'deviceAddr',title:'设备地址',halign:'center',align:'center',width:100,
                 //    formatter: function(value, row, index) {
                 //        if(value == null || value == ""){
                 //        	value = "-";
				// 		}
				// 		return value;
                 //    }
				// },
            	{field:'bakType',title:'备份类型',halign:'center',align:'center',width:60,
                	formatter: function(value, row, index) {
						var bakType = value;
						if(value=='0'){
							bakType = "被动取";
						}else if(value=='1'){
							bakType = "主动推";
						}
						return bakType;
                }},
                {field:'moduleNum',title:'模块数',halign:'center',align:'center',width:40,
                    formatter: function(value, row, index) {
                        if(row.bakType == "1"){
                            value = "-";
                        }
                        return value;
                }},
				{field:'remarks',title:'备注',halign:'center',align:'center',width:100},
				{field:'bakFlag',title:'操作结果',halign:'center',align:'center',width:100,
					formatter: function(value,row,index){
		        		if(value==0){
		        			return "<font color='red'>失败</font>";
		        		}else{
		        			return "成功";
		        		}
		        	}
				},
				{field:'createDate',title:'备份时间',halign:'center',align:'center',width:100}
		]]
	});
	
	//点击搜索按钮
	$('#job_log_btn').click(function(){
        ajax_bakResultByDay();
		$('#listTable').datagrid('load', {
			dateTime:$('#date_time').val()
		});
	});

    // 初始化指定天备份结果
    pieChart = echarts.init(document.getElementById('pie'));
    // 指定图表的配置项和数据
    var option = {
        title : {
            text: '指定天备份结果',
            subtext: '默认今天',
            x:'center'
        },
        tooltip : {
            trigger: 'item',
            formatter: "{a} <br/>{b} : {c} ({d}%)"
        },
        color:['#b3d9c6','#6cacde','#f6da22'],
        legend: {
            orient: 'vertical',
            left: 'left',
            data: ['成功数','失败数']
        },
        series : [
            {
                name: '备份结果',
                type: 'pie',
                radius : '55%',
                label: {
                    normal: {
                        formatter: '{a|{a}}{abg|}\n{hr|}\n  {b|{b}：}{c}  {per|{d}%}  ',
                        backgroundColor: '#eee',
                        borderColor: '#aaa',
                        borderWidth: 1,
                        borderRadius: 4,
                        rich: {
                            a: {
                                color: '#999',
                                lineHeight: 18,
                                align: 'center'
                            },
                            hr: {
                                borderColor: '#aaa',
                                width: '100%',
                                borderWidth: 0.5,
                                height: 0
                            },
                            b: {
                                fontSize: 12,
                                lineHeight: 25
                            },
                            per: {
                                color: '#eee',
                                backgroundColor: '#334455',
                                padding: [2, 4],
                                borderRadius: 2
                            }
                        }
                    }
                },
                center: ['50%', '60%'],
                data:[
                    {value:135, name:'成功数'},
                    {value:1548, name:'失败数'}
                ],
                itemStyle: {
                    emphasis: {
                        shadowBlur: 10,
                        shadowOffsetX: 0,
                        shadowColor: 'rgba(0, 0, 0, 0.5)'
                    }
                }
            }
        ]
    };
    // 使用刚指定的配置项和数据显示图表
    pieChart.setOption(option);
    ajax_bakResultByDay();


    // 初始化指定时间段备份结果
    lineChart = echarts.init(document.getElementById('line'));
    // 指定图表的配置项和数据
    var option2 = {
        title: {
            text: '最近一个月备份结果'
        },
        tooltip : {
            trigger: 'axis',
            axisPointer: {
                type: 'cross',
                label: {
                    backgroundColor: '#6a7985'
                }
            }
        },
        color:['#b3d9c6','#6cacde','#f6da22'],
        legend: {
            data:['成功数','失败数']
        },
        grid: {
            left: '3%',
            right: '4%',
            bottom: '3%',
            containLabel: true
        },
        xAxis : [
            {
                type : 'category',
                boundaryGap : false,
                data : []
            }
        ],
        yAxis : [{type : 'value'}],
        series : []
    };
    // 使用刚指定的配置项和数据显示图表。
    lineChart.setOption(option2);
    ajax_bakResultByTime();

});

// 加载指定天备份结果
function ajax_bakResultByDay() {
	var createDate = $('#date_time').val();
    pieChart.showLoading({
        text : '数据获取中',
        effect: 'whirling'
    });
    $.ajax({
        async : false,
        cache : false,
        type : 'POST',
        dataType : 'json',
        url : 'getBakResultByDay.do',
        data : {
            createDate : createDate
        },
		error : function () {
            option = {
                series : [{data:[]}]
            };
            pieChart.hideLoading();
            pieChart.setOption(option);
        },
        success : function(data) { // 请求成功后处理函数。
			if(data != null && data != "") {
                var succNum = data.succNum;
                var failNum = data.failNum;
                option = {
                    series : [
                        {
                            data:[
                                {value:succNum, name:'成功数'},
                                {value:failNum, name:'失败数'}
                            ],
                        }
                    ]
                };
            }else{
                option = {
                    series : [{data:[]}]
                };
			}
            pieChart.hideLoading();
			pieChart.setOption(option);
        }
    });
}

// 加载指定时间段备份结果
function ajax_bakResultByTime() {
    lineChart.showLoading({
        text : '数据获取中',
        effect: 'whirling'
    });
    $.ajax({
        async : false,
        cache : false,
        type : 'POST',
        dataType : 'json',
        url : 'getBakResultByTime.do',
        data : {
            startDate : getDateStr(-30),
            endDate : getDateStr(0)
        },
        error : function () {
            option = {
                xAxis : [{data : []}],
                series : []
            };
            lineChart.hideLoading();
            lineChart.setOption(option);
        },
        success : function(data) { // 请求成功后处理函数。
            // console.info(data);
            if(data != null && data != "") {
                var xAxisData = [];
                var succData = [];
                var failData = [];
                for(var i=0; i < data.length; i++){
                    var row = data[i];
                    xAxisData.push(row.createDate);
                    succData.push(row.succNum);
                    failData.push(row.failNum);
                }

                option = {
                    xAxis : [
                        {
                            data : xAxisData
                        }
                    ],
                    series : [
                        {
                            name:'成功数',
                            type:'line',
                            stack: '总量',
                            areaStyle: {normal: {}},
                            data:succData
                        },
                        {
                            name:'失败数',
                            type:'line',
                            stack: '总量',
                            label: {
                                normal: {
                                    show: true,
                                    position: 'top'
                                }
                            },
                            areaStyle: {normal: {}},
                            data:failData
                        }
                    ]
                };
            }else{
                option = {
                    xAxis : [{data : []}],
                    series : []
                };
            }
            lineChart.hideLoading();
            lineChart.setOption(option);
        }
    });
}

//获取addDayCount天后的日期
function getDateStr(addDayCount){
    var dd = new Date();
    dd.setDate(dd.getDate()+addDayCount);
    var y = dd.getFullYear();
    var m = (dd.getMonth()+1)<10?"0"+(dd.getMonth()+1):(dd.getMonth()+1);//获取当前月份的日期，不足10补0
    var d = dd.getDate()<10?"0"+dd.getDate():dd.getDate(); //获取当前几号，不足10补0
    return y+"-"+m+"-"+d;
}