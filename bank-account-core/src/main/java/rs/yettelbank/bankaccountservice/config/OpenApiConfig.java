package rs.yettelbank.bankaccountservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Bank Account Microservice API")
                        .version("1.0")
                        .description("RESTful API for managing bank accounts. This service allows for creating, retrieving, updating, and closing bank accounts.")
                        .contact(new Contact().name("API Support").url("http://www.example.com/support").email("support@example.com"))
                        .license(new License().name("Apache 2.0").url("http://www.apache.org/licenses/LICENSE-2.0.html")))
                .servers(List.of(
                        new Server().url("http://bank-app-alb-952876333.eu-north-1.elb.amazonaws.com").description("Produkciono okruženje (ALB)"),
                        new Server().url("http://localhost:11056").description("Lokalno okruženje")
                ));
    }
}