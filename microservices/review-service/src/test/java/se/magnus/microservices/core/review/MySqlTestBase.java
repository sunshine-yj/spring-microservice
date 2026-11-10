package se.magnus.microservices.core.review;

import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.mysql.MySQLContainer;

@TestPropertySource(properties = "spring.jpa.hibernate.ddl-auto=create-drop")
public class MySqlTestBase {

    @ServiceConnection
    static final JdbcDatabaseContainer database = new MySQLContainer("mysql:9.2.0").withStartupTimeoutSeconds(300);

    static  {
        database.start();
    }
}
