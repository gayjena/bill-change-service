package com.gj.billchange;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "Bill Change API", version = "1.0", description = "Calculates change for a given bill"))
public class BillChangeServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(BillChangeServiceApplication.class, args);
	}

}
