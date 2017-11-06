package usi.biz.dao;

import java.util.List;

import usi.biz.entity.Attachment;
import usi.sys.dto.PageObj;

public interface UploadDao {
	
	public long saveUpload(Attachment atta);

	public List<Attachment> downloadList(PageObj pageObj,String realName,String startTime,String endTime);
}
