package projectsap.sap.common;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("SapAutoServiceManager Unit Tests (Schema Inference Engine)")
public class SapAutoServiceManagerTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    @DisplayName("Test inferOpenApiSpec generates compliant OpenAPI 3.0 specification from raw payload")
    void testInferOpenApiSpec() throws Exception {
        String rawData = """
            {
              "items": [
                {
                  "id": "tenant-001",
                  "companyName": "Acme Global",
                  "active": true,
                  "storageQuotaGb": 500,
                  "monthlyFee": 149.99,
                  "metadata": {
                    "tier": "PREMIUM",
                    "region": "EU_CENTRAL"
                  }
                }
              ]
            }
            """;

        JsonNode root = mapper.readTree(rawData);
        String specJson = SapAutoServiceManager.inferOpenApiSpec("tenant", root);

        assertNotNull(specJson);
        assertTrue(specJson.contains("\"openapi\" : \"3.0.0\""));
        assertTrue(specJson.contains("Tenant"));
        assertTrue(specJson.contains("companyName"));
        assertTrue(specJson.contains("active"));
        assertTrue(specJson.contains("monthlyFee"));

        // Validate that generated schema is valid JSON
        JsonNode specTree = mapper.readTree(specJson);
        assertTrue(specTree.has("components"));
        assertTrue(specTree.get("components").has("schemas"));
    }
}
