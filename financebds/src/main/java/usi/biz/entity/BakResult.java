package usi.biz.entity;

import java.util.Date;

public class BakResult {
    private Long pkNo; // ID
    private Long succNum; // 成功数
    private Long failNum; // 失败数
    private Date createDate; // 生成日期

    public Long getPkNo() {
        return pkNo;
    }

    public void setPkNo(Long pkNo) {
        this.pkNo = pkNo;
    }

    public Long getSuccNum() {
        return succNum;
    }

    public void setSuccNum(Long succNum) {
        this.succNum = succNum;
    }

    public Long getFailNum() {
        return failNum;
    }

    public void setFailNum(Long failNum) {
        this.failNum = failNum;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }
}
