package com.goyobo.sqlonline.ui

import com.github.mvysny.karibudsl.v10.*
import com.goyobo.sqlonline.data.Diagram
import com.goyobo.sqlonline.data.ErdExport
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

    private val erdMenuListener = object : ErdMenuListener {
        override fun new() {
            tableData.clear()
            repaint()
        }

        override fun export() {
            // TODO UI input for filename
            downloader.setHref(StreamResource("filename.yml", InputStreamFactory {
                ByteArrayInputStream(ErdExport(tableData).toYml().toByteArray())
            }))
            UI.getCurrent().page.executeJs("$0.click()", downloader.element)
        }
    }

    private val tableGridListener = object : TableGridListener<Diagram.Table> {
        override fun select(bean: Diagram.Table) {
            columnGrid.setColumns(bean.columns)
        }

        override fun delete(bean: Diagram.Table) {
            tableData.remove(bean)
            repaint()
        }
    }

    private val columnGridListener = object : ColumnGridListener<Diagram.Column> {
        override fun delete(bean: Diagram.Column) {
            val table = tableData[tableData.indexOf(bean.table)]
            table.columns.remove(bean)
            repaint()
        }
    }

    @Suppress("unused")
    private val root = ui {
        tableData.addAll(SampleData.data)

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
}

object SampleData {
    val data: ArrayList<Diagram.Table>

    init {
        val users = Diagram.Table("users")
        val addresses = Diagram.Table("addresses")
        val orders = Diagram.Table("orders")
        val items = Diagram.Table("items")

        val uId = Diagram.Column("user_id", "bigint", users, primaryKey = true)
        val uFirstName = Diagram.Column("first_name", "varchar(50)", users)
        val uLastName = Diagram.Column("last_name", "varchar(50)", users)
        val uAddress = Diagram.Column("address_id", "bigint", users)
        val uEmail = Diagram.Column("email", "varchar(50)", users)

        val aId = Diagram.Column("address_id", "bigint", addresses, primaryKey = true)
        val aStreet = Diagram.Column("street", "varchar(50)", addresses)
        val aCity = Diagram.Column("city", "varchar(50)", addresses)

        val oId = Diagram.Column("order_id", "bigint", orders, primaryKey = true)
        val oUser = Diagram.Column("user_id", "bigint", orders)
        val oAddress = Diagram.Column("address_id", "bigint", orders)
        val oItem = Diagram.Column("item_id", "bigint", orders)

        val iId = Diagram.Column("item_id", "bigint", items, primaryKey = true)
        val iDescription = Diagram.Column("description", "text", items)

        users.columns.addAll(listOf(uId, uFirstName, uLastName, uAddress, uEmail))
        addresses.columns.addAll(listOf(aId, aStreet, aCity))
        orders.columns.addAll(listOf(oId, oUser, oAddress, oItem))
        items.columns.addAll(listOf(iId, iDescription))

        uAddress.foreignKey = aId
        oUser.foreignKey = uId
        oAddress.foreignKey = aId
        oItem.foreignKey = iId

        data = arrayListOf(users, addresses, orders, items)
    }
}
