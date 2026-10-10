# Final Year Project Incharge Review Manual
## Compliance & Implementation Report (Post-October 19th Review)

---

### Executive Compliance Matrix

| Requirement | Description | Status | Implementation Details |
|---|---|:---:|---|
| **1. Project Setup** | Configurations, properties, and build environment | **COMPLETE** | Configured `application.properties`, Maven 3.9+, Java 21 LTS, clean dependency tree. |
| **2. JAR Creation** | Standalone packaging excluding test classes | **COMPLETE** | Created `build_jar.bat` producing runnable Fat JAR (`target/sap-bill-crawler-1.0.0-jar-with-dependencies.jar`). Tests strictly excluded. |
| **3. Unit Testing** | JUnit 5 & Mockito test suite for all components | **COMPLETE** | **25/25 Tests Passing (100%)**. Covers success, edge cases, failure states, and custom exceptions. |
| **4. Main Class for Testing** | Real-time test harness with sample inputs | **COMPLETE** | Implemented `RealtimeApiTester.java` and `test_all_apis.bat` testing all 5 APIs live. |
| **5. API Integration** | Integration of 4–5 meaningful SAP APIs | **COMPLETE** | **5 Full APIs Integrated**: Bills, Subscriptions, Products, Rate Plans, Usage Records. |
| **6. AI/ML Analytics** | Machine Learning forecasting & frontend charts | **COMPLETE** | Built `AiPredictor.java` with Ordinary Least Squares (OLS) Linear Regression, confidence bands & Churn scoring. |
| **7. Industry Standards** | Clean code, security, logging, error handling | **COMPLETE** | SLF4J logging, custom `SapApiException`/`SapParseException`, ANSI SQL upsert, parameterized queries. |
| **8. Documentation** | Complete technical guide & build verification | **COMPLETE** | Verified build, test suite, and authored complete architecture manuals. |

---

## 1. Project Setup (`application.properties` & Configuration)

All configuration settings are centralized in `src/main/resources/application.properties` with support for environment variable overrides (`SAP_API_KEY`, `DB_HOST`, `DB_PORT`, etc.):

```properties
# Server
server.port=8080
server.preferred_ports=8080,8085,8081,8082,8888

# SAP API Sandbox Endpoints
sap.api.key=wITmrAEUGLzlYWC2my5UOKunJinBNwe4
sap.api.bills.url=https://sandbox.api.sap.com/subscriptionbilling/api/bill/v2/bills?pageNumber=1&pageSize=40&$count=true
sap.api.subscriptions.url=https://sandbox.api.sap.com/subscriptionbilling/api/subscription/v1/subscriptions?pageSize=40
sap.api.products.url=https://sandbox.api.sap.com/subscriptionbilling/api/product/v1/products?pageSize=40
sap.api.rate_plans.url=https://sandbox.api.sap.com/subscriptionbilling/api/rate-plan/v1/rate-plans?pageSize=40
sap.api.usagerecords.url=https://sandbox.api.sap.com/subscriptionbilling/api/usage-records/v1/usage-records?pageSize=40

# AI/ML Prediction Settings
analytics.prediction.horizon_months=6
analytics.prediction.confidence_interval=0.95
```

---

## 2. JAR Creation & Packaging

### How to Build
Run the automated launcher:
```cmd
.\build_jar.bat
```
Or execute via Maven:
```cmd
mvn clean package -DskipTests
```

### Generated Artifacts
1. **Standard JAR:** `target/sap-bill-crawler-1.0.0.jar`
2. **Runnable Fat JAR:** `target/sap-bill-crawler-1.0.0-jar-with-dependencies.jar`

### Running the Standalone Application
```cmd
java -jar target/sap-bill-crawler-1.0.0-jar-with-dependencies.jar
```
*Note: Test files and test dependencies (`junit-jupiter`, `mockito`) are strictly excluded from the application JAR.*

---

## 3. Unit Testing with JUnit 5 & Mockito

### Execution
Run the full test suite:
```cmd
mvn test
```

### Results Summary
**Total Tests: 25 | Failures: 0 | Errors: 0 | Skipped: 0 (100% Pass Rate)**

