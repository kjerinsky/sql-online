package com.goyobo.sqlonline.ui.erd

import com.github.mvysny.karibudsl.v10.*
import com.goyobo.sqlonline.data.Diagram
import com.vaadin.flow.component.HasComponents
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.function.ValueProvider
import java.io.Serializable

class TableGrid(
    tableData: ArrayList<Diagram.Table>,
    private val listener: TableGridListener<Diagram.Table>
) : KComposite() {
    @Suppress("unused")
    private val root = ui {
        grid<Diagram.Table>()
    }

    private val editColumn = ValueProvider { bean: Diagram.Table ->
        val edit = Button("Edit")
        val delete = Button(VaadinIcon.TRASH.create()).apply {
            addThemeVariants(ButtonVariant.LUMO_ERROR)
            addClickListener { listener.delete(bean) }
        }
        HorizontalLayout(edit, delete)
    }

    init {
        // DCEVM reload crashes on lambdas with karibu
        root.apply {
            setSizeFull()

            addColumnFor(Diagram.Table::name).setHeader("Table")
            addColumn { it.columns.size }.setHeader("Columns")
            addComponentColumn(editColumn)

            asSingleSelect().addValueChangeListener { listener.select(it.value) }
            setItems(tableData)
        }
    }

    fun refresh() {
        root.dataProvider.refreshAll()
    }
}

interface TableGridListener<B> : Serializable {
    fun select(bean: B)
    fun delete(bean: B)
}

@VaadinDsl
fun (@VaadinDsl HasComponents).tableGrid(
    tableData: ArrayList<Diagram.Table>,
    listener: TableGridListener<Diagram.Table>,
    block: (@VaadinDsl TableGrid).() -> Unit = {}
) = init(TableGrid(tableData, listener), block)
