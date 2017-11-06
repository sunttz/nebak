package usi.sys.dao;

import java.util.List;

import usi.sys.dto.RetrievalCode;

/**
 * 
 * @author lmwang
 * 创建时间：2014-4-16 上午11:05:57
 */
public interface RetrievalCodeDao {

	public List<RetrievalCode> getNameIndexCode(String q);
}
