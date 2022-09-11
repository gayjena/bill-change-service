package com.gj.billchange.service;

import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public interface BillChangeService {
	
	public Map<Double, Integer> calculateChange(int bill, String type);

}
