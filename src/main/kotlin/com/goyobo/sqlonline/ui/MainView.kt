package com.goyobo.sqlonline.ui

import com.github.mvysny.karibudsl.v10.*
import com.goyobo.sqlonline.Configuration
import com.goyobo.sqlonline.data.*
import com.goyobo.sqlonline.ui.erd.*
import com.vaadin.componentfactory.EnhancedDialog
import com.vaadin.flow.component.Composite
import com.vaadin.flow.component.Key
import com.vaadin.flow.component.UI
import com.vaadin.flow.component.dependency.CssImport
import com.vaadin.flow.component.html.Anchor
import com.vaadin.flow.component.html.Image
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.notification.Notification
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
        CssImport("./styles/shared-styles.css"),
        CssImport("./styles/custom-form-item.css", themeFor = "vaadin-form-item")
    ]
)
class MainView(config: Configuration) : KComposite() {
    private val erdData = ErdData()

    private lateinit var tableGrid: TableGrid
    private lateinit var columnGrid: ColumnGrid
    private lateinit var diagramContainer: VerticalLayout
    private lateinit var downloader: Anchor

    private var selectedTable: ErdTable? = null
    private var currentDialog: Composite<EnhancedDialog>? = null

    private val erdMenuListener = object : ErdMenuListener {
        override fun new() {
            clearGrids()
            repaint()
        }

        override fun import(inputStream: InputStream) {
            clearGrids()
            ErdFileManager.Import(inputStream, erdData)
            repaint()
        }

        override fun export() {
            // TODO UI input for filename
            downloader.setHref(StreamResource("filename.yaml", InputStreamFactory {
                ByteArrayInputStream(ErdFileManager.Export(erdData.tableCollection()).toYaml().toByteArray())
            }))
            UI.getCurrent().page.executeJs("$0.click()", downloader.element)
        }
    }

    private val tableGridListener = object : TableGridListener<ErdTable> {
        override fun select(table: ErdTable?) {
            if (table != null) {
                columnGrid.setColumns(table.columnCollection())
            } else {
                columnGrid.clearGrid()
            }

            selectedTable = table
        }

        override fun edit(table: ErdTable) {
            openEditTable(table)
        }

        override fun delete(table: ErdTable) {
            erdData.deleteTable(table)
            repaint()
        }
    }

    private val columnGridListener = object : ColumnGridListener<ErdColumn> {
        override fun edit(column: ErdColumn) {
            openEditColumn(column)
        }

        override fun delete(column: ErdColumn) {
            erdData.deleteColumn(column)
            repaint()
        }
    }

    private val tableDialogListener = object : TableDialogListener {
        override fun createTable(table: ErdTable) {
            erdData.createTable(table)
            repaint()
        }

        override fun updateTable() {
            repaint()
        }
    }

    private val columnDialogListener = object : ColumnDialogListener {
        override fun createColumn(column: ErdColumn) {
            erdData.createColumn(column)
            repaint()
        }

        override fun updateColumn() {
            repaint()
        }
    }

    @Suppress("unused")
    private val root = ui {
        horizontalLayout {
            setSizeFull()

            verticalLayout {
                width = "1500px"
                setHeightFull()

                erdMenu(erdMenuListener)
                button("Add (T)able", VaadinIcon.PLUS.create()) {
                    addClickShortcut(Key.KEY_T)
                    addClickListener { openCreateTable() }
                }
                tableGrid = tableGrid(erdData.tableCollection(), tableGridListener)
                button("Add (C)olumn", VaadinIcon.PLUS.create()) {
                    addClickShortcut(Key.KEY_C)
                    addClickListener { openCreateColumn() }
                }
                columnGrid = columnGrid(columnGridListener)
            }
            diagramContainer = verticalLayout {
                setSizeFull()
                add(previewImage())
            }
            downloader = anchor {
                style["display"] = "none"
            }
        }
    }

    init {
        loadSampleData()

        if (config.showAbout) {
            AboutDialog()
        }
    }

    private fun openCreateTable() {
        if (!dialogIsOpened()) {
            currentDialog = TableDialog(ErdTable(""), tableDialogListener, EntityMode.CREATE)
        }
    }

    private fun openEditTable(table: ErdTable) {
        currentDialog = TableDialog(table, tableDialogListener, EntityMode.UPDATE)
    }

    private fun openCreateColumn() {
        if (!dialogIsOpened()) {
            if (selectedTable != null) {
                currentDialog = ColumnDialog(
                    ErdColumn("", table = selectedTable!!),
                    columnDialogListener,
                    EntityMode.CREATE,
                    erdData
                )
            } else {
                Notification.show("Select a table to add a column")
            }
        }
    }

    private fun openEditColumn(column: ErdColumn) {
        currentDialog = ColumnDialog(column, columnDialogListener, EntityMode.UPDATE, erdData)
    }

    private fun dialogIsOpened() = (currentDialog != null && currentDialog?.content!!.isOpened)

    private fun clearGrids() {
        columnGrid.clearGrid()
        erdData.clear()
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
        val diagram = Diagram(erdData.tableCollection())
        val baos = ByteArrayOutputStream()
        diagram.graph().render(Format.SVG).toOutputStream(baos)

        val resource = StreamResource("test.svg", InputStreamFactory {
            ByteArrayInputStream(baos.toByteArray())
        })

        return Image(resource, "test.svg").apply {
            setSizeFull()
        }
    }

    private fun loadSampleData() {
        val inputStream = javaClass.classLoader.getResourceAsStream("sample/sample.yaml")
        ErdFileManager.Import(inputStream, erdData)
        repaint()
    }
}
