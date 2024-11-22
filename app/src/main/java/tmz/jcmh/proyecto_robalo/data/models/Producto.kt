package tmz.jcmh.proyecto_robalo.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Productos")
data class Producto(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name="idProducto") var id: Int? = null,
    @ColumnInfo(name="Codigo") var Codigo: String, //AGREGAR AL REPORTE
    @ColumnInfo(name="Nombre") var Nombre: String,
    @ColumnInfo(name="Presentación") var Presentacion: String,
    @ColumnInfo(name="Precio") var Precio: Double,
    @ColumnInfo(name="Cantidad")var Cantidad: Int,
)
