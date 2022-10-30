package com.ibp.FlashSaleDataCollector.repo;

import com.ibp.FlashSaleDataCollector.entity.Products;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepo extends JpaRepository<Products, Long>{

	
}
