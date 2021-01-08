package com.goyobo.sqlonline.ui.erd

import com.github.mvysny.karibudsl.v10.*
import com.goyobo.sqlonline.data.ErdColumn
import com.vaadin.flow.component.HasComponents
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.grid.GridVariant
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.function.ValueProvider
import java.io.Serializable

class ColumnGrid(listener: ColumnGridListener<ErdColumn>) : KComposite() {
    @Suppress("unused")
    private val root = ui {
        grid<ErdColumn>()
    }

    private val editColumn = ValueProvider { column: ErdColumn ->
        HorizontalLayout().apply {
            button("Edit", VaadinIcon.FORM.create()) {
                addClickListener { listener.edit(column) }
            }
            iconButton(VaadinIcon.TRASH.create()) {
                addThemeVariants(ButtonVariant.LUMO_ERROR)
                addClickListener { listener.delete(column) }
            }
        }
    }

    init {
        root.apply {
            setSizeFull()
            addThemeVariants(GridVariant.LUMO_COMPACT, GridVariant.LUMO_ROW_STRIPES)

            addColumnFor(ErdColumn::name).setHeader("Column")
            addColumnFor(ErdColumn::type)
            addColumnFor(ErdColumn::primaryKey)
            addColumn { it.foreignKey?.table?.name }.setHeader("Foreign Reference")
            addComponentColumn(editColumn)
        }
    }

    fun setColumns(columns: MutableCollection<ErdColumn>) {
        root.setItems(columns)
    }

    fun clearGrid() {
        setColumns(arrayListOf())
    }

    fun refresh() {
        root.dataProvider.refreshAll()
    }
}

interface ColumnGridListener<B> : Serializable {
    fun edit(column: B)
    fun delete(column: B)
}

@VaadinDsl
fun (@VaadinDsl HasComponents).columnGrid(
    listener: ColumnGridListener<ErdColumn>,
    block: (@VaadinDsl ColumnGrid).() -> Unit = {}
) = init(ColumnGrid(listener), block)
