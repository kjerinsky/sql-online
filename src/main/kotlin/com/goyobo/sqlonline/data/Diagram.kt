package com.goyobo.sqlonline.data

import guru.nidi.graphviz.engine.Graphviz

class Diagram(private val tables: ArrayList<Table>) {

    private val sb = StringBuilder()
    private val refs = StringBuilder()

    init {
        sb.append("digraph {")
        newLine()
        writeAttrs()
        newLine(2)
        writeTables()
        newLine(2)
        sb.append(refs)
        sb.append("}")
    }

    override fun toString(): String {
        return sb.toString()
    }

    fun graph(): Graphviz = Graphviz.fromString(toString())

    private fun writeAttrs() {
        sb.append(
            """
            |${tab(1)}graph [pad="0.5", nodesep="1", ranksep="1"];
            |${tab(1)}node [shape=none, fontname=verdana];
        """.trimMargin()
        )
    }

    private fun writeTables() {
        tables.forEachIndexed { index, table ->
            writeTable(table)
            if (tables.size - 1 != index) {
                newLine(2)
            }
        }
    }

    private fun writeTable(table: Table) {
        sb.append(
            """
            |${tab(1)}${table.name} [
            |${tab(2)}label=<<table border="1" cellborder="0" cellspacing="0" cellpadding="5" bgcolor="aliceblue">
            |${tab(3)}<tr><td bgcolor="lightblue" colspan="3" cellpadding="5">${table.name}</td></tr>
        """.trimMargin()
        )
        newLine()
        writeColumns(table.columns)
        newLine()
        sb.append(
            """
            |${tab(2)}</table>>
            |${tab(1)}];
        """.trimMargin()
        )
    }

    private fun writeColumns(columns: ArrayList<Column>) {
        columns.forEachIndexed { index, column ->
            val even = index % 2 == 0
            writeColumn(column, even)
            if (columns.size - 1 != index) {
                newLine()
            }
        }
    }

    private fun writeColumn(column: Column, even: Boolean) {
        val color = if (even) "bgcolor=\"#DCE4EB\"" else ""
        sb.append(
            """
            |${tab(3)}<tr>
            |${tab(4)}<td $color port="${column.name}">${columnKey(column)}</td>
            |${tab(4)}<td $color align="left">${column.name}  </td>
            |${tab(4)}<td $color align="left">${column.type}</td>
            |${tab(3)}</tr>
        """.trimMargin()
        )
    }

    private fun columnKey(column: Column): String = when {
        column.primaryKey -> "\uD83D\uDD11"
        column.foreignKey != null -> {
            writeReference(column)
            "\uD83D\uDD37"
        }
        else -> ""
    }

    private fun writeReference(column: Column) {
        val foreignKey = column.foreignKey
        if (foreignKey != null) {
            refs.append(
                """
                |${tab(1)}${column.table.name}:${column.name} -> ${foreignKey.table.name}:${foreignKey.name};
            """.trimMargin()
            )
            refs.append("\n")
        }
    }

    private fun tab(level: Int) = "  ".repeat(level)

    private fun newLine(times: Int = 1) {
        sb.append("\n".repeat(times))
    }

    class Table(val name: String, val columns: ArrayList<Column> = arrayListOf())
    class Column(
        val name: String,
        var type: String? = null,
        val table: Table,
        var foreignKey: Column? = null,
        var primaryKey: Boolean = false
    )
}
