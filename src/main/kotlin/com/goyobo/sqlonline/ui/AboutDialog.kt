package com.goyobo.sqlonline.ui

import com.github.mvysny.karibudsl.v10.anchor
import com.github.mvysny.karibudsl.v10.h4
import com.github.mvysny.karibudsl.v10.span
import com.vaadin.flow.component.Composite
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.orderedlayout.VerticalLayout

class AboutDialog : Composite<Dialog>() {
    private val body = VerticalLayout().apply {
        isSpacing = false

        h4("SQL Online")
        span("version - 0.1")
        span("author - Kevin Jerinsky")
        anchor("https://github.com/kjerinsky/sql-online", "Github") {
            setTarget("_blank")
        }
    }

    init {
        content.isModal = true
        content.add(body)
        content.open()
    }
}
