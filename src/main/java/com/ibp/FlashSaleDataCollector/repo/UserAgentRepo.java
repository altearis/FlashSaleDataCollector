package com.ibp.FlashSaleDataCollector.repo;

import com.ibp.FlashSaleDataCollector.entity.UserAgent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAgentRepo extends JpaRepository<UserAgent, Long>{

}
