package com.goyobo.sqlonline.ui.erd

import com.github.mvysny.karibudsl.v10.*
import com.goyobo.sqlonline.data.ErdTable
import com.vaadin.flow.component.HasComponents
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.grid.GridVariant
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.function.ValueProvider
import java.io.Serializable

class TableGrid(tableCollection: MutableCollection<ErdTable>, listener: TableGridListener<ErdTable>) : KComposite() {
    @Suppress("unused")
    private val root = ui {
        grid<ErdTable>()
    }

    private val editColumn = ValueProvider { table: ErdTable ->
        HorizontalLayout().apply {
            button("Edit", VaadinIcon.FORM.create()) {
                addClickListener { listener.edit(table) }
            }
            iconButton(VaadinIcon.TRASH.create()) {
                addThemeVariants(ButtonVariant.LUMO_ERROR)
                addClickListener { listener.delete(table) }
            }
        }
    }

    init {
        // DCEVM reload crashes on lambdas with karibu
        root.apply {
            setSizeFull()
            addThemeVariants(GridVariant.LUMO_COMPACT, GridVariant.LUMO_ROW_STRIPES)

            addColumnFor(ErdTable::name).setHeader("Table")
            addColumn { it.columnCollection().size }.setHeader("Columns")
            addComponentColumn(editColumn)

            asSingleSelect().addValueChangeListener { listener.select(it.value) }
            setItems(tableCollection)
        }
    }

    fun refresh() {
        root.dataProvider.refreshAll()
    }
}

interface TableGridListener<B> : Serializable {
    fun select(table: B?)
    fun edit(table: B)
    fun delete(table: B)
}

@VaadinDsl
fun (@VaadinDsl HasComponents).tableGrid(
    tableCollection: MutableCollection<ErdTable>,
    listener: TableGridListener<ErdTable>,
    block: (@VaadinDsl TableGrid).() -> Unit = {}
) = init(TableGrid(tableCollection, listener), block)
