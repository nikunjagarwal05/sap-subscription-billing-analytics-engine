package projectsap.sap.common;

import projectsap.sap.bill.model.Bill;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Central registry mapping SAP service identifiers to their target POJO classes and metadata.
 * Enables the system to support any arbitrary SAP service dynamically without hardcoded branching.
 */
public class SapServiceRegistry {

    public static class ServiceDescriptor<T> {
        private final String serviceId;
        private final String displayName;
        private final String defaultEndpoint;
        private final Class<T> pojoClass;
        private final String description;

        public ServiceDescriptor(String serviceId, String displayName, String defaultEndpoint, Class<T> pojoClass, String description) {
            this.serviceId = serviceId;
            this.displayName = displayName;
            this.defaultEndpoint = defaultEndpoint;
            this.pojoClass = pojoClass;
            this.description = description;
        }

        public String getServiceId() {
            return serviceId;
        }

        public String getDisplayName() {
            return displayName;
        }

        public String getDefaultEndpoint() {
            return defaultEndpoint;
        }

        public Class<T> getPojoClass() {
            return pojoClass;
        }

        public String getDescription() {
            return description;
        }
    }

    private static final Map<String, ServiceDescriptor<?>> REGISTRY = new LinkedHashMap<>();

    static {
        // 1. SAP Subscription Billing - Bills Service
        register(new ServiceDescriptor<>(
                "bills",
                "SAP Subscription Billing - Bills",
                "https://sandbox.api.sap.com/subscriptionbilling/api/bill/v2/bills?pageNumber=1&pageSize=40&$count=true",
                Bill.class,
                "Retrieves customer invoices, line items, and financial charges"
        ));

        // 2. SAP Subscription Billing - Subscriptions Service
        register(new ServiceDescriptor<>(
                "subscriptions",
                "SAP Subscription Billing - Subscriptions",
                "https://sandbox.api.sap.com/subscriptionbilling/api/subscription/v1/subscriptions?pageSize=40",
                projectsap.sap.subscription.model.Subscription.class,
                "Manages customer subscriptions, recurring contracts, and lifecycles"
        ));

        // 3. SAP Subscription Billing - Products Service
        register(new ServiceDescriptor<>(
                "products",
                "SAP Subscription Billing - Products",
                "https://sandbox.api.sap.com/subscriptionbilling/api/product/v1/products?pageSize=40",
                projectsap.sap.product.model.Product.class,
                "Maintains catalog of billable products, digital items, and pricing models"
        ));

        // 4. SAP Subscription Billing - Rate Plans Service
        register(new ServiceDescriptor<>(
                "rate_plans",
                "SAP Subscription Billing - Rate Plans",
                "https://sandbox.api.sap.com/subscriptionbilling/api/rate-plan/v1/rate-plans?pageSize=40",
                projectsap.sap.rate_plan.model.RatePlan.class,
                "Defines recurring pricing structures, fee models, and tier policies"
        ));

        // 5. SAP Subscription Billing - Usage Records Service
        register(new ServiceDescriptor<>(
                "usagerecords",
                "SAP Subscription Billing - Usage Records",
                "https://sandbox.api.sap.com/subscriptionbilling/api/usage-records/v1/usage-records?pageSize=40",
                projectsap.sap.usagerecords.model.Usagerecords.class,
                "Processes metered consumption records and volume-based rating data"
        ));

        // 6. SAP Subscription Billing - Allowances Service
        register(new ServiceDescriptor<>(
                "allowances",
                "SAP Subscription Billing - Allowances",
                "https://sandbox.api.sap.com/subscriptionbilling/api/allowance/v1/allowances?pageSize=40",
                projectsap.sap.allowances.model.Allowances.class,
                "Tracks customer allowances, balances, and quota allocations"
        ));
    }

    /**
     * Registers a new SAP service descriptor with its corresponding auto-generated POJO class.
     */
    public static <T> void register(ServiceDescriptor<T> descriptor) {
        REGISTRY.put(descriptor.getServiceId().toLowerCase(), descriptor);
    }

    /**
     * Dynamically registers a service by loading the POJO class name dynamically via Class.forName.
     */
    @SuppressWarnings("unchecked")
    public static boolean registerDynamic(String serviceId, String displayName, String defaultEndpoint, String pojoClassName, String description) {
        try {
            Class<?> clazz = Class.forName(pojoClassName);
            register(new ServiceDescriptor(serviceId, displayName, defaultEndpoint, clazz, description));
            System.out.println("[Registry] Dynamically registered service '" + serviceId + "' with POJO " + pojoClassName);
            return true;
        } catch (ClassNotFoundException e) {
            System.err.println("[Registry] Could not dynamically load POJO class: " + pojoClassName);
            return false;
        }
    }

    /**
     * Retrieves the descriptor for a registered service ID.
     */
    @SuppressWarnings("unchecked")
    public static <T> ServiceDescriptor<T> getDescriptor(String serviceId) {
        if (serviceId == null) return null;
        return (ServiceDescriptor<T>) REGISTRY.get(serviceId.toLowerCase());
    }

    /**
     * Returns all registered service identifiers.
     */
    public static Set<String> getAvailableServices() {
        return Collections.unmodifiableSet(REGISTRY.keySet());
    }

    /**
     * Returns all registered service descriptors.
     */
    public static Map<String, ServiceDescriptor<?>> getAllDescriptors() {
        return Collections.unmodifiableMap(REGISTRY);
    }
}
