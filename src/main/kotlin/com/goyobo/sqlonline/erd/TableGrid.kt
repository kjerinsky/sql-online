package com.goyobo.sqlonline.erd

import com.github.mvysny.karibudsl.v10.*
import com.goyobo.sqlonline.SampleData
import com.vaadin.flow.component.HasComponents
import java.io.Serializable

class TableGrid(private val listener: TableGridListener<Diagram.Table>) : KComposite() {
    @Suppress("unused")
    private val root = ui {
        grid<Diagram.Table> {
            setSizeFull()
            addColumnFor(Diagram.Table::name)
            setItems(SampleData.data)
        }
    }

    init {
        // DCEVM reload crashes on lambdas with karibu
        root.apply {
            addColumn { it.columns.size }.setHeader("Columns")
            asSingleSelect().addValueChangeListener {
                listener.selected(it.value)
            }
        }
    }
}

interface TableGridListener<B> : Serializable {
    fun selected(bean: B)
}

@VaadinDsl
fun (@VaadinDsl HasComponents).tableGrid(
    listener: TableGridListener<Diagram.Table>,
    block: (@VaadinDsl TableGrid).() -> Unit = {}
) = init(TableGrid(listener), block)
