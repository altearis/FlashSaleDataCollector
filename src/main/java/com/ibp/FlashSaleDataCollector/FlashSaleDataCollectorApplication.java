package com.ibp.FlashSaleDataCollector;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FlashSaleDataCollectorApplication {

	public static void main(String[] args) {
		SpringApplication.run(FlashSaleDataCollectorApplication.class, args);		
	}

}
