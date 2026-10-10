package projectsap.sap.common;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import projectsap.sap.bill.model.Bill;
import projectsap.sap.product.model.Product;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("GenericSapParser Unit Tests (Universal Jackson Deserializer)")
public class GenericSapParserTest {

    @Test
    @DisplayName("Test deserialization of top-level JSON array")
    void testParseListTopLevelArray() throws IOException {
        String json = """
            [
              { "id": "bill-test-01", "documentNumber": 5001 },
              { "id": "bill-test-02", "documentNumber": 5002 }
            ]
            """;
        List<Bill> list = GenericSapParser.parseList(json, Bill.class);
        assertNotNull(list);
        assertEquals(2, list.size());
        assertEquals("bill-test-01", list.get(0).getId());
        assertEquals(5001L, list.get(0).getDocumentNumber());
    }

    @Test
    @DisplayName("Test automatic unwrapping of SAP REST envelope { 'bills': [...] }")
    void testParseListRestWrapper() throws IOException {
        String json = """
            {
              "bills": [
                { "id": "bill-wrap-01", "documentNumber": 9001 }
              ]
            }
            """;
        List<Bill> list = GenericSapParser.parseList(json, Bill.class);
        assertNotNull(list);
        assertEquals(1, list.size());
        assertEquals("bill-wrap-01", list.get(0).getId());
    }

    @Test
    @DisplayName("Test automatic unwrapping of SAP OData envelope { 'd': { 'results': [...] } }")
    void testParseListODataWrapper() throws IOException {
        String json = """
            {
              "d": {
                "results": [
                  { "id": "prod-odata-01", "name": "OData Product" }
                ]
              }
            }
            """;
        List<Product> list = GenericSapParser.parseList(json, Product.class);
        assertNotNull(list);
        assertEquals(1, list.size());
        assertEquals("prod-odata-01", list.get(0).getId());
        assertEquals("OData Product", list.get(0).getName());
    }

    @Test
    @DisplayName("Test single object deserialization")
    void testParseObject() throws Exception {
        String json = """
            { "id": "single-bill-99", "documentNumber": 9999 }
            """;
        Bill bill = GenericSapParser.parseObject(json, Bill.class);
        assertNotNull(bill);
        assertEquals("single-bill-99", bill.getId());
        assertEquals(9999L, bill.getDocumentNumber());
    }

    @Test
    @DisplayName("Test unknown fields are tolerated without exceptions (FAIL_ON_UNKNOWN_PROPERTIES=false)")
    void testUnknownPropertiesTolerated() throws IOException {
        String json = """
            [
              { "id": "bill-unk-01", "unexpectedField": "ignoredVal", "futureVersionTag": 999 }
            ]
            """;
        List<Bill> list = GenericSapParser.parseList(json, Bill.class);
        assertNotNull(list);
        assertEquals(1, list.size());
        assertEquals("bill-unk-01", list.get(0).getId());
    }

    @Test
    @DisplayName("Test null and empty string handling returns empty list")
    void testNullAndEmptyHandling() throws IOException {
        assertTrue(GenericSapParser.parseList(null, Bill.class).isEmpty());
        assertTrue(GenericSapParser.parseList("", Bill.class).isEmpty());
        assertTrue(GenericSapParser.parseList("   ", Bill.class).isEmpty());
        assertNull(GenericSapParser.parseObject(null, Bill.class));
    }

    @Test
    @DisplayName("Test serialization to clean JSON string")
    void testToJsonSerialization() throws Exception {
        Bill bill = new Bill();
        bill.setId("json-test-bill");
        bill.setDocumentNumber(8888L);

        String json = GenericSapParser.toJson(bill);
        assertNotNull(json);
        assertTrue(json.contains("json-test-bill"));
        assertTrue(json.contains("8888"));
    }
}
