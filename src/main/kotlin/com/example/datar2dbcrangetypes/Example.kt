package com.example.datar2dbcrangetypes

import io.r2dbc.postgresql.codec.PostgresTypes
import io.r2dbc.spi.Parameter
import io.r2dbc.spi.Parameters
import org.springframework.core.convert.converter.Converter
import org.springframework.data.annotation.Id
import org.springframework.data.convert.ReadingConverter
import org.springframework.data.convert.WritingConverter
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.time.format.DateTimeFormatterBuilder
import java.time.temporal.ChronoField

data class TsRange(val start: LocalDateTime, val end: LocalDateTime)

fun TsRange.toPostgresValue(): String =
    "[\"${start.format(PG_TIMESTAMP_FORMATTER)}\",\"${end.format(PG_TIMESTAMP_FORMATTER)}\"]"

private val PG_TIMESTAMP_FORMATTER = DateTimeFormatterBuilder()
    .appendPattern("yyyy-MM-dd HH:mm:ss")
    .appendFraction(ChronoField.MICRO_OF_SECOND, 0, 6, true)
    .toFormatter()

@ReadingConverter
object TsRangeDeserializer : Converter<String, TsRange> {
    private val regex = Regex("\\[\"(.*?)\",\"(.*?)\"]")

    override fun convert(source: String): TsRange {
        regex.matchEntire(source)?.let {
            val (start, end) = it.destructured
            return TsRange(
                LocalDateTime.parse(start, PG_TIMESTAMP_FORMATTER),
                LocalDateTime.parse(end, PG_TIMESTAMP_FORMATTER)
            )
        } ?: throw IllegalArgumentException("Invalid tsrange: $source")
    }
}

private val PG_RANGE_TYPE = PostgresTypes.PostgresType(3908, 3908, 3909, 3909, "tsrange", "R")

@WritingConverter
object TsRangeSerializer : Converter<TsRange, Parameter> {
    override fun convert(source: TsRange): Parameter {
        return Parameters.`in`(PG_RANGE_TYPE, source.toPostgresValue())
    }
}

@Table("example")
class Example(
    @Column("range")
    val range: TsRange
) {
    @Id
    var id: Long? = null
}

@Repository
interface ExampleRepository : CoroutineCrudRepository<Example, Long>
