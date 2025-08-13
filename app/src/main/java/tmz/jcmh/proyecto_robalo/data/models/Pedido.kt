package tmz.jcmh.proyecto_robalo.data.models

import java.util.Date

data class Pedido(
    var idPedido: String = "",
    var idCliente: String = "",
    var Productos: MutableMap<String, Producto> = mutableMapOf(),
    var Fecha: Date = Date(),
    var Estado: String = "",
    var Total: Double = 0.0
)
