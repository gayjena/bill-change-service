package com.gj.billchange.controller;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import com.gj.billchange.config.CoinStoreConfig;
import com.gj.billchange.dto.BillChangeServiceResponse;
import com.gj.billchange.exception.BadRequestException;
import com.gj.billchange.exception.ChangeNotFoundException;
import com.gj.billchange.service.BillChangeService;

@SpringBootTest
@ActiveProfiles("test")
class BillChangeServiceControllerTest {

	@Mock
	BillChangeService billChangeService;

	@Mock
	CoinStoreConfig coinStoreConfig;

	@InjectMocks
	BillChangeServiceController billChangeServiceController;

	@BeforeEach
	public void setUp() {
		ReflectionTestUtils.setField(billChangeServiceController, "validBills", mockValidBills());
		ReflectionTestUtils.setField(billChangeServiceController, "validChangeTypes", mockValidChangeTypes());
		ReflectionTestUtils.setField(billChangeServiceController, "defaultChangeType", "LEAST");

	}

	private Set<Integer> mockValidBills() {
		return new HashSet<>(Arrays.asList(1, 2, 5, 10, 20, 50, 100));
	}
	
	private Set<String> mockValidChangeTypes() {
		return new HashSet<>(Arrays.asList("MOST","LEAST"));
	}
	
	@DisplayName("Test Controller GET /billchange - Success")
	@Test
	void testGetBillChange() throws Exception {
		when(billChangeService.calculateChange(Mockito.anyInt(), Mockito.anyString())).thenReturn(mockResultMap());
		BillChangeServiceResponse resp = billChangeServiceController.getBillChange(2, Optional.empty());
		assertFalse(resp.getChange().isEmpty());
		assertNull(resp.getError());
	}

	@DisplayName("Test Controller GET /billchange - No Change Found")

	@Test
	void testGetBillChange_NoChange() {
		when(billChangeService.calculateChange(Mockito.anyInt(), Mockito.anyString()))
				.thenThrow(ChangeNotFoundException.class);
		Optional<String> type = Optional.empty();
		assertThrows(ChangeNotFoundException.class, () -> billChangeServiceController.getBillChange(2, type));
	}

	@DisplayName("Test Controller GET /billchange - Missing Required")

	@Test
	void testGetBillChange_MissingRequired() {
		Optional<String> type = Optional.of("DESC");
		assertThrows(BadRequestException.class, () -> billChangeServiceController.getBillChange(null, type));
	}

	@DisplayName("Test Controller GET /billchange - Invalid bill param")

	@Test
	void testGetBillChange_InvalidBill() {
		Optional<String> type = Optional.of("DESC");
		assertThrows(BadRequestException.class, () -> billChangeServiceController.getBillChange(-1, type));
	}

	@DisplayName("Test Controller GET /billchange - Invalid type param")

	@Test
	void testGetBillChange_InvalidType() {
		Optional<String> type = Optional.of("TEST");
		assertThrows(BadRequestException.class, () -> billChangeServiceController.getBillChange(2, type));
	}

	private Map<Double, Integer> mockResultMap() {
		Map<Double, Integer> sortedMap = new TreeMap<>();
		sortedMap.put(0.25, 100);
		sortedMap.put(0.10, 100);
		sortedMap.put(0.05, 100);
		sortedMap.put(0.01, 100);
		return Collections.synchronizedMap(sortedMap);
	}

}
