package usi.biz.util.excelUtil;

import java.util.List;
import java.util.Map;

/**
 * 定义表格的数据对象
 */
public class ExcelSheetPO {

    /**
     * sheet的名称
     */
    private String sheetName;

    /**
     * 表格标题
     */
    private String title;

    /**
     * 头部标题集合
     */
    private String[] headers;

    /**
     * 数据集合
     */
    private List<List<Object>> dataList;

    /**
     * 下拉菜单列表集合
     */
    private Map<String, String[]> paraMap;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String[] getHeaders() {
        return headers;
    }

    public void setHeaders(String[] headers) {
        this.headers = headers;
    }

    public List<List<Object>> getDataList() {
        return dataList;
    }

    public void setDataList(List<List<Object>> dataList) {
        this.dataList = dataList;
    }

    public String getSheetName() {
        return sheetName;
    }

    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }

    public Map<String, String[]> getParaMap() {
        return paraMap;
    }

    public void setParaMap(Map<String, String[]> paraMap) {
        this.paraMap = paraMap;
    }
}