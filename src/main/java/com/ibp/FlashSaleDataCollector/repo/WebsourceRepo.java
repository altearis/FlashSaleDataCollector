package com.ibp.FlashSaleDataCollector.repo;

import com.ibp.FlashSaleDataCollector.entity.Websource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WebsourceRepo extends JpaRepository<Websource, Long> {

}
