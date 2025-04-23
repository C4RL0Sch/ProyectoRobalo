package tmz.jcmh.proyecto_robalo.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
//ETIQUETAS DE ROOM EN DESUSO
@Entity(
    tableName = "Producto_has_Venta",
    foreignKeys = [
        ForeignKey(
            entity = Venta::class,
            parentColumns = ["idVenta"],
            childColumns = ["idVenta"],
            onDelete = ForeignKey.SET_NULL,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Producto::class,
            parentColumns = ["idProducto"],
            childColumns = ["idProducto"],
            onDelete = ForeignKey.SET_NULL,
            onUpdate = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["idVenta"]), Index(value = ["idProducto"])]
)
data class ProductoVenta(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "idProductoVenta") val id: Int? = null,
    @ColumnInfo(name = "idVenta") val idVenta: Int,
    @ColumnInfo(name = "idProducto") val idProducto: Int,
    @ColumnInfo(name = "cantidad") val cantidad: Int,
    @ColumnInfo(name = "precioUnitario") val precioUnitario: Double,
    @ColumnInfo(name = "subtotal") val subtotal: Double
)
