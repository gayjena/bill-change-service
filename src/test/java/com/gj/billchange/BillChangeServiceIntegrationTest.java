package com.gj.billchange;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.FileReader;
import java.net.URL;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;

import com.gj.billchange.dto.BillChangeServiceResponse;
import com.gj.billchange.dto.InsertCoinRequest;
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Slf4j
class BillChangeServiceIntegrationTest {

	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate restTemplate;

	@DisplayName("Test Controller GET /billchange - Integration Test")
	@Test
	void intgTestGetBillChange() throws Exception {
		String lineSeparator = System.getProperty("line.separator");
		int lineNo = 2;
		CSVParser csvParser = new CSVParserBuilder().withSeparator('|').build(); 
		try (CSVReader reader = new CSVReaderBuilder(new FileReader("src/test/resources/testData.csv"))
				.withCSVParser(csvParser) 
				.withSkipLines(2) 
				.build()) {
			String[] lineInArray;
			while ((lineInArray = reader.readNext()) != null) {
				++lineNo;
				if (lineInArray.length == 1 && lineInArray[0].isEmpty()) {
				    continue;
				}else if (StringUtils.equalsIgnoreCase("Insert", lineInArray[0])) {
					InsertCoinRequest request = new InsertCoinRequest();
					request.setCoinValue(Double.parseDouble(lineInArray[1]));
					request.setQuantity(Integer.parseInt(lineInArray[2]));
					log.info("************************************************");
					log.info("                  Insert Coin {} - {}                  {}", request.getCoinValue(), request.getQuantity(), lineSeparator);
					log.info("************************************************");
					BillChangeServiceResponse response = restTemplate.postForEntity(
							new URL("http://localhost:" + port + "/api/coin").toString(), request,
							BillChangeServiceResponse.class).getBody();
					log.info("Insert Status = {} {}", response.getMessage(), lineSeparator);
				}else {
					int bill = Integer.parseInt(lineInArray[0]);
					String type = lineInArray[1];
					String changeMapStr = lineInArray[2];
					String error = lineInArray[3];
					log.info("************************************************");
					log.info("                  Test Step - {}                  {}", lineNo, lineSeparator);
					log.info("************************************************");

					log.info("Input Values [bill = {}, type = {}]", bill, type);
					log.info("Expected Output: [change = {}, error = {}]{}", changeMapStr, error, lineSeparator);
					BillChangeServiceResponse response = restTemplate.getForEntity(
							new URL("http://localhost:" + port + "/api/billchange?bill="+bill+"&type="+type).toString(),
							BillChangeServiceResponse.class).getBody();
					assertTrue(StringUtils.equalsIgnoreCase(changeMapStr, String.valueOf(response.getChange())));
					assertTrue(StringUtils.equals(error, String.valueOf(response.getError())));
					log.info("Test Success{}", lineSeparator);
				}
			}
		}

	}
}
