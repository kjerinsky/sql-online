package com.goyobo.sqlonline.ui.erd

import com.flowingcode.vaadin.addons.fontawesome.FontAwesome
import com.github.mvysny.karibudsl.v10.*
import com.vaadin.flow.component.HasComponents
import com.vaadin.flow.component.contextmenu.MenuItem
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.upload.Upload
import com.vaadin.flow.component.upload.receivers.MemoryBuffer
import java.io.InputStream
import java.io.Serializable

class ErdMenu(private val listener: ErdMenuListener) : KComposite() {
    private lateinit var refreshButton: MenuItem

    @Suppress("unused")
    private val root = ui {
        horizontalLayout {
            menuBar {
                item("File") {
                    item("New") {
                        addComponentAsFirst(FontAwesome.Regular.FILE.create())
                        addClickListener { listener.new() }
                    }
                    subMenu.add(hr())
                    item("Import Data") {
                        addComponentAsFirst(FontAwesome.Solid.FILE_IMPORT.create())
                        addClickListener { promptUpload() }
                    }
                    item("Export Data") {
                        addComponentAsFirst(FontAwesome.Solid.FILE_EXPORT.create())
                        addClickListener { listener.export() }
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

    private fun promptUpload() {
        val dialog = Dialog().apply {
            open()
        }
        val buffer = MemoryBuffer()
        val upload = Upload(buffer).apply {
            setAcceptedFileTypes("application/x-yaml", ".yaml")
            addSucceededListener {
                dialog.close()
//                val content = buffer.inputStream.bufferedReader().use(BufferedReader::readText)
//                println(buffer.inputStream.bufferedReader().use(BufferedReader::readText))
                listener.import(buffer.inputStream)
            }
        }
        dialog.add(upload)
    }

    fun isAutoReloadEnabled(): Boolean = !refreshButton.isEnabled
}

interface ErdMenuListener : Serializable {
    fun new()

    //    fun reload()
    fun import(inputStream: InputStream)
    fun export()
}

@VaadinDsl
fun (@VaadinDsl HasComponents).erdMenu(
    listener: ErdMenuListener,
    block: (@VaadinDsl ErdMenu).() -> Unit = {}
): ErdMenu =
    init(ErdMenu(listener), block)
