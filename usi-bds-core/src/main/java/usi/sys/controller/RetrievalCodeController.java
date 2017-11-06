package usi.sys.controller;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import usi.sys.dto.RetrievalCode;
import usi.sys.service.RetrievalCodeService;

/**
 * 
 * @author lmwang
 * 创建时间：2014-4-16 上午9:20:34
 */
@Controller
@RequestMapping("/RetrievalCodeController")
public class RetrievalCodeController {
	
	@Resource
	private RetrievalCodeService retrievalCodeService;
	
	@RequestMapping(value = "/main.do",method =RequestMethod.GET)
	public String getMain(){
		return "system/retrievalCode";
	}
	
	@RequestMapping(value = "/codeData.do",method =RequestMethod.GET)
	@ResponseBody
	public List<RetrievalCode> getCodeData(String q){
		return retrievalCodeService.getNameIndexCode(q);
	}
}
