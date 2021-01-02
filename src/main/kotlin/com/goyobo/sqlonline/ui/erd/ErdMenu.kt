package com.goyobo.sqlonline.ui.erd

import com.flowingcode.vaadin.addons.fontawesome.FontAwesome
import com.github.mvysny.karibudsl.v10.*
import com.vaadin.flow.component.HasComponents
import com.vaadin.flow.component.contextmenu.MenuItem
import com.vaadin.flow.component.html.Anchor
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.server.InputStreamFactory
import com.vaadin.flow.server.StreamResource
import java.io.ByteArrayInputStream
import java.io.Serializable


class ErdMenu(private val listener: ErdMenuListener) : KComposite() {
    private lateinit var refreshButton: MenuItem

    @Suppress("unused")
    private val root = ui {
        var download = Anchor(StreamResource("filename.yml", InputStreamFactory {
            ByteArrayInputStream("Test".toByteArray())
        }), "Donwload")
        download.element.setAttribute("download", true)
        download.style["display"] = "none"

        horizontalLayout {
            menuBar {
                item("File") {
                    item("New") {
                        addComponentAsFirst(FontAwesome.Regular.FILE.create())
                        addClickListener { listener.new() }
                    }
                    subMenu.add(hr())
                    item("Import") {
                        addComponentAsFirst(FontAwesome.Solid.FILE_IMPORT.create())
                    }
                    item("Export") {
                        addComponentAsFirst(FontAwesome.Solid.FILE_EXPORT.create())
                        addClickListener { listener.export() }
                    }
                    subMenu.add(download)
                }
                item("Image") {
                    item("SVG") {
                        addComponentAsFirst(FontAwesome.Regular.FILE_IMAGE.create())
                    }
                    item("PNG") {
                        addComponentAsFirst(FontAwesome.Regular.FILE_CODE.create())
                    }
                }
                refreshButton = item(VaadinIcon.REFRESH.create()) {
                    isEnabled = false
                }
            }
            checkBox("Auto reload preview") {
                value = true

                addValueChangeListener {
                    refreshButton.isEnabled = !it.value
                }
            }
        }
    }

    fun isAutoReloadEnabled(): Boolean = !refreshButton.isEnabled
}

interface ErdMenuListener : Serializable {
    fun new()

    //    fun reload()
    fun export()
}

@VaadinDsl
fun (@VaadinDsl HasComponents).erdMenu(
    listener: ErdMenuListener,
    block: (@VaadinDsl ErdMenu).() -> Unit = {}
): ErdMenu =
    init(ErdMenu(listener), block)
