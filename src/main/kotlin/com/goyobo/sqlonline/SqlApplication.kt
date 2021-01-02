package com.goyobo.sqlonline

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class SqlApplication

fun main(args: Array<String>) {
    runApplication<SqlApplication>(*args)
}
