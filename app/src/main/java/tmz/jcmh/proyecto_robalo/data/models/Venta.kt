package tmz.jcmh.proyecto_robalo.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

//ETIQUETAS DE ROOM EN DESUSO
@Entity(tableName = "Ventas")
data class Venta(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "idVenta")val id: Int?,
    @ColumnInfo(name = "Total")val total: Double,
    @ColumnInfo(name = "fecha")val fecha: String,
)
