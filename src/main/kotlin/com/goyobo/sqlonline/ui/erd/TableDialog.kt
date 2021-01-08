package com.goyobo.sqlonline.ui.erd

import com.github.mvysny.karibudsl.v10.*
import com.goyobo.sqlonline.data.EntityMode
import com.goyobo.sqlonline.data.ErdTable
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

class TableDialog(
    table: ErdTable,
    listener: TableDialogListener,
    mode: EntityMode
) : Composite<EnhancedDialog>() {
    private val binder = beanValidationBinder<ErdTable>()

    private val header = HorizontalLayout().apply {
        setWidthFull()
        alignItems = FlexComponent.Alignment.BASELINE

        h4("Add Table") {
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
                bind(binder).bind(ErdTable::name)
            }
        }
    }

    private val footer = Button("Add").apply {
        addThemeVariants(ButtonVariant.LUMO_PRIMARY)
        addClickShortcut(Key.ENTER)
        addClickListener {
            if (binder.validate().isOk && binder.writeBeanIfValid(table)) {
                when (mode) {
                    EntityMode.CREATE -> listener.createTable(table)
                    EntityMode.UPDATE -> listener.updateTable()
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

        binder.readBean(table)
    }

}

interface TableDialogListener : Serializable {
    fun createTable(table: ErdTable)
    fun updateTable()
}
