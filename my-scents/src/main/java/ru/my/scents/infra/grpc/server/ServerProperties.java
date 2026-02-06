package ru.my.scents.infra.grpc.server;

import java.time.Duration;
import java.util.Random;
import lombok.Data;

@Data
public class ServerProperties {

    private int port = new Random().nextInt(10000);
    private Duration shutdownGracePeriod = Duration.ofSeconds(30);
}
