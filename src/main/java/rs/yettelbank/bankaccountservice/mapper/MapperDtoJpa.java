package rs.yettelbank.bankaccountservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import rs.yettelbank.bankaccountservice.api.model.response.AccountResponseDTO;
import rs.yettelbank.bankaccountservice.db.entity.Account;

@Mapper
public interface MapperDtoJpa {
    MapperDtoJpa INSTANCE = Mappers.getMapper(MapperDtoJpa.class);

    AccountResponseDTO mapToAccountResponseDTO(Account account);
}
