package ru.my.scents.configuration;

import io.restassured.RestAssured;
import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

@Import({TestConfig.class})
@SpringBootTest(webEnvironment = RANDOM_PORT)
public abstract class BaseIntegrationTest extends BaseTest {

    private static final MongoDBContainer MONGO_DB_CONTAINER;

    @Value("${local.server.port}")
    private int localPort;

    static {
        MONGO_DB_CONTAINER = new MongoDBContainer(DockerImageName.parse("mongo:latest"))
                .waitingFor(Wait.defaultWaitStrategy())
                .withExposedPorts(27017);
    }

    @DynamicPropertySource
    static void setContainerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", MONGO_DB_CONTAINER::getReplicaSetUrl);
    }

    @BeforeAll
    static void init() {
        MONGO_DB_CONTAINER.start();
    }

    @BeforeEach
    void setUp() {
        RestAssured.requestSpecification = given().port(localPort).contentType(JSON);
    }
}