package com.gj.billchange.dto;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class InsertCoinRequest {
	
	@JsonProperty(value="coinValue", required = true)
    @NotNull(message="Please provide a valid coinValue")
	private Double coinValue;
	@JsonProperty(value="quantity", required = true)
    @NotNull(message="Please provide valid quantity")
	private Integer quantity;
	
}
