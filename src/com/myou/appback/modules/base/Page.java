package com.myou.appback.modules.base;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;


/**
 * 与具体ORM实现无关的分页参数及查询结果封装.
 * 注意所有序号从1开始.
 *
 * @param <T> Page中记录的类型.
 * 
 */
public class Page<T> implements Serializable{

    private static final long serialVersionUID = 134234256576756313L;
    
    /** 升序 */
	public static final String ASC = "asc";
	public static final String DESC = "desc";

	/** 当前页 */
	protected int pageNo = 1;
	/** 每页显示的条数 */
	protected int pageSize = 10;
	
	/** 排序的条件 */
	protected String orderBy = null;
	
	/** 排序的方向 */
	protected String order = null;
	
	/** 是否自动求总条数 */
	protected boolean autoCount = true;

	/** 返回结果 */
	protected List<T> result = Collections.emptyList();
	
	/** 总条数 */
	protected long totalCount = -1;
	protected int totalPages = -1;

	/**省的合计*/
	protected List<Map<String, Object>> provinceCountList;
	
	/**市的合计*/
	protected List<Map<String, Object>> cityCountList;
	
	/** 区的合计 */
	protected List<Map<String, Object>> regionCountList;
	
	/** 特殊服务的合计 */
	protected Integer arrays;
	
	
	// 构造函数 //
	public Page() {
	}

	public Page(final int pageSize) {
		setPageSize(pageSize);
	}

	public Page(final int pageSize, final boolean autoCount) {
		setPageSize(pageSize);
		setAutoCount(autoCount);
	}


	/**
	 * 获得当前页的页号,序号从1开始,默认为1.
	 */
	public int getPageNo() {
		return pageNo;
	}

	/**
	 * 设置当前页的页号,序号从1开始,低于1时自动调整为1.
	 */
	public void setPageNo(final int pageNo) {
		this.pageNo = pageNo;

		if (pageNo < 1) {
			this.pageNo = 1;
		}
	}

	/**
	 * 获得每页的记录数量,默认为1.
	 */
	public int getPageSize() {
		return pageSize;
	}

	/**
	 * 设置每页的记录数量,低于1时自动调整为1.
	 */
	public void setPageSize(final int pageSize) {
		this.pageSize = pageSize;

		if (pageSize < 1) {
			this.pageSize = 1;
		}
	}

	/**
	 * 根据pageNo和pageSize计算当前页第一条记录在总结果集中的位置,序号从0开始.
	 */
	public int getFirst() {
		return ((pageNo - 1) * pageSize);
	}

	/**
	 * 获得排序字段,无默认值.多个排序字段时用','分隔.
	 */
	public String getOrderBy() {
		return orderBy;
	}

	/**
	 * 设置排序字段,多个排序字段时用','分隔.
	 */
	public void setOrderBy(final String orderBy) {
		this.orderBy = orderBy;
	}

	/**
	 * 是否已设置排序字段,无默认值.
	 */
	public boolean isOrderBySetted() {
		return (StringUtils.isNotBlank(orderBy) && StringUtils.isNotBlank(order));
	}

	/**
	 * 获得排序方向.
	 */
	public String getOrder() {
		return order;
	}

	/**
	 * 设置排序方式向.
	 *
	 * @param order 可选值为desc或asc,多个排序字段时用','分隔.
	 */
	public void setOrder(final String order) {
		//检查order字符串的合法值
		String[] orders = StringUtils.split(StringUtils.lowerCase(order), ',');
		for (String orderStr : orders) {
			if (!StringUtils.equals(DESC, orderStr) && !StringUtils.equals(ASC, orderStr))
				throw new IllegalArgumentException("排序方向" + orderStr + "不是合法值");
		}

		this.order = StringUtils.lowerCase(order);
	}

	/**
	 * 查询对象时是否自动另外执行count查询获取总记录数, 默认为false.
	 */
	public boolean isAutoCount() {
		return autoCount;
	}

	/**
	 * 查询对象时是否自动另外执行count查询获取总记录数.
	 */
	public void setAutoCount(final boolean autoCount) {
		this.autoCount = autoCount;
	}

	// 访问查询结果函数 //

	/**
	 * 取得页内的记录列表.
	 */
	public List<T> getResult() {
		return result;
	}

	public void setResult(final List<T> result) {
		this.result = result;
	}

	/**
	 * 取得总记录数, 默认值为-1.
	 */
	public long getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(final long totalCount) {
		this.totalCount = totalCount;
	}

	/**
	 * 根据pageSize与totalCount计算总页数, 默认值为-1.
	 */
	public int getTotalPages() {
		if (totalCount < 0)
			return totalPages;

	    totalPages = (int) (totalCount / pageSize);
		if (totalCount % pageSize > 0) {
			totalPages++;
		}
		return totalPages;
	}

	/**
	 * 是否还有下一页.
	 */
	public boolean isHasNext() {
		return (pageNo + 1 <= getTotalPages());
	}

	/**
	 * 取得下页的页号, 序号从1开始.
	 * 当前页为尾页时仍返回尾页序号.
	 */
	public int getNextPage() {
		if (isHasNext())
			return pageNo + 1;
		else
			return pageNo;
	}

	/**
	 * 是否还有上一页.
	 */
	public boolean isHasPre() {
		return (pageNo - 1 >= 1);
	}

	/**
	 * 取得上页的页号, 序号从1开始.
	 * 当前页为首页时返回首页序号.
	 */
	public int getPrePage() {
		if (isHasPre())
			return pageNo - 1;
		else
			return pageNo;
	}

    public List<Map<String, Object>> getProvinceCountList() {
        return provinceCountList;
    }

    public void setProvinceCountList(List<Map<String, Object>> provinceCountList) {
        this.provinceCountList = provinceCountList;
    }

    public List<Map<String, Object>> getCityCountList() {
        return cityCountList;
    }

    public void setCityCountList(List<Map<String, Object>> cityCountList) {
        this.cityCountList = cityCountList;
    }

    public List<Map<String, Object>> getRegionCountList() {
        return regionCountList;
    }

    public void setRegionCountList(List<Map<String, Object>> regionCountList) {
        this.regionCountList = regionCountList;
    }

    public Integer getArrays() {
        return arrays;
    }

    public void setArrays(Integer arrays) {
        this.arrays = arrays;
    }
    
    
	
	
	
}
