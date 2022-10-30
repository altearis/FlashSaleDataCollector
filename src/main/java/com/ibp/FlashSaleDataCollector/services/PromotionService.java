package com.ibp.FlashSaleDataCollector.services;

import java.util.List;

import com.ibp.FlashSaleDataCollector.entity.PromotionCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ibp.FlashSaleDataCollector.repo.PromotionCategoryRepo;

@Service
public class PromotionService {

	@Autowired
	PromotionCategoryRepo promRepo;
	
	public void saveData(List<PromotionCategory> prm)
	{
		promRepo.saveAll(prm);
	}
}
