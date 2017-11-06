package usi.biz.controller;


import java.util.List;

import javax.annotation.Resource;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import usi.biz.entity.NeServer;
import usi.biz.service.NeServerService;


@Component
public class TimerController {
	
	@Resource 
	private NeServerService neServerService;
	/**
	 * 定时启动。每天执行一次
     */
    @Scheduled(fixedDelay=1000*60*60*12)
    public void show() {
    	//先删除日志，再进行备份
    	neServerService.deleteAutoLogByTime();
    	
    	List<NeServer> list=neServerService.getAllNE();
    	String ids="";
    	for(NeServer neServer:list){
    		ids+=neServer.getServerId()+",";
    	}
    	if(!ids.equals("")){
    		ids=ids.substring(0, ids.length()-1);
    		neServerService.autoBakNow(ids);
    	}
    }
}
