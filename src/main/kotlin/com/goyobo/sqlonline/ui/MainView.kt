package com.goyobo.sqlonline.ui

import com.github.mvysny.karibudsl.v10.*
import com.goyobo.sqlonline.data.Diagram
import com.goyobo.sqlonline.data.ErdFileManager
import com.goyobo.sqlonline.ui.erd.*
import com.vaadin.flow.component.Key
import com.vaadin.flow.component.UI
import com.vaadin.flow.component.dependency.CssImport
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.html.Anchor
import com.vaadin.flow.component.html.Image
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.Route
import com.vaadin.flow.server.InputStreamFactory
import com.vaadin.flow.server.PWA
import com.vaadin.flow.server.StreamResource
import guru.nidi.graphviz.engine.Format
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream

@Route("")
@PWA(name = "Project Base for Vaadin", shortName = "Project Base")
@CssImport.Container(
    value = [
        CssImport("./styles/shared-styles.css")
    ]
)
class MainView : KComposite() {
    private val tableData = arrayListOf<Diagram.Table>()

    private lateinit var tableGrid: TableGrid
    private lateinit var columnGrid: ColumnGrid
    private lateinit var diagramContainer: VerticalLayout
    private lateinit var downloader: Anchor

    private var selectedTable: Diagram.Table? = null

    private val erdMenuListener = object : ErdMenuListener {
        override fun new() {
            clearGrids()
            repaint()
        }

        override fun import(inputStream: InputStream) {
            clearGrids()
            tableData.addAll(ErdFileManager.Import(inputStream).tableData)
            repaint()
        }

        override fun export() {
            // TODO UI input for filename
            downloader.setHref(StreamResource("filename.yaml", InputStreamFactory {
                ByteArrayInputStream(ErdFileManager.Export(tableData).toYaml().toByteArray())
            }))
            UI.getCurrent().page.executeJs("$0.click()", downloader.element)
        }
    }

    private val tableGridListener = object : TableGridListener<Diagram.Table> {
        override fun select(table: Diagram.Table?) {
            if (table != null) {
                columnGrid.setColumns(table.columns)
            } else {
                columnGrid.clearGrid()
            }

            selectedTable = table
        }

        override fun delete(table: Diagram.Table) {
            tableData.remove(table)
            repaint()
        }
    }

    private val columnGridListener = object : ColumnGridListener<Diagram.Column> {
        override fun delete(column: Diagram.Column) {
            val table = tableData[tableData.indexOf(column.table)]
            table.columns.remove(column)
            repaint()
        }
    }

    @Suppress("unused")
    private val root = ui {
        horizontalLayout {
            setSizeFull()

            verticalLayout {
                setSizeFull()

                erdMenu(erdMenuListener)
                button("Add table", VaadinIcon.PLUS.create()) {
                    addClickListener { promptAddTable() }
                }
                tableGrid = tableGrid(tableData, tableGridListener)
                button("Add column", VaadinIcon.PLUS.create())
                columnGrid = columnGrid(columnGridListener)
            }
            diagramContainer = verticalLayout {
                add(previewImage())
            }
            downloader = anchor {
                style["display"] = "none"
            }
        }
    }

    init {
        loadSampleData()
    }

    private fun promptAddTable() {
        Dialog().apply {
            isCloseOnOutsideClick = false
            val tableName = textField("Table name") {
                isRequired = true
                focus()
            }
            horizontalLayout {
                button("Add table") {
                    setPrimary()
                    addClickShortcut(Key.ENTER)
                    onLeftClick {
                        if (tableName.value.isEmpty()) {
                            tableName.errorMessage = "Required"
                            tableName.isInvalid = true
                        } else {
                            addTable(tableName.value)
                            close()
                        }
                    }
                }
                button("Cancel") {
                    onLeftClick {
                        close()
                    }
                }
            }
            open()
        }
    }

    private fun addTable(name: String) {
        tableData.add(Diagram.Table(name))
        repaint()
    }

    private fun clearGrids() {
        columnGrid.clearGrid()
        tableData.clear()
    }

    // Request UI repaint
    private fun repaint() {
        tableGrid.refresh()
        columnGrid.refresh()
        // TODO only repaint when "Auto reload preview" enabled
        with(diagramContainer) {
            removeAll()
            add(previewImage())
        }
    }

    private fun previewImage(): Image {
        val diagram = Diagram(tableData)
        val baos = ByteArrayOutputStream()
        diagram.graph().render(Format.SVG).toOutputStream(baos)

        val resource = StreamResource("test.svg", InputStreamFactory {
            ByteArrayInputStream(baos.toByteArray())
        })

        return Image(resource, "test.svg")
    }

    private fun loadSampleData() {
        val inputStream = javaClass.classLoader.getResourceAsStream("sample/sample.yaml")
        tableData.addAll(ErdFileManager.Import(inputStream).tableData)
        repaint()
    }
}
