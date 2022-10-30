package com.ibp.FlashSaleDataCollector.entity;

import javax.persistence.*;

import org.hibernate.annotations.Type;

import lombok.Data;

@Data
@Entity
@Table(name = "user_agent")
public class UserAgent {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	@Column(name = "userAgent", length = 65535, columnDefinition="TEXT")
	@Type(type="text")	
	private String userAgent;
	public UserAgent(){}
	public UserAgent(String userAgent) {
		super();
		this.userAgent = userAgent;
	}
}
