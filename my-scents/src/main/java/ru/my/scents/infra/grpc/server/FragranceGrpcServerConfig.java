package ru.my.scents.infra.grpc.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import jakarta.annotation.PreDestroy;
import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.my.scents.adapter.controller.grpc.FragranceGrpcController;
import ru.my.scents.infra.logger.Logger;

@Configuration
@RequiredArgsConstructor
public class FragranceGrpcServerConfig {

    private final Logger logger;
    private Server server;
    private Duration shutdownGracePeriod;
    private int port;

    @Bean(name = "serverProperties")
    @ConfigurationProperties(prefix = "grpc.server")
    ServerProperties serverProperties() {
        ServerProperties props = new ServerProperties();
        shutdownGracePeriod = props.getShutdownGracePeriod();
        port = props.getPort();
        return props;
    }

    @ConditionalOnBean(name = "serverProperties")
    @Bean
    Server fragranceGrpcServer(FragranceGrpcController fragranceGrpcController) throws IOException {
        this.server = ServerBuilder.forPort(port)
                .addService(fragranceGrpcController)
                .build()
                .start();

        logger.info("Fragrance gRPC сервер запущен на порту {}", port);

        return this.server;
    }

    @PreDestroy
    public void shutdown() {
        if (server != null && !server.isShutdown()) {
            try {
                logger.info("Завершение работы Fragrance gRPC сервера...");
                server.shutdown();
                if (!server.awaitTermination(shutdownGracePeriod.toSeconds(), TimeUnit.SECONDS)) {
                    logger.info("gRPC сервер не смог завершить работу некорректно, завершаем принудительно");
                    server.shutdownNow();
                }
                logger.info("Fragrance gRPC сервер успешно завершил работу");
            } catch (InterruptedException e) {
                logger.error("Ошибка gRPC сервера завершить работу", e);
                server.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }
}
