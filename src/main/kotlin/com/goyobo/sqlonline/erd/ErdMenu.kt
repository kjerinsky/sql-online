package com.goyobo.sqlonline.erd

import com.flowingcode.vaadin.addons.fontawesome.FontAwesome
import com.github.mvysny.karibudsl.v10.*
import com.vaadin.flow.component.HasComponents
import com.vaadin.flow.component.contextmenu.MenuItem
import com.vaadin.flow.component.icon.VaadinIcon

class ErdMenu : KComposite() {
    private lateinit var refreshButton: MenuItem

    @Suppress("unused")
    private val root = ui {
        horizontalLayout {
            menuBar {
                item("File") {
                    item("New") {
                        addComponentAsFirst(FontAwesome.Regular.FILE.create())
                    }
                    subMenu.add(hr())
                    item("Import") {
                        addComponentAsFirst(FontAwesome.Solid.FILE_IMPORT.create())
                    }
                    item("Export") {
                        addComponentAsFirst(FontAwesome.Solid.FILE_EXPORT.create())
                    }
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

//interface ErdMenuListender<B> : Serializable {
//    fun reload()
//}

fun HasComponents.erdMenu(block: ErdMenu.()->Unit = {}): ErdMenu = init(ErdMenu(), block)
