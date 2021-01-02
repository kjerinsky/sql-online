package com.goyobo.sqlonline.data

class ErdExport(tables: ArrayList<Diagram.Table>) {
    private val sb = StringBuilder()

    init {
        tables.forEach { table ->
            writeTable(table)
            if (table.columns.isNotEmpty()) {
                sb.appendLine("    columns:")
                table.columns.forEach { column ->
                    writeColumn(column)
                }
            }
        }
    }

    private fun writeTable(table: Diagram.Table) {
        sb.appendLine("- ${table.name}:")
    }

    private fun writeColumn(column: Diagram.Column) {
        sb.appendLine("      - ${column.name}:")
        sb.appendLine("          type: ${column.type}")
        if (column.primaryKey) {
            sb.appendLine("          primary_key: true")
        }
        column.foreignKey?.let { fk ->
            sb.appendLine("          foreign_key: ${fk.table.name}.${fk.name}")
        }
    }

    fun toYml() = sb.toString()
}
