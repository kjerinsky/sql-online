package com.goyobo.sqlonline

import com.vaadin.flow.component.Key
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.dependency.CssImport
import com.vaadin.flow.component.notification.Notification
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.router.Route
import com.vaadin.flow.server.PWA

@Route("")
@PWA(name = "Project Base for Vaadin", shortName = "Project Base")
@CssImport.Container(value = [
    CssImport("./styles/shared-styles.css"),
    CssImport(value = "./styles/vaadin-text-field-styles.css", themeFor = "vaadin-text-field")
])
class MainView : VerticalLayout() {
    init {
        val textField = TextField("Your name")

        val greetService = GreetService()
        val button = Button(
            "Say hello"
        ) {
            Notification.show(
                greetService.greet(textField.value)
            )
        }

        button.addThemeVariants(ButtonVariant.LUMO_PRIMARY)

        button.addClickShortcut(Key.ENTER)

        addClassName("centered-content")
        add(textField, button)
    }
}
