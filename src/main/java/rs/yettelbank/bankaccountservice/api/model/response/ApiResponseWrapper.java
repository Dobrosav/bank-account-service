package rs.yettelbank.bankaccountservice.api.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponseWrapper<T> {
    private T data;

    private ApiErrorResponse error;

    public ApiResponseWrapper(T data) {
        this.data = data;
    }

    public ApiResponseWrapper(ApiErrorResponse error) {
        this.error = error;
    }

    public T getData() {
        return this.data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public ApiErrorResponse getError() {
        return this.error;
    }

    public void setError(ApiErrorResponse error) {
        this.error = error;
    }

    public String toString() {
        return "ApiResponseWrapper{data=" + this.data + ", error=" + this.error + "}";
    }
}
