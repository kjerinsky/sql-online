package com.goyobo.sqlonline.data

import javax.validation.constraints.NotBlank

class ErdData {
    private val tables = hashMapOf<String, ErdTable>()
    val columns = hashMapOf<String, ErdColumn>()
//    val foreignKeys = hashMapOf<String, String>()

    fun createTable(table: ErdTable) {
        tables[table.name] = table
    }

    fun deleteTable(table: ErdTable) {
        tables.remove(table.name)
    }

    fun createColumn(column: ErdColumn) {
        column.table.addColumn(column)
        columns[column.notation()] = column
    }

    fun deleteColumn(column: ErdColumn) {
        val table = tables[column.table.name]
        table?.removeColumn(column)
        columns.remove(column.name)
    }

    fun tableCollection(): MutableCollection<ErdTable> = tables.values

    fun columnCollection(): MutableCollection<ErdColumn> = columns.values

//    fun addForeignKey(source: String, reference: String) {
//        foreignKeys[source] = reference
//    }

    fun clear() {
        tables.clear()
        columns.clear()
//        foreignKeys.clear()
    }
}

class ErdTable(@field:NotBlank(message = "Required") var name: String) {
    private val columns = linkedMapOf<String, ErdColumn>()

    fun addColumn(column: ErdColumn) {
        columns[column.name] = column
    }

    fun removeColumn(column: ErdColumn) {
        columns.remove(column.name)
    }

    fun columnCollection() = columns.values
}

class ErdColumn(
    @field:NotBlank(message = "Required")
    var name: String,
    var type: String? = null,
    val table: ErdTable,
    var foreignKey: ErdColumn? = null,
    var primaryKey: Boolean = false
) {
    fun notation() = "${table.name}.$name"
}

enum class EntityMode {
    CREATE, UPDATE
}
