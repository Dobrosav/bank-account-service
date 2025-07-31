package rs.yettelbank.bankaccountservice.exception;

import org.springframework.http.HttpStatus;

public class ServiceException extends RuntimeException {
    private final ErrorType errorType;
    private final HttpStatus httpCode;

    public ServiceException(ErrorType errorType, HttpStatus httpCode) {
        super(errorType.getMessage());
        this.errorType = errorType;
        this.httpCode = httpCode;
    }

    public ErrorType getErrorType() {
        return errorType;
    }

    public HttpStatus getHttpCode() {
        return httpCode;
    }
}
