package projectsap.sap.db;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import projectsap.sap.bill.db.DatabaseManager;
import projectsap.sap.bill.model.Bill;

import java.sql.Connection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("DatabaseManager Unit Tests (Dual-Engine JDBC & Dynamic DDL)")
public class DatabaseManagerTest {

    @BeforeAll
    static void setup() {
        assertTrue(DatabaseManager.testConnection(), "Database must be reachable (PostgreSQL or H2 fallback)");
        assertDoesNotThrow(DatabaseManager::initDatabase, "Database tables must initialize cleanly");
    }

    @Test
    @DisplayName("Test resolveTableName normalization")
    void testResolveTableName() {
        assertEquals("bills", DatabaseManager.resolveTableName("bill"));
        assertEquals("bills", DatabaseManager.resolveTableName("bills"));
        assertEquals("subscriptions", DatabaseManager.resolveTableName("subscription"));
        assertEquals("products", DatabaseManager.resolveTableName("product"));
        assertEquals("rate_plans", DatabaseManager.resolveTableName("rate_plans"));
        assertEquals("usagerecords", DatabaseManager.resolveTableName("usagerecords"));
    }

    @Test
    @DisplayName("Test snake_case and camelCase utility converters")
    void testCaseConverters() {
        assertEquals("billing_type", DatabaseManager.toSnakeCase("billingType"));
        assertEquals("customer_id", DatabaseManager.toSnakeCase("customerId"));
        assertEquals("gross_amount", DatabaseManager.toSnakeCase("grossAmount"));

        assertEquals("billingType", DatabaseManager.toCamelCase("billing_type"));
        assertEquals("customerId", DatabaseManager.toCamelCase("customer_id"));
    }

    @Test
    @DisplayName("Test saveBills and retrieval with POJO upsert")
    void testSaveBillsAndRetrieve() throws Exception {
        String testBillJson = """
            {
              "bills": [
                {
                  "id": "db-test-bill-001",
                  "documentNumber": 80001,
                  "billingType": "CHARGE",
                  "billStatus": "OPEN",
                  "currency": "EUR"
                }
              ]
            }
            """;
        int count = DatabaseManager.saveBills(testBillJson);
        assertTrue(count >= 1);

        String single = DatabaseManager.getBillById("db-test-bill-001");
        assertNotNull(single);
        assertTrue(single.contains("db-test-bill-001"));
        assertTrue(single.contains("80001"));
    }

    @Test
    @DisplayName("Test dynamic table synthesis for arbitrary new service (ensureServiceTable)")
    void testEnsureServiceTableDynamicDDL() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        var sampleJson = mapper.readTree("""
            {
              "id": "item-dynamic-1",
              "metricName": "CPU_CYCLES",
              "active": true,
              "score": 98.5,
              "count": 1500
            }
            """);

        try (Connection conn = DatabaseManager.getConnection()) {
            assertDoesNotThrow(() -> {
                DatabaseManager.ensureServiceTable(conn, "test_dynamic_metrics", sampleJson);
            });
        }
    }

    @Test
    @DisplayName("Test getStats returns structured analytics JSON")
    void testGetStats() throws Exception {
        String statsJson = DatabaseManager.getStats();
        assertNotNull(statsJson);
        assertTrue(statsJson.contains("totalBills"));
        assertTrue(statsJson.contains("byStatus"));
        assertTrue(statsJson.contains("byType"));
        assertTrue(statsJson.contains("dbEngine"));
    }
}
