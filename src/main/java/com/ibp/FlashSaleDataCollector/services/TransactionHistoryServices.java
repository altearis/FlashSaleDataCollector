package com.ibp.FlashSaleDataCollector.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.ibp.FlashSaleDataCollector.entity.TransactionHistory;
import com.ibp.FlashSaleDataCollector.util.AbstractManagedBean;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ibp.FlashSaleDataCollector.repo.TransactionHistoryRepo;

@Service
public class TransactionHistoryServices extends AbstractManagedBean {

	@Autowired
	TransactionHistoryRepo trxRepo;
	
	public List<Map<String,Object>> getSoldOutProduct(){
		
		List<TransactionHistory> result = trxRepo.findAllByDiscountPriceGreaterThanAndIsSoldOutTrue(BigDecimal.ZERO);
		List<Map<String,Object>> returnVa = new ArrayList<Map<String,Object>>() ;
		List<Map<String,Object>> returnVa2 = new ArrayList<Map<String,Object>>() ;
		if(result.size()>0)
		{
			String name = "";
			for(TransactionHistory a:result){
				Map<String,Object> tmp = new HashMap<String, Object>();
				tmp.put("productName", a.getProduct().getProductName());
				tmp.put("productUrl", a.getProduct().getProductUrl());
				tmp.put("productImage", a.getProduct().getProductImage());
				tmp.put("productPrice", a.getPrice());
				tmp.put("productDiscountPrice", a.getDiscountPrice());				
				tmp.put("productSellOutStock", a.getFlashSaleStock());
				tmp.put("productDiscountRate", a.getDiscountRate());
				returnVa.add(tmp);
				name = a.getPromcat().getPromotionName();
			}
			Map<String,Object> tmp2 = new HashMap<String, Object>();
			tmp2.put("promotionName",name);
			returnVa2.add(tmp2);
			returnVa2.addAll(returnVa);
			
			
		}
		
		
		return returnVa2;
	}
	public List<Map<String,Object>> getDiscountRate(LocalDate searchDate){
		List<Map<String,Object>> data = trxRepo.getSoldoutDiscountRate(searchDate);
		if(data.size()>0)
			return data.stream().limit(5).collect(Collectors.toList());
		else
			return null;
	}
	
	
}
