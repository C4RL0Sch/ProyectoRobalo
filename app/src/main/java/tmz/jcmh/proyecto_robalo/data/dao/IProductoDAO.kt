package tmz.jcmh.proyecto_robalo.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import tmz.jcmh.proyecto_robalo.data.models.Producto
// USADO PARA EL FUNCIONAMIENTO DE LA APLICACIÓN CON ROOM Y SQLITE
// EN DESUSO AL IMPLEMENTAR FIREBASE
@Dao
interface IProductoDAO {

    @Query("SELECT * FROM Productos")
    fun getAll():LiveData<List<Producto>>

    @Query("SELECT * FROM Productos")
    fun getAllNow():List<Producto>

    //TODO Colocar este metodo en el reporte
    @Query("SELECT * FROM Productos WHERE idProducto = :id")
    suspend fun getById(id:Int):Producto

    //TODO Colocar este metodo en el reporte
    @Query("SELECT * FROM Productos WHERE Codigo = :codigo")
    suspend fun getByCode(codigo:String):Producto

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(productos:List<Producto>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(producto: Producto)

    //TODO Colocar este metodo en el reporte
    @Query("SELECT COUNT(*) FROM Productos WHERE Codigo = :codigo")
    suspend fun CountByCodigo(codigo: String): Int

    @Update
    suspend fun UpdateAll(productos:List<Producto>)

    @Update
    suspend fun Update(producto:Producto)

    @Delete
    suspend fun DeleteAll(productos: List<Producto>)

    @Delete
    suspend fun Delete(producto: Producto)

}
