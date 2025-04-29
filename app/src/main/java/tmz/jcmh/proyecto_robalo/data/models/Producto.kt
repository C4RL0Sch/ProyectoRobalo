package tmz.jcmh.proyecto_robalo.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

data class Producto(
    var Codigo: String? = null,
    var Nombre: String? = null,
    var Marca: String? = null,
    var Categoria: String? = null,
    var Presentacion: String ? = null,
    var Medida: String ? = null,
    var Precio: Double ? = 0.0,
    var Cantidad: Double ? = 0.0,
){
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false

        other as Producto

        return Codigo == other.Codigo &&
                Nombre == other.Nombre &&
                Marca == other.Marca &&
                Categoria == other.Categoria &&
                Presentacion == other.Presentacion &&
                Medida == other.Medida &&
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
