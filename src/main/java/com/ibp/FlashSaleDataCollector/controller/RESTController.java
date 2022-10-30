package com.ibp.FlashSaleDataCollector.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;

import com.ibp.FlashSaleDataCollector.entity.GlobalVariable;
import com.ibp.FlashSaleDataCollector.services.ProductServices;
import com.ibp.FlashSaleDataCollector.services.TransactionHistoryServices;
import com.ibp.FlashSaleDataCollector.util.AbstractManagedBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ibp.FlashSaleDataCollector.services.SchedullerServices;

@RestController
@RequestMapping("/collector")
public class RESTController extends AbstractManagedBean {
		
	@Autowired
	SchedullerServices scheduler;
	
	@Autowired
    TransactionHistoryServices trxSvc;
	
	@Autowired
    ProductServices prdSvc;
    
    
	
	@RequestMapping(value="/getShopee", method=RequestMethod.GET)
	public ResponseEntity<String> CallShopeeGrabber(){
		if(GlobalVariable.jobRun){
			return ResponseEntity.ok("Job Already Run");
		}else {
			scheduler.runJobShopee();
			return ResponseEntity.ok("Job Run");
		}
	}
	@RequestMapping(value="/soldout", method=RequestMethod.GET)
	public ResponseEntity<Object> GetSoldOutItem(){
		List<Map<String,Object>> his=trxSvc.getSoldOutProduct();
		 		
		return response(HttpStatus.OK, null, his, "00","Retriving "+his.size()+" data");
	}

	@RequestMapping(value = "/getDiscountRate/{tanggal}", method = RequestMethod.GET)
	public ResponseEntity<Object> getDiscount(@PathVariable() String tanggal){
		try {
			LocalDate curDate = LocalDate.parse(tanggal, DateTimeFormatter.ofPattern("yyyyMMdd"));
			List<Map<String, Object>> listData = trxSvc.getDiscountRate(curDate);
			return response(HttpStatus.OK, null, listData, "00", "Retriving " + listData.size() + " data");
		} catch (DateTimeParseException dte){
			return response(HttpStatus.BAD_REQUEST, null, null, "66", "Invalid Date.");
		} catch (NullPointerException npe){
			return response(HttpStatus.OK, null, null, "00", "No Result found.");
		}
	}
	
	
	}
