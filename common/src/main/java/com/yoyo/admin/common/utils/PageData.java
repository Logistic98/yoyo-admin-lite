package com.yoyo.admin.common.utils;

import org.springframework.data.domain.Page;

import java.io.Serializable;
import java.util.List;

/**
 * 结果分页封装
 * @param <T>
 */
public class PageData<T> implements Serializable {

    private List<T> data;
    private Integer page;
    private Integer pageSize;
    private Long dataCount;
    private Integer pageCount;

    public PageData(List<T> data, Integer page, Integer pageSize, Long dataCount) {
        this.data = data;
        this.page = page;
        this.pageSize = pageSize;
        this.dataCount = dataCount;
        if (pageSize != null && !pageSize.equals(0)) {
            Double t = Math.ceil(dataCount.doubleValue() / pageSize);
            this.pageCount = t.intValue();
        } else {
            this.pageCount = 0;
        }
    }

    public PageData(Page<T> domainPage, Integer page, Integer pageSize) {
        this.data = domainPage.getContent();
        this.pageCount = domainPage.getTotalPages();
        this.dataCount = domainPage.getTotalElements();
        this.page = page;
        this.pageSize = pageSize;
    }

    public PageData(Page<T> domainPage) {
        this.data = domainPage.getContent();
        this.pageCount = domainPage.getTotalPages();
        this.dataCount = domainPage.getTotalElements();
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Long getDataCount() {
        return dataCount;
    }

    public void setDataCount(Long dataCount) {
        this.dataCount = dataCount;
    }

    public Integer getPageCount() {
        return pageCount;
    }

    public void setPageCount(Integer pageCount) {
        this.pageCount = pageCount;
    }
}
