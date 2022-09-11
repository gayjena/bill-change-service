package com.gj.billchange.config;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@ConfigurationProperties("config.coin-store")
@Data
@Slf4j
@Configuration
public class CoinStoreConfig {
	
	private Map<Double, Integer> values;
	private Integer maxCapacity;

	public Map<Double, Integer> getCoinStore(String sortType) {
		if (StringUtils.equals(sortType, "DESC")) {
			Map<Double, Integer> treemap = new TreeMap<>(Collections.reverseOrder());
			treemap.putAll(values);
			return treemap;
		}else {
			Map<Double, Integer> treemap = new TreeMap<>();
			treemap.putAll(values);
			return treemap;
		}
	}

	public void updateCoinStore(Map<Double, Integer> usedCoinMap) {
		usedCoinMap.forEach((coinVal, coinQty) -> values.computeIfPresent(coinVal, (k, v) -> v - coinQty));
		log.info("Coin store updated {}", values);
	}
	
	public void insertCoins(Double coinVal, Integer addQty) {
		int currentQty = values.get(coinVal);
		values.put(coinVal, currentQty + addQty);
		log.info("Coin store updated {}", values);
	}
	
	public boolean isValidCoin(Double coinVal) {
		return values.containsKey(coinVal);
	}
	
	public int getAvailableCapacity(Double coinVal) {
		return maxCapacity - values.get(coinVal);
	}
	
	
	
}


