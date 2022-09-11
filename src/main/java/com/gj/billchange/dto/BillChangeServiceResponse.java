package com.gj.billchange.dto;

import java.util.Map;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class BillChangeServiceResponse {
	
	private Map<Double, Integer> change;
	private String error;
	private String message;
	
}
