# bill-change-service

### API docs
Check swagger UI for details http://localhost:8080/swagger-ui/index.html

### Testing
Integration test suite is created using plain SpringBootTest which parses CSV test data file with below format.
bill|type|change|error
--- | --- | --- | --- 
10|LEAST|{0.1=50, 0.25=20}|null
Test calls api to get change for given bill 10 and matches the actual api result with the expected results from column change and error.
Insert|coinValue|quantity
--- | --- | ---
Insert|0.25|18
Test calls api to update the coin store by adding the new quantity.

### TO DO: (Potential Improvements)
 * addressing uncommon scenarios
 * cache implementation for storing all change combination and updating it every time coin store changes, so that optimal result can be fetched.
 * DB/central cache implementation for distributed cloud support.
