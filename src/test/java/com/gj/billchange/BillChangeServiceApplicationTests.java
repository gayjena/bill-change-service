package com.gj.billchange;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.gj.billchange.config.CoinStoreConfig;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
class BillChangeServiceApplicationTests {

	@Autowired
	CoinStoreConfig config;
	
	@Test
	void contextLoads() {
		Map<Double, Integer> coinStoreMap = config.getCoinStore("DESC");
		assertFalse(coinStoreMap.isEmpty());
	}

}
