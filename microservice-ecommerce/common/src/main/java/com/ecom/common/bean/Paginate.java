package com.ecom.common.bean;

import lombok.Data;

import java.io.Serializable;

@Data
public class Paginate implements Serializable {
    private int total;
	private int limit;
	private int page;
	
	public Paginate() {
		this.total = 0;
		this.limit = 0;
		this.page = 0;
	}
}
