package rs.yettelbank.bankaccountservice.api.model.request;
import jakarta.validation.constraints.NotNull;
import rs.yettelbank.bankaccountservice.model.AccountStatus;

import java.util.Objects;

public class UpdateAccountStatusRequestDTO {

    @NotNull(message = "Account status is required.")
    private AccountStatus status;

    public UpdateAccountStatusRequestDTO() {
    }

    public UpdateAccountStatusRequestDTO(AccountStatus status) {
        this.status = status;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public void setStatus(AccountStatus status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UpdateAccountStatusRequestDTO that = (UpdateAccountStatusRequestDTO) o;
        return status == that.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(status);
    }

    @Override
    public String toString() {
        return "UpdateAccountStatusRequestDTO{" +
                "status=" + status +
                '}';
    }
}