package ru.my.scents.configuration;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import ru.my.scents.boundary.gateway.FragranceGateway;
import ru.my.scents.fakeobject.gateway.FakeFragranceGrpcGateway;

@TestConfiguration
public class TestConfig {

    @Primary
    @Bean
    public FragranceGateway getFragranceGateway() {
        return new FakeFragranceGrpcGateway();
    }
}
