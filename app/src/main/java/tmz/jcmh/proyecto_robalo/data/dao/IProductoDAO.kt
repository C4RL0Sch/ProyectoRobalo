package tmz.jcmh.proyecto_robalo.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import tmz.jcmh.proyecto_robalo.data.models.Producto

@Dao
interface IProductoDAO {

    @Query("SELECT * FROM Productos")
    suspend fun getAll():List<Producto>


    //TODO Colocar este metodo en el reporte
    @Query("SELECT * FROM Productos WHERE idProducto = :id")
    suspend fun getById(id:Int):Producto

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(productos:List<Producto>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(producto: Producto)

    @Update
    suspend fun UpdateAll(productos:List<Producto>)

    @Update
    suspend fun Update(producto:Producto)

    @Delete
    suspend fun DeleteAll(productos: List<Producto>)

    @Delete
    suspend fun Delete(producto: Producto)

}
