package ru.my.scents.configuration;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles(profiles = "test")
@TestMethodOrder(value = MethodOrderer.Random.class)
public abstract class BaseTest {
}
