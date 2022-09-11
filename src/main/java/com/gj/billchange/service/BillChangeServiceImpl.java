package com.gj.billchange.service;


import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gj.billchange.config.CoinStoreConfig;
import com.gj.billchange.exception.ChangeNotFoundException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class BillChangeServiceImpl implements BillChangeService{
	
	@Autowired
	CoinStoreConfig coinStoreConfig;
	
	@Override
	public Map<Double, Integer> calculateChange(int bill, String type) {
		log.info("calculateChange() invoked with bill = {} and type = {}", bill);
		Map<Double, Integer> resultChangeMap = new TreeMap<>();
		
		String sortType = StringUtils.equals(type, "MOST") ? "ASC" : "DESC";
		Map<Double, Integer> coinStore = coinStoreConfig.getCoinStore(sortType);
		log.info("calculateChange() coinStore init: {}", coinStore);

		double pendingBillAmt = bill;
		for (Entry<Double, Integer> entrySet: coinStore.entrySet()) {
			if (pendingBillAmt <= 0)
				break;
			double coinVal = entrySet.getKey();
			int coinQty = entrySet.getValue();
			int no = getSingleChangeQty(pendingBillAmt, coinVal, coinQty);
			if (no != -1) {
				pendingBillAmt -= (coinVal * no);
				pendingBillAmt = Math.round(pendingBillAmt * 100.0) / 100.0;
				resultChangeMap.put(coinVal, no);
			}
			log.info("pendingBillAmt = {}", pendingBillAmt);
		}
		
		if (pendingBillAmt > 0) {
			log.info("calculateChange() completed but Change can not be made for bill {}.", bill);
			throw new ChangeNotFoundException(String.format("Change Not Found for %d", bill));
		}else {
			log.info("calculateChange() completed with resultChangeMap = {}", resultChangeMap);
			coinStoreConfig.updateCoinStore(resultChangeMap);
			return resultChangeMap;
		}
	}

	private int getSingleChangeQty(double bill, Double coinVal, Integer coinQty) {
		log.info("getSingleChangeQty() bill = {}, coinVal = {}, coinQty = {}", bill, coinVal, coinQty);

		int result = -1;
		if (coinQty > 0) {
			result = Math.min((int)(bill * (1 / coinVal)), coinQty);
		}
		log.info("getSingleChangeQty() result = {}", result);

		return result;
	}
 
}
