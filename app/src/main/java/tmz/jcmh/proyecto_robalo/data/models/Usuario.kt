package tmz.jcmh.proyecto_robalo.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "Usuarios")
data class Usuario (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name="idUsuario") var id: Int? = null,
    @ColumnInfo(name="Nombre") var Nombre: String,
    @ColumnInfo(name="ApellidoP") var ApellidoP: String,
    @ColumnInfo(name="ApellidoM") var ApellidoM: String,
    @ColumnInfo(name="Usuario") var Usuario: String,
    @ColumnInfo(name="Password") var Password: String,
    @ColumnInfo(name="Puesto") var Puesto: Int,
)