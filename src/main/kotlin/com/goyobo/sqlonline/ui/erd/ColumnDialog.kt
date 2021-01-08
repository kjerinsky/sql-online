package com.goyobo.sqlonline.ui.erd

import com.github.mvysny.karibudsl.v10.*
import com.goyobo.sqlonline.data.EntityMode
import com.goyobo.sqlonline.data.ErdColumn
import com.goyobo.sqlonline.data.ErdData
import com.goyobo.sqlonline.ui.RequiredLabel
import com.vaadin.componentfactory.EnhancedDialog
import com.vaadin.componentfactory.theme.EnhancedDialogVariant
import com.vaadin.flow.component.Composite
import com.vaadin.flow.component.Key
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.formlayout.FormLayout
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import java.io.Serializable

class ColumnDialog(
    private val column: ErdColumn,
    listener: ColumnDialogListener,
    mode: EntityMode,
    private val erdData: ErdData
) : Composite<EnhancedDialog>() {
    private val binder = beanValidationBinder<ErdColumn>()

    private val header = HorizontalLayout().apply {
        setWidthFull()
        alignItems = FlexComponent.Alignment.BASELINE

        h4("Add Column") {
            flexGrow = 1.0
        }
        iconButton(VaadinIcon.CLOSE.create()) {
            onLeftClick { content.close() }
        }
    }

    private val form = FormLayout().apply {
        formItem(RequiredLabel("Name")) {
            textField {
                focus()
                bind(binder).bind(ErdColumn::name)
            }
        }
        formItem(RequiredLabel("Type")) {
            textField {
                bind(binder).bind(ErdColumn::type)
            }
        }
        formItem("Primary Key") {
            checkBox {
                bind(binder).bind(ErdColumn::primaryKey)
            }
        }
        formItem("Foreign Key") {
            comboBox<ErdColumn> {
                isClearButtonVisible = true

                setItems(foreignKeys())
                setItemLabelGenerator { it.notation() }

                bind(binder).bind(ErdColumn::foreignKey)
            }
        }
    }

    private val footer = Button("Add").apply {
        addThemeVariants(ButtonVariant.LUMO_PRIMARY)
        addClickShortcut(Key.ENTER)
        addClickListener {
            if (binder.validate().isOk && binder.writeBeanIfValid(column)) {
                when (mode) {
                    EntityMode.CREATE -> listener.createColumn(column)
                    EntityMode.UPDATE -> listener.updateColumn()
                }
                content.close()
            }
        }
    }

    init {
        with(content) {
            setThemeVariants(EnhancedDialogVariant.SIZE_SMALL)
            isCloseOnEsc = false

            setHeader(header)
            setContent(form)
            setFooter(footer)
            open()
        }

        binder.readBean(column)
    }

    private fun foreignKeys() = erdData.columnCollection()
        .filter { it.table.name != column.table.name }
        .sortedBy { it.notation() }
}

interface ColumnDialogListener : Serializable {
    fun createColumn(column: ErdColumn)
    fun updateColumn()
}
