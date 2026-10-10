package projectsap.sap.common.exception;

/**
 * Custom exception representing errors encountered during SAP API network operations.
 */
public class SapApiException extends Exception {

    private final int statusCode;
    private final String endpointUrl;

    public SapApiException(String message) {
        super(message);
        this.statusCode = 0;
        this.endpointUrl = "";
    }

    public SapApiException(String message, Throwable cause) {
        super(message, cause);
        this.statusCode = 0;
        this.endpointUrl = "";
    }

    public SapApiException(String message, int statusCode, String endpointUrl) {
        super(message + " [HTTP " + statusCode + " from " + endpointUrl + "]");
        this.statusCode = statusCode;
        this.endpointUrl = endpointUrl;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getEndpointUrl() {
        return endpointUrl;
    }
}
