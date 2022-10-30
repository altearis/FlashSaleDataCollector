package com.ibp.FlashSaleDataCollector.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.*;

import org.hibernate.annotations.Type;

import lombok.Data;

@Data
@Entity
@Table(name = "products")
public class Products implements Serializable{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7645994926018730115L;
	@Id
	private long productId;
	@Temporal(TemporalType.TIMESTAMP)
	private Date insertTime;
	private String productName;
	@Column(name = "productUrl", length = 65535, columnDefinition="TEXT")
	@Type(type="text")
	private String productUrl;
	private String productImage;
	private int productCategory;
	private BigDecimal productPrice;
	private String productSource;
	
	 @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = false)
	    private List<TransactionHistory> transactions = new ArrayList<TransactionHistory>();
		 
	 public Products(){};
	public Products(long productId, String productName, String productUrl,
			String productImage, int productCategory, BigDecimal productPrice, String productSource,
			List<TransactionHistory> transactions) {
		super();
		this.productId = productId;
		this.insertTime = new Date();
		this.productName = productName;
		this.productUrl = productUrl;
		this.productImage = productImage;
		this.productCategory = productCategory;
		this.productPrice = productPrice;
		this.productSource = productSource;
		this.transactions = transactions;
	}
	public Products(long productId, String productName, String productUrl,
			String productImage, int productCategory, BigDecimal productPrice, String productSource) {
		super();
		this.productId = productId;
		this.insertTime = new Date();
		this.productName = productName;
		this.productUrl = productUrl;
		this.productImage = productImage;
		this.productCategory = productCategory;
		this.productPrice = productPrice;
		this.productSource = productSource;
	}
}
