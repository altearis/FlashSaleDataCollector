package com.ibp.FlashSaleDataCollector.entity;

import java.util.List;

import lombok.Data;

@Data
public class GlobalVariable {
	public static List<Websource> allsource;
	public static List<UserAgent> useragent;

	public static Boolean jobRun = false;
}
