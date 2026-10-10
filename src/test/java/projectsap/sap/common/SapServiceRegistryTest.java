package projectsap.sap.common;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import projectsap.sap.bill.model.Bill;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("SapServiceRegistry Unit Tests (Dynamic Service Catalog)")
public class SapServiceRegistryTest {

    @Test
    @DisplayName("Test core registered services exist in registry")
    void testCoreServicesRegistered() {
        Set<String> services = SapServiceRegistry.getAvailableServices();
        assertTrue(services.contains("bills"));
        assertTrue(services.contains("subscriptions"));
        assertTrue(services.contains("products"));
        assertTrue(services.contains("rate_plans"));
        assertTrue(services.contains("usagerecords"));
    }

    @Test
    @DisplayName("Test getDescriptor returns valid metadata")
    void testGetDescriptor() {
        var desc = SapServiceRegistry.getDescriptor("bills");
        assertNotNull(desc);
        assertEquals("bills", desc.getServiceId());
        assertEquals(Bill.class, desc.getPojoClass());
        assertTrue(desc.getDefaultEndpoint().contains("sandbox.api.sap.com"));
        assertNotNull(desc.getDescription());
    }

    @Test
    @DisplayName("Test case-insensitivity of service descriptor lookup")
    void testCaseInsensitivity() {
        assertNotNull(SapServiceRegistry.getDescriptor("BILLS"));
        assertNotNull(SapServiceRegistry.getDescriptor("Bills"));
        assertNotNull(SapServiceRegistry.getDescriptor("bills"));
    }

    @Test
    @DisplayName("Test unregistered service returns null")
    void testUnregisteredServiceReturnsNull() {
        assertNull(SapServiceRegistry.getDescriptor("non_existent_microservice_xyz"));
        assertNull(SapServiceRegistry.getDescriptor(null));
    }

    @Test
    @DisplayName("Test dynamic registration via Class.forName")
    void testDynamicRegistration() {
        boolean success = SapServiceRegistry.registerDynamic(
                "custom_dynamic",
                "Custom Dynamic Test Service",
                "https://api.sap.com/custom",
                "projectsap.sap.bill.model.Bill",
                "Dynamic test registration"
        );
        assertTrue(success);
        assertNotNull(SapServiceRegistry.getDescriptor("custom_dynamic"));

        boolean failure = SapServiceRegistry.registerDynamic(
                "invalid_dynamic",
                "Invalid Class Service",
                "https://api.sap.com/invalid",
                "non.existent.class.Path",
                "Should fail safely"
        );
        assertFalse(failure);
    }
}
