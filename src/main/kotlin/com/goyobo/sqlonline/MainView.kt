package com.goyobo.sqlonline

import com.goyobo.sqlonline.erd.Diagram
import com.vaadin.flow.component.html.Image
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.Route
import com.vaadin.flow.server.InputStreamFactory
import com.vaadin.flow.server.PWA
import com.vaadin.flow.server.StreamResource
import guru.nidi.graphviz.engine.Format
import guru.nidi.graphviz.engine.Graphviz
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

@Route("")
@PWA(name = "Project Base for Vaadin", shortName = "Project Base")
class MainView : VerticalLayout() {
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

        val diagram = Diagram(arrayListOf(users, addresses, orders, items))
        val raw = diagram.test()
//        println(raw)

        test(raw)
    }

    fun test(raw: String) {
        val baos = ByteArrayOutputStream()
        Graphviz.fromString(raw).render(Format.SVG).toOutputStream(baos)
        val resource = StreamResource("test.svg", InputStreamFactory {
            ByteArrayInputStream(baos.toByteArray())
        })

        val image = Image(resource, "test.svg")

        add(image)
    }

}
