package com.goyobo.sqlonline

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration

@Configuration
class Configuration(
    @Value("\${showAbout}")
    val showAbout: Boolean
)
