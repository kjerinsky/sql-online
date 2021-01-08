package com.goyobo.sqlonline.ui

import com.vaadin.flow.component.html.Label

class RequiredLabel(text: String) : Label(text) {
    init {
        addClassName("required")
    }
}
