package com.tuanjian.common.utils;

import java.io.Serializable;
import lombok.EqualsAndHashCode;

/**
 * 分页对象
 */
@EqualsAndHashCode
public class Page implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final int DEFAULT_PAGE_SIZE = 10;

    /**
     * 页码，从1开始
     */
    private int pageNum;
    /**
     * 页面大小：默认10
     */
    private int pageSize;

    public Page() {
    }

    public Page(int pageNum, int pageSize) {
        this.pageNum(pageNum).pageSize(pageSize);
    }

    public void setPageNum(int pageNum) {
        this.pageNum(pageNum);
    }

    public int getPageNum() {
        this.pageNum(this.pageNum);
        return this.pageNum;
    }

    public void setPageSize(int pageSize) {
        this.pageSize(pageSize);
    }

    public int getPageSize() {
        this.pageSize(this.pageSize);
        return this.pageSize;
    }

    public Page pageNum(int pageNum) {
        this.pageNum = (pageNum < 1) ? 1 : pageNum;
        return this;
    }

    public Page pageSize(int pageSize) {
        this.pageSize = (pageSize < 1) ? DEFAULT_PAGE_SIZE : pageSize;
        return this;
    }

    public Page incrPageNum() {
        return changePageNum(1);
    }

    public Page decrPageNum() {
        return changePageNum(-1);
    }

    public Page changePageNum(int delta) {
        this.pageNum += delta;
        return this;
    }

}
