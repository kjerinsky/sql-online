package com.goyobo.sqlonline

import com.github.mvysny.karibudsl.v10.*
import com.goyobo.sqlonline.erd.Diagram
import com.goyobo.sqlonline.erd.TableGridListener
import com.goyobo.sqlonline.erd.erdMenu
import com.goyobo.sqlonline.erd.tableGrid
import com.vaadin.flow.component.Key
import com.vaadin.flow.component.dependency.CssImport
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.html.Image
import com.vaadin.flow.component.icon.VaadinIcon
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
    private lateinit var columnGrid: Grid<Diagram.Column>

    private val tableGridListener = object : TableGridListener<Diagram.Table> {
        override fun selected(bean: Diagram.Table) {
            columnGrid.setItems(bean.columns)
        }
    }

    @Suppress("unused")
    private val root = ui {
        horizontalLayout {
            setSizeFull()

            verticalLayout {
                setSizeFull()

                erdMenu()
                button("Add table", VaadinIcon.PLUS.create()) {
                    addClickListener { promptAddTable() }
                }
                tableGrid(tableGridListener)
                columnGrid = grid {
                    setSizeFull()
                    addColumnFor(Diagram.Column::name)
                    addColumnFor(Diagram.Column::type)
                    addColumnFor(Diagram.Column::primaryKey)
                }
            }
            verticalLayout {
                add(previewImage())
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
        println(name)
    }

    private fun previewImage(): Image {
        val diagram = Diagram(SampleData.data)
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
