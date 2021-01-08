package com.goyobo.sqlonline.data

import java.io.BufferedReader
import java.io.InputStream

class ErdFileManager {
    class Export(tables: MutableCollection<ErdTable>) {
        private val sb = StringBuilder()

        init {
            tables.forEach { table ->
                writeTable(table)
                if (table.columnCollection().isNotEmpty()) {
                    sb.appendLine(COLUMNS_KEY)
                    table.columnCollection().forEach { column ->
                        writeColumn(column)
                    }
                }
            }
        }

        private fun writeTable(table: ErdTable) {
            sb.appendLine("$TABLE_PREFIX${table.name}:")
        }

        private fun writeColumn(column: ErdColumn) {
            sb.appendLine("$COLUMN_PREFIX${column.name}:")
            sb.appendLine(COLUMN_TYPE_PREFIX + column.type)
            if (column.primaryKey) {
                sb.appendLine(PRIMARY_KEY)
            }
            column.foreignKey?.let { fk ->
                sb.appendLine("$FOREIGN_KEY_PREFIX${fk.table.name}.${fk.name}")
            }
        }

        fun toYaml() = sb.toString()
    }

    class Import(inputStream: InputStream, private val erdData: ErdData) {
        private var currentTable: ErdTable? = null
        private var currentColumn: ErdColumn? = null
        private val foreignKeyMap = hashMapOf<String, String>()

        init {
            val reader = BufferedReader(inputStream.reader())
            reader.use { readFile(it) }
        }

        private fun readFile(br: BufferedReader) {
            var line = br.readLine()

            while (line != null) {
                with(Line(line)) {
                    when {
                        isTable() -> handleTable(this)
                        isColumn() -> handleColumn(this)
                        isColumnType() -> handleColumnType(this)
                        isPrimaryKey() -> handlePrimaryKey()
                        isForeignKey() -> handleForeignKey(this)
                        else -> {}
                    }
                }

                line = br.readLine()
            }

            linkForeignKeyReferences()
        }

        private fun handleTable(line: Line) {
            currentTable = ErdTable(line.tableName())
            erdData.createTable(currentTable!!)
        }

        private fun handleColumn(line: Line) {
            currentColumn = ErdColumn(line.columnName(), table = currentTable!!)
            erdData.createColumn(currentColumn!!)
        }

        private fun handleColumnType(line: Line) {
            currentColumn?.type = line.columnType()
        }

        private fun handlePrimaryKey() {
            currentColumn!!.primaryKey = true
        }

        private fun handleForeignKey(line: Line) {
            foreignKeyMap[columnNotation()] = line.foreignKey()
        }

        private fun columnNotation() = "${currentTable!!.name}.${currentColumn!!.name}"

        private fun linkForeignKeyReferences() {
            foreignKeyMap.forEach {
                val column = erdData.columns[it.key]
                val reference = erdData.columns[it.value]
                column?.foreignKey = reference
            }
        }

        private class Line(private val line: String) {
            fun isTable() = line.startsWith(TABLE_PREFIX)
            fun tableName() = line.substring(2, line.length - 1)
            fun isColumn() = line.startsWith(COLUMN_PREFIX)
            fun columnName() = line.substring(8, line.length - 1)
            fun isColumnType() = line.startsWith(COLUMN_TYPE_PREFIX)
            fun columnType() = line.substring(16, line.length)
            fun isPrimaryKey() = line == PRIMARY_KEY
            fun isForeignKey() = line.startsWith(FOREIGN_KEY_PREFIX)
            fun foreignKey() = line.substring(23, line.length)
        }
    }

    companion object {
        private const val TABLE_PREFIX = "- "
        private const val COLUMNS_KEY = "    columns:"
        private const val COLUMN_PREFIX = "      - "
        private const val COLUMN_TYPE_PREFIX = "          type: "
        private const val PRIMARY_KEY = "          primary_key: true"
        private const val FOREIGN_KEY_PREFIX = "          foreign_key: "
    }
}
