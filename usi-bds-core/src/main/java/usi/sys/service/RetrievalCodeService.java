package usi.sys.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import usi.sys.dao.RetrievalCodeDao;
import usi.sys.dto.RetrievalCode;

/**
 * 
 * @author lmwang
 * 创建时间：2014-4-16 上午11:23:55
 */
@Service
public class RetrievalCodeService {

	@Resource
	private RetrievalCodeDao retrievalCodeDao;
	
	public List<RetrievalCode> getNameIndexCode(String q){
		return retrievalCodeDao.getNameIndexCode(q);
	}
}
