package com.ibp.FlashSaleDataCollector.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

import lombok.Data;

@Data
@Entity
@Table(name = "promotion_category")
public class PromotionCategory {
	@Id
	private Long promotionId;
	private String promotionName;
	private Long promotionStart;
	private Long promotionEnd;
	
	@OneToMany(mappedBy = "promcat", cascade = CascadeType.ALL, orphanRemoval = false)
    private List<TransactionHistory> transHis = new ArrayList<TransactionHistory>();
	 
}