| Test Class | Focus / Scenarios Tested | Test Count | Status |
|---|---|:---:|:---:|
| `SapApiClientTest` | HTTP 200 responses, network timeouts, error handling, offline sandbox fixtures | 4 | PASS |
| `GenericSapParserTest` | Top-level arrays, REST wrapper unwrapping, OData envelopes, unknown fields, nulls | 7 | PASS |
| `SapServiceRegistryTest` | Core service lookup, case insensitivity, dynamic `Class.forName` reflection | 5 | PASS |
| `DatabaseManagerTest` | PostgreSQL/H2 fallback, dynamic DDL, table name normalization, stats calculation | 5 | PASS |
| `AiPredictorTest` | OLS regression computation, confidence bounds verification, churn scoring | 3 | PASS |
| `SapAutoServiceManagerTest` | OpenAPI 3.0 schema inference from raw payloads | 1 | PASS |

---

## 4. Main Class for Testing (`RealtimeApiTester.java`)

### Location
`src/main/java/projectsap/sap/test/RealtimeApiTester.java`

### How to Run
```cmd
.\test_all_apis.bat
```

### Output Scorecard
```
================================================================================
                         REAL-TIME API TEST SCORECARD                           
================================================================================
  Service Name           | Status   | POJO Model       | Records | Time     | Details
  ------------------------------------------------------------------------------
  Bills API              | ✓ PASS   | Bill             | 40      | 5203  ms | Synced 40 records to DB
  Subscriptions API      | ✓ PASS   | Subscription     | 11      | 1086  ms | Synced 11 records to DB
  Products API           | ✓ PASS   | Product          | 3       | 523   ms | Synced 3 records to DB
  Rate Plans API         | ✓ PASS   | RatePlan         | 3       | 474   ms | Synced 3 records to DB
  Usage Records API      | ✓ PASS   | Usagerecords     | 3       | 496   ms | Synced 3 records to DB
  ------------------------------------------------------------------------------
  Summary: 5 / 5 APIs verified successfully (100% Operational)
================================================================================
```

---

## 5. The 5 Integrated SAP APIs

1. **Bills API (`bills`):** Invoicing, billing line items, gross/net amounts, payment status.
2. **Subscriptions API (`subscriptions`):** Customer subscription agreements, renewal terms, validity dates.
3. **Products API (`products`):** Billable catalog products, electronic delivery settings, pricing schemes.
4. **Rate Plans API (`rate_plans`):** Recurring billing rates, pricing tiers, charge elements.
5. **Usage Records API (`usagerecords`):** Metered consumption records, volume-based rating data.

---

## 6. AI/ML-Based Predictive Analytics

### Mathematical Approach
The engine implements **Ordinary Least Squares (OLS) Linear Regression**:
$$\hat{y}_t = m \cdot t + c$$

Where:
- $t$ is the discrete monthly billing interval.
- $y$ is the aggregated gross billing revenue for that interval.
- Slope $m = \frac{n \sum(t y) - (\sum t)(\sum y)}{n \sum(t^2) - (\sum t)^2}$
- Intercept $c = \frac{\sum y - m \sum t}{n}$

### Statistical Goodness of Fit ($R^2$)
$$R^2 = 1 - \frac{\sum (y_i - \hat{y}_i)^2}{\sum (y_i - \bar{y})^2}$$

### 95% Confidence Band Synthesis
Future projection uncertainty is modeled as:
$$\text{Margin} = \pm 1.96 \cdot S_e \cdot \sqrt{1 + \frac{1}{n} + \frac{(t - \bar{t})^2}{\sum(t_i - \bar{t})^2}}$$

### Customer Churn Risk Assessment
Calculates a Risk Score $R \in [0, 1]$ based on unpaid bill ratio and credit/refund frequency:
$$\text{Risk} = 0.6 \times \left(\frac{\text{Open Bills}}{\text{Total Bills}}\right) + 0.4 \times \left(\frac{\text{Credit Notes}}{\text{Total Bills}}\right)$$

### Visual Display in Dashboard
- **Chart 1:** Historical revenue (solid line) vs AI forecast (dashed line) with shaded confidence intervals.
- **Chart 2:** Churn Risk breakdown (doughnut chart: Low vs Medium vs High Risk).
- **Chart 3:** Multi-service record distribution across all 5 integrated APIs.

---

## 7. Industry Standards Followed

1. **Clean Code & Architecture:** Strict layered architecture (`client`, `db`, `server`, `common`, `analytics`, `test`).
2. **Logging:** Enterprise logging using SLF4J (`LoggerFactory.getLogger`).
3. **Structured Exceptions:** Custom domain exceptions (`SapApiException`, `SapParseException`) holding HTTP status codes and endpoint metadata.
4. **Security & SQL Injection Prevention:** Zero dynamic string concatenation for query parameters—100% parameterized JDBC `PreparedStatement`.
5. **Graceful Fallbacks:** Connection fallback from PostgreSQL to embedded H2, and network fallback to realistic sandbox fixtures.
