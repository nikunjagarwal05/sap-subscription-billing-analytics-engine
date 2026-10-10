package projectsap.sap.analytics;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("AiPredictor Unit Tests (Machine Learning & Predictive Forecasting)")
public class AiPredictorTest {

    @Test
    @DisplayName("Test generateRevenueForecast computes valid OLS regression and future timeline")
    void testRevenueForecast() {
        AiPredictor.PredictionResult result = AiPredictor.generateRevenueForecast(6);

        assertNotNull(result);
        assertEquals("Gross Billing Revenue", result.targetField);
        assertTrue(result.rSquared >= 0.0 && result.rSquared <= 1.0, "R-squared must be bounded in [0, 1]");
        assertTrue(result.timeline.size() >= 6, "Timeline must contain historical + forecasted periods");

        // Verify confidence intervals: lowerBound <= value <= upperBound for predicted points
        for (AiPredictor.DataPoint pt : result.timeline) {
            if (pt.isPredicted) {
                assertTrue(pt.lowerBound <= pt.value, "Lower bound must be <= predicted value");
                assertTrue(pt.value <= pt.upperBound, "Predicted value must be <= upper bound");
            }
        }

        assertNotNull(result.modelInterpretation);
        assertFalse(result.modelInterpretation.isEmpty());
    }

    @Test
    @DisplayName("Test computeChurnRiskAssessment produces valid portfolio health scoring")
    void testChurnRiskAssessment() {
        Map<String, Object> risk = AiPredictor.computeChurnRiskAssessment();

        assertNotNull(risk);
        assertTrue(risk.containsKey("totalAssessedAccounts"));
        assertTrue(risk.containsKey("portfolioHealthScore"));
        assertTrue(risk.containsKey("lowRiskCount"));
        assertTrue(risk.containsKey("highRiskCount"));

        double health = ((Number) risk.get("portfolioHealthScore")).doubleValue();
        assertTrue(health >= 0.0 && health <= 100.0, "Health score must be between 0 and 100%");
    }

    @Test
    @DisplayName("Test computeMultiServiceKpis aggregates all 5 integrated services")
    void testMultiServiceKpis() {
        Map<String, Object> kpis = AiPredictor.computeMultiServiceKpis();

        assertNotNull(kpis);
        assertTrue(kpis.containsKey("billsCount"));
        assertTrue(kpis.containsKey("subscriptionsCount"));
        assertTrue(kpis.containsKey("productsCount"));
        assertTrue(kpis.containsKey("ratePlansCount"));
        assertTrue(kpis.containsKey("usageRecordsCount"));
    }
}
