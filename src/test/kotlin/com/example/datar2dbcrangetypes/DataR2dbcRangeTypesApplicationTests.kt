package com.example.datar2dbcrangetypes

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.r2dbc.core.await
import java.time.LocalDateTime

@SpringBootTest
class DataR2dbcRangeTypesApplicationTests {
    @Autowired
    private lateinit var exampleRepository: ExampleRepository

    @Autowired
    private lateinit var template: R2dbcEntityTemplate

    @Test
    fun `inserts in example table`() {
        val example = Example(
            range = TsRange(LocalDateTime.now(), LocalDateTime.now().plusDays(1))
        )

        runBlocking {
            assertDoesNotThrow {
                exampleRepository.save(example)
            }
        }
    }

    @Test
    fun `reads from example table`() {
        runBlocking {
            exampleRepository.deleteAll()

            template.databaseClient.sql("""
                INSERT INTO example (range) VALUES ('["2021-11-01 00:00:00","2021-11-02 00:00:00"]');
            """.trimIndent()).await()

            val result = exampleRepository.findAll().first()
            assertTrue(result.range.start.year == 2021)
        }
    }
}
