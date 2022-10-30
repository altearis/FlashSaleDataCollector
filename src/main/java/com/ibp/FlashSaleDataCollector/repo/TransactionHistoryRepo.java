package com.ibp.FlashSaleDataCollector.repo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import com.ibp.FlashSaleDataCollector.entity.TransactionHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionHistoryRepo extends JpaRepository<TransactionHistory, Long>{

	List<TransactionHistory> findAllByDiscountPriceGreaterThanAndIsSoldOutTrue(BigDecimal i);

	@Query("SELECT u FROM TransactionHistory u where isSoldOut=1 and discountPrice>0")
	List<TransactionHistory> findAllByIsSoldOutTrue();

	@Query(value = "SELECT promotionName,discountRate,count(*) totalSoldout FROM transaction_history T " +
			"INNER JOIN promotion_category C on T.promotionalId = C.promotionId and FROM_UNIXTIME(C.promotionStart) >= CURRENT_DATE()" +
			"where isSoldOut = 1 and DATE(FROM_UNIXTIME(promotionstart)) = :searchDate " +
			"group by discountRate,promotionName " +
			"ORDER BY totalSoldout desc",nativeQuery = true)
	List<Map<String,Object>> getSoldoutDiscountRate(@Param("searchDate") LocalDate searchDate);

}
