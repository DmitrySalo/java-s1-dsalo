package ru.my.scents.infra.db.mongodb;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.mongodb.MongoMetricsCommandListener;
import io.micrometer.core.instrument.binder.mongodb.MongoMetricsConnectionPoolListener;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

@Configuration
public class MongoDBConfig {

    @Value("${spring.application.name}")
    private String applicationName;

    @Value("${spring.data.mongodb.uri}")
    private String mongoUri;

    @Bean
    ConnectionString connectionString() {
        return new ConnectionString(mongoUri);
    }

    @Bean
    MongoClient mongoClient(MeterRegistry meterRegistry) {
        MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
                .applyConnectionString(connectionString())
                .applicationName(applicationName)
                .applyToConnectionPoolSettings(builder ->
                        builder.addConnectionPoolListener(new MongoMetricsConnectionPoolListener(meterRegistry)))
                .addCommandListener(new MongoMetricsCommandListener(meterRegistry))
                .build();

        return MongoClients.create(mongoClientSettings);
    }

    @Bean
    MongoTemplate mongoTemplate(
            MongoClient mongoClient,
            MongoMappingContext mongoMappingContext,
            ConnectionString connectionString
    ) {
        SimpleMongoClientDatabaseFactory factory = new SimpleMongoClientDatabaseFactory(
                mongoClient,
                Objects.requireNonNull(connectionString.getDatabase()));

        MappingMongoConverter converter = new MappingMongoConverter(
                new DefaultDbRefResolver(factory),
                mongoMappingContext);

        converter.setTypeMapper(new DefaultMongoTypeMapper(null));
        converter.afterPropertiesSet();

        return new MongoTemplate(factory, converter);
    }
}