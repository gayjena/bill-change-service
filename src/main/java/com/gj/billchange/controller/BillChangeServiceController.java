package com.gj.billchange.controller;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.gj.billchange.config.CoinStoreConfig;
import com.gj.billchange.dto.BillChangeServiceResponse;
import com.gj.billchange.dto.InsertCoinRequest;
import com.gj.billchange.exception.BadRequestException;
import com.gj.billchange.service.BillChangeService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api")
public class BillChangeServiceController {
	
	@Autowired
	BillChangeService billChangeService;
	
	@Autowired
	CoinStoreConfig coinStoreConfig;
	
	@Value("#{'${config.bill.validValues}'.split(',')}")  
	private Set<Integer> validBills;
	
	@Value("#{'${config.change-type.validValues}'.split(',')}")  
	private Set<String> validChangeTypes;
	
	@Value("${config.change-type.defaultValue}")  
	private String defaultChangeType;
	
	
	
	@GetMapping("/billchange")
    @ResponseStatus(HttpStatus.OK)
	public BillChangeServiceResponse getBillChange(@RequestParam Integer bill, @RequestParam Optional<String> type) {
		log.info("getBillChange() - Request recieved with bill = {} and type = {}", bill, type);
		validateChangeInputs(bill, type);
		BillChangeServiceResponse response = new BillChangeServiceResponse();
		Map<Double, Integer> changeMap = billChangeService.calculateChange(bill, type.orElse(defaultChangeType));
		response.setMessage("Change found");
		response.setChange(changeMap);
		return response;
	}
	
	private void validateChangeInputs(Integer bill, Optional<String> type) {
		if (!isValidBill(bill))
			throw new BadRequestException(String.format("Invalid bill given. [bill = %s]", bill));
		if (type.isPresent() && !isValidType(type.get()))
			throw new BadRequestException(String.format("Invalid type requested. [type = %s]", type.get()));

	}

	private boolean isValidBill(Integer bill) {
		log.info("validBills = {}", validBills);
		return validBills.contains(bill);
	}

	private boolean isValidType(String type) {
		//return StringUtils.equalsAnyIgnoreCase(type, "LEAST", "MOST");
		log.info("validChangeTypes = {}, type = {}", validChangeTypes, type);

		return validChangeTypes.contains(type);
	}
	
	@PostMapping("/coin")
    @ResponseStatus(HttpStatus.OK)
	public BillChangeServiceResponse insertCoins(@RequestBody @Valid InsertCoinRequest insertCoinRequest) {
		Double coinValue = insertCoinRequest.getCoinValue();
		Integer qty = insertCoinRequest.getQuantity();
		log.info("insertCoins() - Request recieved with coinValue = {}, coinQty = {}", coinValue, qty);
		validateCoinInputs(coinValue, qty);
		BillChangeServiceResponse response = new BillChangeServiceResponse();
		coinStoreConfig.insertCoins(coinValue, qty);
		response.setMessage("Coins added");
		return response;
	}

	private void validateCoinInputs(Double coinValue, Integer qty) {
		if (!coinStoreConfig.isValidCoin(coinValue))
			throw new BadRequestException(String.format("Invalid coin value given. [coinValue = %s]", coinValue));
		if (qty <= 0)
			throw new BadRequestException(String.format("Invalid coin quantity added. [coinQty = %s]", qty));
		int capacityLeft = coinStoreConfig.getAvailableCapacity(coinValue);
		if (qty > capacityLeft)
			throw new BadRequestException(String.format("Coins not added - Maximum capacity reached. [capacityLeft = %s]", capacityLeft));
	}

}
