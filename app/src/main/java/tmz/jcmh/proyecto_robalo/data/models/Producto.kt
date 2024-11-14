package tmz.jcmh.proyecto_robalo.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Productos")
data class Producto(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name="idProducto") val id: Int? = null,
    @ColumnInfo(name="Nombre") val Nombre: String,
    @ColumnInfo(name="Presentación")val Presentacion: String,
    @ColumnInfo(name="Precio")val Precio: Double,
    @ColumnInfo(name="Cantidad")val Cantidad: Int,
)
