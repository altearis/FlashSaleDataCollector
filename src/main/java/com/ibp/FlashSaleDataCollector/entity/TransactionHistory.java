package com.ibp.FlashSaleDataCollector.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.*;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import lombok.Data;

@Data
@Entity
@Table(name ="transaction_history")
public class TransactionHistory implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3392928037188363346L;
	@Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
	private String historyId;
	private long prod_Id;	
	@Temporal(TemporalType.TIMESTAMP)
	private Date insertTime;
	private long promotionalId;
	private BigDecimal price;
	private BigDecimal discountPrice;
	private Boolean isSoldOut;
	private String discountRate;
	private int currentStock;
	private int flashSaleStock;

	@ManyToOne(targetEntity = Products.class)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "prod_Id", referencedColumnName = "productId", insertable = false, updatable = false)
	private Products product;
	
	
	@ManyToOne(targetEntity = PromotionCategory.class)
    @JoinColumn(name = "promotionalId", referencedColumnName = "promotionId", insertable = false, updatable = false)	
	private PromotionCategory promcat;
	
	
	public TransactionHistory(){}
	public TransactionHistory(String historyId, long prod_Id,Long promotionalId, BigDecimal price, BigDecimal discountPrice,
			Boolean isSoldOut,String discountRate,int currentStock, int flashSaleStock) {
		super();
		this.historyId = historyId;
		this.prod_Id = prod_Id;
		this.promotionalId = promotionalId;
		this.insertTime = new Date();
		this.price = price;
		this.discountPrice = discountPrice;
		this.isSoldOut = isSoldOut;
		this.discountRate = discountRate;
		this.currentStock=currentStock;
		this.flashSaleStock=flashSaleStock;
	}
}
