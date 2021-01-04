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

class ColumnGrid(listener: ColumnGridListener<Diagram.Column>) : KComposite() {
    @Suppress("unused")
    private val root = ui {
        grid<Diagram.Column>()
    }

    private val editColumn = ValueProvider { bean: Diagram.Column ->
        val edit = Button("Edit")
        val delete = Button(VaadinIcon.TRASH.create()).apply {
            addThemeVariants(ButtonVariant.LUMO_ERROR)
            addClickListener { listener.delete(bean) }
        }
        HorizontalLayout(edit, delete)
    }

    init {
        root.apply {
            setSizeFull()
            addColumnFor(Diagram.Column::name).setHeader("Column")
            addColumnFor(Diagram.Column::type)
            addColumnFor(Diagram.Column::primaryKey)
            addComponentColumn(editColumn)
        }
    }

    fun setColumns(columns: ArrayList<Diagram.Column>) {
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
    fun delete(column: B)
}

@VaadinDsl
fun (@VaadinDsl HasComponents).columnGrid(
    listener: ColumnGridListener<Diagram.Column>,
    block: (@VaadinDsl ColumnGrid).() -> Unit = {}
) = init(ColumnGrid(listener), block)
