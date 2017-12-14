package usi.biz.service;

import org.springframework.stereotype.Service;
import usi.biz.dao.BakResultDao;
import usi.biz.entity.BakResult;

import javax.annotation.Resource;
import java.util.List;

@Service
public class BakResultService {
    @Resource
    BakResultDao bakResultDao;

    /**
     * 查询指定天备份结果
     * @param createDate
     * @return
     */
    public BakResult queryByTime(String createDate){
        return bakResultDao.queryByTime(createDate);
    }

    /**
     * 查询指定时间段备份结果
     * @param startDate
     * @param endDate
     * @return
     */
    public List<BakResult> queryBakResult(String startDate, String endDate){
        return bakResultDao.queryBakResult(startDate,endDate);
    }
}
