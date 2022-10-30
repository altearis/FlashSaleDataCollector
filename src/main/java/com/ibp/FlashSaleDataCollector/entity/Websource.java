package com.ibp.FlashSaleDataCollector.entity;

import javax.persistence.*;

import lombok.Data;

@Data
@Entity
@Table(name = "websource")
public class Websource {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String url;
	private String name;	
	private Long prefix;
	private int sort;
	public Websource(){};
	public Websource(String url, String name, Long prefix,int sort) {
		super();
		this.url = url;
		this.name = name;
		this.prefix = prefix;
		this.sort = sort;
	} 
	

}
