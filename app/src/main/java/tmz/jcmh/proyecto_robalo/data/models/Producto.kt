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
){
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false

        other as Producto

        return Codigo == other.Codigo &&
                Nombre == other.Nombre &&
                Presentacion == other.Presentacion &&
                Precio == other.Precio &&
                Cantidad == other.Cantidad
    }

    override fun hashCode(): Int {
        return Codigo.hashCode() * 31 +
                Nombre.hashCode() * 31 +
                Presentacion.hashCode() * 31 +
                Precio.hashCode() * 31 +
                Cantidad.hashCode() * 31
    }
}
