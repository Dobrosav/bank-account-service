package rs.yettelbank.bankaccountservice.exception;

public enum ErrorType {
    ACCOUNT_NOT_FOUND("001", "Account not found"),
    BAD_REQUEST("002", "Bad request"),
    INVALID_ARGUMENT("003", "Invalid fields in the request"),
    UNRESOLVED_ERROR("100", "General error"),
    NOT_SUPPORTED_HTTP_METHOD("101", "Not supported http method"),
    NOT_VALID_REQUEST_FORMAT("103", "Not valid request format");

    private final String code;
    private final String message;

    ErrorType(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}