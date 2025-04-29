package tmz.jcmh.proyecto_robalo.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

data class Usuario (
    var Usuario: String? = null,
    var Nombre: String? = null,
    var ApellidoP: String? = null,
    var ApellidoM: String? = null,
    var Password: String? = null,
    var Puesto: Int? = null,
)