package com.ibp.FlashSaleDataCollector.model;

import java.util.List;

import lombok.Data;

@Data
public class ShopeeItemOnSale {
	
	    private int category;
	    private Long item;
	    private List<Long> itemLists;
	    public ShopeeItemOnSale(){}
		public ShopeeItemOnSale(int category, Long item) {
			super();
			this.category = category;
			this.item = item;
		}
		public ShopeeItemOnSale(int category,List<Long> itemLists) {
			super();
			this.category = category;
			this.itemLists = itemLists;
		}

}
