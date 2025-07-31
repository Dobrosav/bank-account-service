package rs.yettelbank.bankaccountservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Bank Account Microservice API")
                        .version("1.0")
                        .description("RESTful API for managing bank accounts. This service allows for creating, retrieving, updating, and closing bank accounts."));
    }
}