package usi.sys.dto;

/**
 * 分页参数的载体
 * @author fan.fan
 * @date 2014-3-27 下午1:44:58
 */
public class PageObj {
	
	//每一页的行数
	private int rows;
	
	//当前页
	private int page;
	
	//总记录数
	private int total;
	
	public int getRows() {
		return rows;
	}

	public void setRows(int rows) {
		this.rows = rows;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}
}
