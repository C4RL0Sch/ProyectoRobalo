package tmz.jcmh.proyecto_robalo.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import tmz.jcmh.proyecto_robalo.data.models.Producto
import tmz.jcmh.proyecto_robalo.data.models.Usuario

@Dao
interface IUsuarioDAO {
    @Query("SELECT * FROM Usuarios")
    fun getAll(): LiveData<List<Usuario>>

    @Query("SELECT * FROM Usuarios")
    fun getAllNow():List<Usuario>

    @Query("SELECT * FROM Usuarios WHERE idUsuario = :id")
    suspend fun getById(id:Int):Usuario

    @Query("SELECT * FROM Usuarios WHERE Usuario = :usuario")
    suspend fun getByUser(usuario:String):Usuario

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(usuario: Usuario)

    @Query("SELECT COUNT(*) FROM Usuarios WHERE Usuario = :usuario")
    suspend fun CountByUser(usuario: String): Int

    @Update
    suspend fun Update(usuario: Usuario)

    @Delete
    suspend fun Delete(usuario: Usuario)
}