package com.ecom.product_service.bean;

import lombok.Data;

@Data
public class Paginate {
    private int total;
	private int limit;
	private int page;
	
	public Paginate() {
		this.total = 0;
		this.limit = 0;
		this.page = 0;
	}
}
