package com.example.doit.common.constants

import java.time.format.DateTimeFormatter

object DatabaseConstants {
    const val LIST_SEPARATOR = "|"

    val DATE_FORMATTER: DateTimeFormatter = DateTimeFormatter.ISO_DATE
    val DATE_TIME_FORMATTER: DateTimeFormatter = DateTimeFormatter.ISO_DATE_TIME
}