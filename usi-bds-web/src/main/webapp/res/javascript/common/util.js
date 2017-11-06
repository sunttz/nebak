String.prototype.replaceAll = function(s1,s2) {
    return this.replace(new RegExp(s1,"gm"),s2);
};

String.prototype.endWith=function(s){
	if(s==null||s==""||this.length==0||s.length>this.length)
		return false;
	if(this.substring(this.length-s.length)==s)
		return true;
	else
		return false;
	return true;
};

String.prototype.startWith=function(s){
	if(s==null||s==""||this.length==0||s.length>this.length)
		return false;
	if(this.substr(0,s.length)==s)
		return true;
	else
		return false;
	return true;
};

Date.prototype.format = function(format) {
	 var o = {
			"M+" : this.getMonth()+1, //month
	 		"d+" : this.getDate(),    //day
	 		"h+" : this.getHours(),   //hour
	 		"m+" : this.getMinutes(), //minute
	 		"s+" : this.getSeconds(), //second
	 		"q+" : Math.floor((this.getMonth()+3)/3),  //quarter
	 		"S" : this.getMilliseconds() //millisecond
	 	}
	 if(/(y+)/.test(format)) {
	 	format=format.replace(RegExp.$1, (this.getFullYear()+"").substr(4 - RegExp.$1.length));
	 }
	 for(var k in o){
		 if(new RegExp("("+ k +")").test(format)){
		 	format = format.replace(RegExp.$1, RegExp.$1.length==1 ? o[k] : ("00"+ o[k]).substr((""+ o[k]).length));
		 }
	 }
	 return format;
}

/**
 * 将格式化后的日期字符串转换回日期
 * @param {} time	格式化后的字符串
 * @param {} format	(yyyy MM dd HH mm ss)
 */
function getDateFromString(time, format){
	var y_index = format.indexOf('yyyy');
	var M_index = format.indexOf('MM');
	var d_index = format.indexOf('dd');
	var H_index = format.indexOf('HH');
	var m_index = format.indexOf('mm');
	var s_index = format.indexOf('ss');
	
	var y = y_index > -1 ? parseInt(time.substr(y_index, 4),10) : 0;
	var M = M_index > -1 ? parseInt(time.substr(M_index, 2),10) - 1 : 0;
	var d = d_index > -1 ? parseInt(time.substr(d_index, 2),10) : 0;
	var H = H_index > -1 ? parseInt(time.substr(H_index, 2),10) : 0;
	var m = m_index > -1 ? parseInt(time.substr(m_index, 2),10) : 0;
	var s = s_index > -1 ? parseInt(time.substr(s_index, 2),10) : 0;
	
	return new Date(y,M,d,H,m,s);
}

/**
 * 获取上一个月
 *
 * @date 格式为yyyy-mm-dd的日期，如：2014-01-25
 */
function getPreMonth(date) {
    var arr = date.split('-');
    var year = arr[0]; //获取当前日期的年份
    var month = arr[1]; //获取当前日期的月份
    var day = arr[2]; //获取当前日期的日
    var days = new Date(year, month, 0);
    days = days.getDate(); //获取当前日期中月的天数
    var year2 = year;
    var month2 = parseInt(month,10) - 1;
    if (month2 == 0) {
        year2 = parseInt(year2) - 1;
        month2 = 12;
    }
    var day2 = day;
    var days2 = new Date(year2, month2, 0);
    days2 = days2.getDate();
    if (day2 > days2) {
        day2 = days2;
    }
    if (month2 < 10) {
        month2 = '0' + month2;
    }
    var t2 = year2 + '-' + month2 + '-' + day2;
    return t2;
}
        
/**
 * 获取下一个月
 *
 * @date 格式为yyyy-mm-dd的日期，如：2014-01-25
 */        
function getNextMonth(date) {
    var arr = date.split('-');
    var year = arr[0]; //获取当前日期的年份
    var month = arr[1]; //获取当前日期的月份
    var day = arr[2]; //获取当前日期的日
    var days = new Date(year, month, 0);
    days = days.getDate(); //获取当前日期中的月的天数
    var year2 = year;
    var month2 = parseInt(month,10) + 1;
    if (month2 == 13) {
        year2 = parseInt(year2) + 1;
        month2 = 1;
    }
    var day2 = day;
    var days2 = new Date(year2, month2, 0);
    days2 = days2.getDate();
    if (day2 > days2) {
        day2 = days2;
    }
    if (month2 < 10) {
        month2 = '0' + month2;
    }

    var t2 = year2 + '-' + month2 + '-' + day2;
    return t2;
}
