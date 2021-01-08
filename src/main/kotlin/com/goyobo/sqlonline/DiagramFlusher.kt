package com.goyobo.sqlonline

import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.io.File
import java.util.*

@Component
class DiagramFlusher {
    private val ttl = 300_000 // 5m * 60s * 1000ms

    @Suppress("unused")
    @Scheduled(fixedRate = 300_000)
    private fun flushDir() {
        File("/tmp/GraphvizJava").listFiles()?.forEach {
            val diff = Date().time - it.lastModified()
            if (diff > ttl) {
                it.deleteRecursively()
            }
        }
    }
}
