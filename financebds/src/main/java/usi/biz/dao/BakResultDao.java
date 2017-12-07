package usi.biz.dao;

import usi.biz.entity.BakResult;

import java.util.List;

public interface BakResultDao {
    /**
     * 查询指定天备份结果
     * @param createDate
     * @return
     */
    BakResult queryByTime(String createDate);

    /**
     * 查询指定时间段备份结果
     * @param startDate
     * @param endDate
     * @return
     */
    List<BakResult> queryBakResult(String startDate, String endDate);

    /**
     * 保存备份结果
     * @param bakResult
     * @return
     */
    int saveBakResult(BakResult bakResult);

    /**
     * 删除当天备份结果
     * @return
     */
    int deleteBakResultByTime();
}
