package com.example.datar2dbcrangetypes

import io.r2dbc.postgresql.PostgresqlConnectionConfiguration
import io.r2dbc.postgresql.PostgresqlConnectionFactory
import io.r2dbc.spi.ConnectionFactory
import org.springframework.context.annotation.Configuration
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories

@Configuration
@EnableR2dbcRepositories
class R2dbcConfig : AbstractR2dbcConfiguration() {
    override fun connectionFactory(): ConnectionFactory {
        // This is not actually used.
        return PostgresqlConnectionFactory(
            PostgresqlConnectionConfiguration.builder()
                .host("localhost")
                .port(5432)
                .database("postgres")
                .username("postgres")
                .password("postgres")
                .build()
        )
    }

    override fun getCustomConverters(): MutableList<Any> = mutableListOf(
        TsRangeSerializer,
        TsRangeDeserializer,
    )
}
