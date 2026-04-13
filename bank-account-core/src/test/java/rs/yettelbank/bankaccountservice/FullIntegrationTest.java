package rs.yettelbank.bankaccountservice;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.testcontainers.containers.MSSQLServerContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest
@EmbeddedKafka(partitions = 1, topics = {"bank-transactions"})
@Testcontainers
public abstract class FullIntegrationTest {
    @Container
    @ServiceConnection
    private static final MSSQLServerContainer<?> mssqlServer = new MSSQLServerContainer<>(
            DockerImageName.parse("mcr.microsoft.com/azure-sql-edge:latest")
                    .asCompatibleSubstituteFor("mcr.microsoft.com/mssql/server"))
            .acceptLicense();
}
