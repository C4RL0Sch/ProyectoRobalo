package tmz.jcmh.proyecto_robalo.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import tmz.jcmh.proyecto_robalo.data.dao.IProductoDAO
import tmz.jcmh.proyecto_robalo.data.models.Producto

@Database(entities =[Producto::class], version = 1)
abstract class Database: RoomDatabase() {
    abstract val daoProductos: IProductoDAO
}
