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
});