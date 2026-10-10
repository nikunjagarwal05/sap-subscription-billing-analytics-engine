package projectsap.sap.client;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import projectsap.sap.bill.client.SapApiClient;
import projectsap.sap.common.exception.SapApiException;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("SapApiClient Unit Tests (Apache HttpClient 5 & Fallbacks)")
public class SapApiClientTest {

    @Test
    @DisplayName("Test fetchBillsFromSAP returns valid JSON payload")
    void testFetchBillsReturnsValidJson() {
        assertDoesNotThrow(() -> {
            String json = SapApiClient.fetchBillsFromSAP();
            assertNotNull(json, "Response JSON must not be null");
            assertFalse(json.trim().isEmpty(), "Response JSON must not be empty");
            assertTrue(json.contains("bills") || json.contains("id"), "Payload must contain billing records");
        });
    }

    @Test
    @DisplayName("Test getFallbackFixture for all 5 SAP services")
    void testFallbackFixturesForAllServices() {
        String billsJson = SapApiClient.getFallbackFixture("https://example.com/api/bill/v2/bills");
        assertNotNull(billsJson);
        assertTrue(billsJson.contains("bills"));

        String subsJson = SapApiClient.getFallbackFixture("https://example.com/api/subscription/v1/subscriptions");
        assertNotNull(subsJson);
        assertTrue(subsJson.contains("subscriptions"));

        String prodsJson = SapApiClient.getFallbackFixture("https://example.com/api/product/v1/products");
        assertNotNull(prodsJson);
        assertTrue(prodsJson.contains("items") || prodsJson.contains("products"));

        String rpJson = SapApiClient.getFallbackFixture("https://example.com/api/rate-plan/v1/rate-plans");
        assertNotNull(rpJson);
        assertTrue(rpJson.contains("items") || rpJson.contains("rate"));

        String usageJson = SapApiClient.getFallbackFixture("https://example.com/api/usage-records/v1/usage-records");
        assertNotNull(usageJson);
        assertTrue(usageJson.contains("items") || usageJson.contains("usage"));
    }

    @Test
    @DisplayName("Test exception thrown when fallback is disabled on invalid URL")
    void testExceptionThrownWhenFallbackDisabled() {
        assertThrows(SapApiException.class, () -> {
            SapApiClient.fetchFromUrl("https://invalid-non-existent-domain-12345.com/api", false);
        });
    }

    @Test
    @DisplayName("Test fallback succeeds when fallback is enabled on invalid URL")
    void testFallbackSucceedsOnInvalidUrl() {
        assertDoesNotThrow(() -> {
            String json = SapApiClient.fetchFromUrl("https://invalid-non-existent-domain-12345.com/api/subscription", true);
            assertNotNull(json);
            assertTrue(json.contains("subscriptions"));
        });
    }
}
